/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: Producer.java
 * Course materials (19F) CST 8277
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.assignment1;

/**
 *
 * <b>Description</b></br></br>
 *
 * Producer interface and (new with Java 8+) default method code for the {@link #run()} method </br>
 * 
 * @author mwnorman
 * @date 2019 09
 *
 * @param <T> the element type processed by a Producer thread
 */
public interface Producer<T> extends Runnable {

    int produce();

    /**
     * This method is called when an instance of this {@link Runnable} to installed in a {@link Thread}
     *
     * @author mwnorman
     */
    default void run() {
        @SuppressWarnings("unused")
        int numProduced = produce();
    }

}