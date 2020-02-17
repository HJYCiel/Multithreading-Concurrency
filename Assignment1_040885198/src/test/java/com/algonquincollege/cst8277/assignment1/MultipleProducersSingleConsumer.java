/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: MultipleProducersSingleConsumer.java
 * Course materials (19F) CST 8277
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.assignment1;

import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algonquincollege.cst8277.assignment1.BlockingQueueBuffer;
import com.algonquincollege.cst8277.assignment1.Buffer;
import com.algonquincollege.cst8277.assignment1.CircularBuffer;
import com.algonquincollege.cst8277.assignment1.TicketConsumer;
import com.algonquincollege.cst8277.assignment1.TicketProducer;
import com.algonquincollege.cst8277.assignment1.HibernateHelper;
import com.algonquincollege.cst8277.assignment1.LoggingOutputStream.LogLevel;
import com.algonquincollege.cst8277.assignment1.model.Ticket;

/**
 *
 * <b>Description</b></br></br>
 * Driver class that demonstrates the Multiple-Producers-&-Single-Consumer solution using two different buffer implementations
 *
 * @date  (modified) 2019 09
 *
 * @author Jiaying Huang 040-885-198
 *
 */
public class MultipleProducersSingleConsumer{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected static final int FIRST_AWAIT_TERMINATION_TIME_MINUTES = 3;
    protected static final int SECOND_AWAIT_TERMINATION_TIME_MINUTES = 1;
    protected static final String CIRCULAR_BUFTYPE = "c";
    protected static final String BLOCKING_BUFTYPE = "b";
    protected static final String USE_CIRCULAR_BUFFER_MSG = "Using Circular Buffer";
    protected static final String USE_BLOCKING_BUFFER_MSG = "Using Blocking Buffer";
    protected static final String CMDLINE_PARSING_ERROR_MSG = "cmdLine parsing error: {}";
    protected static final String COMPLETABLE_ERROR_MSG = "Completable execution error: {}";
    protected static final String TOTAL_TIME_MSG = "Total time = {} ms";

    public static void main(String[] args) {

        CmdLineParser cmdLineParser = null;
        CmdLineOptions cmdLineOptions = new CmdLineOptions();
        try {
            ParserProperties parserProperties = ParserProperties
                .defaults()
                .withOptionSorter(null)
              //.withUsageWidth(100)
            ;
            cmdLineParser = new CmdLineParser(cmdLineOptions, parserProperties);
            cmdLineParser.parseArgument(args);
        }
        catch (CmdLineException e) {
            // if there's a problem in the command line, you'll get this exception
            // this will report an error message
            logger.error(CMDLINE_PARSING_ERROR_MSG, e.getLocalizedMessage());
            LoggingOutputStream los = new LoggingOutputStream(logger, LogLevel.ERROR);
            logCmdLineUsage(cmdLineParser, los);
            System.exit(-1);
        }
        if (cmdLineOptions.help) {
            LoggingOutputStream los = new LoggingOutputStream(logger, LogLevel.INFO);
            logCmdLineUsage(cmdLineParser, los);
            return;
        }

        Buffer<Optional<Ticket>> threadSafeBuffer = null;
        if (BLOCKING_BUFTYPE.equalsIgnoreCase(cmdLineOptions.bufType)) {
            logger.info(USE_BLOCKING_BUFFER_MSG);
            threadSafeBuffer = new BlockingQueueBuffer<>(cmdLineOptions.bufSize);
        }
        else if (CIRCULAR_BUFTYPE.equalsIgnoreCase(cmdLineOptions.bufType)) {
            logger.info(USE_CIRCULAR_BUFFER_MSG);
            threadSafeBuffer = new CircularBuffer<>(cmdLineOptions.bufSize);//--s in configuration to see the buffer size
        }
        
        //TODO - build a consumer thread, then build all the producer threads
       
        
        Instant startTime = Instant.now();
        //build a consumer pool, start TicketConsumer
        ExecutorService consumerPool = Executors.newCachedThreadPool();
        /**Credit for prof. Jamil Dimassi in helping complete the coding for excution in consumer pool**/
                TicketConsumer ticketConsumer = new TicketConsumer(threadSafeBuffer,HibernateHelper.INSTANCE);
                consumerPool.execute(ticketConsumer);

        //build a producer pool, start required number of TicketProducers
        ExecutorService producerPool = Executors.newCachedThreadPool();
        List<Callable<Void>> producerCallables = new ArrayList<>();
        // need to divide work across producer threads
        int workLeftTodo = cmdLineOptions.numberOfTickets;
        int workPerProducerThread = workLeftTodo / cmdLineOptions.numberOProducerThreads;
        for (int idx = 1, lastLoop = cmdLineOptions.numberOProducerThreads, stop = lastLoop + 1 ; idx < stop; idx++) {          
            int numTicketsToProducePerProducerThread = workPerProducerThread;
            // last thread does whatever work there is left to do (handles odd num threads, odd num tickets)
            if (idx == lastLoop) {
                numTicketsToProducePerProducerThread = workLeftTodo;
            }
            TicketProducer ticketProducer = new TicketProducer(threadSafeBuffer, numTicketsToProducePerProducerThread);
            Callable<Void> callable = Executors.callable(ticketProducer,null);
            producerCallables.add(callable);
            workLeftTodo -= numTicketsToProducePerProducerThread;
        }
        // wait for all TicketProducer callables to be done
        try {
            List<Future<Void>> futures = producerPool.invokeAll(producerCallables);
            //Note: why are we wrapping the futures? Java 8 Lambda's (the -> thingie) cannot handle checked Exceptions
            futures.stream().forEach(future -> wrapCheckedExceptionWithUnchecked(future::get).get());
            // indicate to TicketConsumer no more work to be done
            threadSafeBuffer.blockingPut(Optional.empty());
        }
        catch (Exception e) {
            logger.error(COMPLETABLE_ERROR_MSG, e.getLocalizedMessage());
        } 

        shutdownAndAwaitTermination(consumerPool);
        shutdownAndAwaitTermination(producerPool);

        Instant endTime = Instant.now();
        long elapsedTime = Duration.between(startTime, endTime).toMillis();
        logger.info(TOTAL_TIME_MSG, elapsedTime);
    }

    protected static void logCmdLineUsage(CmdLineParser cmdLineParser, LoggingOutputStream los) {
        // print the list of available options
        PrintWriter pw = new PrintWriter(los);
        pw.println("\nUsage:");
        pw.flush();
        cmdLineParser.printUsage(los);
        los.line();
    }

    //copied almost verbatim from ExecutorService javadocs
    protected static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(FIRST_AWAIT_TERMINATION_TIME_MINUTES, TimeUnit.MINUTES)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(SECOND_AWAIT_TERMINATION_TIME_MINUTES, TimeUnit.MINUTES))
                    logger.error("thread pool {} did not properly terminate", pool.toString());
            }
        }
        catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    //see note above re: checked Exceptions and Lambda's
    @FunctionalInterface
    interface CheckedExceptionSupplier<X> {
        X get() throws Throwable;
    }
    static <X> Supplier<X> wrapCheckedExceptionWithUnchecked(final CheckedExceptionSupplier<X> checkedExceptionSupplier) {
        return () -> {
            try {
                return checkedExceptionSupplier.get();
            }
            catch (final Throwable checked) {
                RuntimeException wrappedUncheckedException = new RuntimeException(checked);
                throw wrappedUncheckedException;
            }
        };
    }



}