/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: ContactConsumer.java
 * Course materials (19F) CST 8277
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.assignment1;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algonquincollege.cst8277.assignment1.model.Ticket;

/**
 *
 * <b>Description</b></br></br>
 * Implements the {@link com.algonquincollege.cst8277.assignment1.Consumer} interface </br>
 *
 * @date (modified) 2019 09
 *
 * @author Jiaying Huang 040-885-198
 *
 */
public class TicketConsumer implements Consumer<Optional<Ticket>> {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String SOMETHING_WENT_WRONG_CONSUMING = "something went wrong consuming";
    
    protected static final String START_MSG = "starting to consume tickets";
    protected static final String FINISH_MSG = "finished consuming tickets";
    // STATISTICS_MSG_FMT is a SLF4J format string (with "{}" formatting anchors) that
    // logs the number of elements consumed and the elapsed time @see https://www.slf4j.org/faq.html#string_contents
    final static String STATISTICS_MSG_FMT = "number of tickets consumed={} ({} ms)";

    protected Buffer<Optional<Ticket>> threadSafeBuffer;
    protected HibernateHelper hibernateHelper;

    public TicketConsumer(Buffer<Optional<Ticket>> threadSafeBuffer, HibernateHelper hibernateHelper) {
        this.threadSafeBuffer = threadSafeBuffer;
        this.hibernateHelper = hibernateHelper;
    }
    /*
     * public TicketConsumer(Buffer<Optional<Ticket>> threadSafeBuffer) {
     * this.threadSafeBuffer = threadSafeBuffer; }
     */

    /**
     * Work through buffer, storing {@link Ticket}'s into the database
     *
     * @return int number of Tickets saved to the database
     */
    @Override
    public int consume() {
        int numConsumed = 0;
        boolean done = false;
        logger.debug(START_MSG);
        Instant startTime = Instant.now();
        while (!done) {
            try {
                Optional<Ticket> oTicket = threadSafeBuffer.blockingGet();
                if (oTicket.isPresent()) {
                    Ticket ticket = oTicket.get();
                    logger.trace("consuming ticket {}", ticket.toString());
                    //TODO
                    /** Store the ticket in the hibernate and remove it from buffer (consumed)**/
                    hibernateHelper.saveTicket(ticket);
                    
                    numConsumed++;
                }
                else {
                    done = true;
                    //if there are other consumer threads, we should put the empty Optional back so they know to stop, too
                    //threadSafeBuffer.blockingPut(oTicket);
                    //but in this case, we know there is only 1 TicketConsumer 
                }
            }
            catch (InterruptedException e) {
                logger.error(SOMETHING_WENT_WRONG_CONSUMING, e);
            }
        }
        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
        logger.debug(FINISH_MSG);
        logger.info(STATISTICS_MSG_FMT, numConsumed, elapsedTime);
        return numConsumed;
    }

}