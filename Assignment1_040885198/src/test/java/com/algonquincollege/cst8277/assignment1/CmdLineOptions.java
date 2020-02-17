/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: CmdLineOptions.java
 * Course materials (19F) CST 8277
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.assignment1;

import static com.algonquincollege.cst8277.assignment1.MultipleProducersSingleConsumer.BLOCKING_BUFTYPE;

import org.kohsuke.args4j.Option;

/**
 *
 * <b>Description</b></br></br>
 * Helper class that holds annotated member fields that represent cmdLine args for {@link MultipleProducersSingleConsumer}
 *
 * @date 2019 09
 *
 * @author mwnorman
 *
 */
public class CmdLineOptions {

    protected static final int DEFAULT_BUFFER_SIZE = 20;
    protected static final int DEFAULT_PRODUCER_NUM = 4;
    protected static final int DEFAULT_TICKET_COUNT = 1000;
    protected static final String DASH = "-";
    protected static final String DASHDASH = DASH + DASH;
    protected static final String HELP_SHORTOPT = "h";
    protected static final String HELP_LONGOPT = "help";
    protected static final String HELP_USAGE = "print this message";
    protected static final String BUFFER_LONGOPT = "buffer";
    protected static final String BUFFER_USAGE = "b=BlockingQueue Buffer, c=Circular Buffer";
    protected static final String BUFFER_META = "<Buffer Type>";
    protected static final String SIZE_SHORTOPT = "s";
    protected static final String SIZE_LONGOPT = "size";
    protected static final String SIZE_USAGE = "buffer size";
    protected static final String SIZE_META = "<size>";
    protected static final String PRODUCER_NUM_SHORTOPT = "p";
    protected static final String PRODUCER_NUM_LONGOPT = "producer";
    protected static final String PRODUCER_NUM_USAGE = "number of producer threads";
    protected static final String PRODUCER_NUM_META = "<thread-count>";
    protected static final String TICKET_COUNT_SHORTOPT = "t";
    protected static final String TICKET_COUNT_LONGOPT = "tickets";
    protected static final String TICKET_COUNT_USAGE = "number of tickets";
    protected static final String TICKET_COUNT_META = "<count>";

    @Option(
        name = DASH + HELP_SHORTOPT,
        aliases = {DASHDASH + HELP_LONGOPT},
        help = true,
        usage = HELP_USAGE
    )
    public boolean help;

    @Option(
        required = true,
        name = DASH + BLOCKING_BUFTYPE,
        aliases= {DASHDASH + BUFFER_LONGOPT},
        usage = BUFFER_USAGE,
        metaVar = BUFFER_META
    )
    public String bufType;

    @Option(
        name = DASH + SIZE_SHORTOPT,
        aliases = {DASHDASH + SIZE_LONGOPT},
        usage = SIZE_USAGE,
        metaVar = SIZE_META
    )
    public int bufSize = DEFAULT_BUFFER_SIZE;

    @Option(
        name = DASH + PRODUCER_NUM_SHORTOPT,
        aliases = {DASHDASH + PRODUCER_NUM_LONGOPT},
        usage = PRODUCER_NUM_USAGE,
        metaVar = PRODUCER_NUM_META
    )
    public int numberOProducerThreads = DEFAULT_PRODUCER_NUM;

    @Option(
        name = DASH + TICKET_COUNT_SHORTOPT,
        aliases = {DASHDASH + TICKET_COUNT_LONGOPT},
        usage = TICKET_COUNT_USAGE,
        metaVar = TICKET_COUNT_META
    )
    public int numberOfTickets = DEFAULT_TICKET_COUNT;
    
}