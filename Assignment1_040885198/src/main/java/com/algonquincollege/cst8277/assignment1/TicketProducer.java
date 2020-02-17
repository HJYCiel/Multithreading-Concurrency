/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: ContactProducer.java
 * Course materials (19W) CST 8277
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

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DefaultClassInfoStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
/**
 *
 * <b>Description</b></br></br>
 * Implements the {@link com.algonquincollege.cst8277.assignment1.Producer} interface </br>
 *
 * @date  (modified) 2019 09
 *
 * @author Jiaying Huang 040-885-198
 *
 */
public class TicketProducer implements Producer<Optional<Ticket>> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String SOMETHING_WENT_WRONG_CREATING_TICKET = 
        "something went wrong creating ticket or adding to buffer";

    protected static final String START_MSG = "starting to produce tickets";
    protected static final String FINISH_MSG = "finished producing tickets";
    // STATISTICS_MSG_FMT is a SLF4J format string (with "{}" formatting anchors) that
    // logs the number of elements produced and the elapsed time @see https://www.slf4j.org/faq.html#string_contents
    final static String STATISTICS_MSG_FMT = "number of tickets produced={} ({} ms)";

    protected Buffer<Optional<Ticket>> threadSafeBuffer;
    protected int numTicketsToProduce = 0;
    PodamFactory factory =  new PodamFactoryImpl(); //PODAM - POjo DAta Mocker

    public TicketProducer(Buffer<Optional<Ticket>> threadSafeBuffer, int numTicketsToProduce) {
        this.threadSafeBuffer = threadSafeBuffer;
        this.numTicketsToProduce = numTicketsToProduce;

        ClassInfoStrategy classInfoStrategy = factory.getClassStrategy();
        //no need to generate primary key (id), database will do that for us
        ((DefaultClassInfoStrategy)classInfoStrategy).addExcludedField(Ticket.class, "id");
        ((DefaultClassInfoStrategy)classInfoStrategy).addExcludedField(Ticket.class, "tstamp");
    }

    /**
     * Using POjo DAta Mocker (DOPAM), build random {@link Ticket}'s and add them to the buffer
     *
     * @return int number of random Tickets added to buffer
     */
    @Override
    public int produce() {
        int count = 0;
        logger.debug(START_MSG);
        Instant startTime = Instant.now();
        while (count < numTicketsToProduce) {
            try {
                Ticket t = buildNewRandomTicket();
                
                //TODO
               /**writes the ticket into buffer**/
                threadSafeBuffer.blockingPut(Optional.of(t));
                
                count++;
            }
            catch (InterruptedException e) {
                logger.error(SOMETHING_WENT_WRONG_CREATING_TICKET, e);
            }
        }
        long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
        logger.debug(FINISH_MSG);
        logger.info(STATISTICS_MSG_FMT, count, elapsedTime);
        return count;
    }

    /**
     * @return create random ticket
     */
    protected Ticket buildNewRandomTicket() {
        Ticket randomTicket = factory.manufacturePojoWithFullData(Ticket.class);
        return randomTicket;
    }
}