/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: CircularBuffer.java
 * Course materials (19F) CST 8277
 * @author (original) Mike Norman, derived from code by Deitel & Associates, Inc.
 *         (Notes: Fig. 23.18: CircularBuffer.java Synchronizing access to a shared bounded buffer)
 *
 * (C) Copyright 1992-2015 by Deitel & Associates, Inc. and
 * Pearson Education,Inc.
 * All Rights Reserved.
 *
 * DISCLAIMER: The authors and publisher of this book have used their
 * best efforts in preparing the book. These efforts include the
 * development, research, and testing of the theories and programs to determine their
 * effectiveness. The authors and publisher make no warranty of any kind,
 * expressed or implied, with regard to these programs or to the
 * documentation contained in these books. The authors and publisher
 * shall not be liable in any event for incidental or
 * consequential damages in connection with, or arising out of, the
 * furnishing, performance, or use of these programs.
 *
 *************************************************************************/
package com.algonquincollege.cst8277.assignment1;

/**
 *
 * Description: Implements the {@link com.algonquincollege.cst8277.assignment1.Buffer} interface using a CircularBuffer </br>
 *
 * @date  (modified) 2019 09
 *
 * @author Jiaying Huang 040-885-198
 *
 * @param <E> the element type held in the buffer
 * @param writeIndexindex: of next element to write to
 * @param occupiedCells :count number of buffers used
 * @param readIndex: index of next element to read
 */
public class CircularBuffer<E> implements Buffer<E> {
    protected E[] bufArray;
    //TODO additional required member fields
    /**
     * Parameters get  from "Figure 23.18 in Paul J. Deitel, Harvey Deitel. (2017). Java How to Program, Early Objects (11th Edition) [Texidium version]. Retrieved from http://texidium.com"
     * **/
    private int occupiedCells = 0; 
    private int writeIndex = 0; 
   private int readIndex = 0; 

    /**
     * Constructor builds a buffer of the specified size
     * @param capacity
     */
    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        bufArray = (E[])new Object[capacity];// initialize @param bufArray
    }

    @Override
    /**
     * Add element to buffer (thread-safe); if no room, block
     * function modified based on "Figure 23.18 in Paul J. Deitel, Harvey Deitel. (2017). Java How to Program, Early Objects (11th Edition) [Texidium version]. Retrieved from http://texidium.com"
     * @param element
     */
    public synchronized void blockingPut(E element) throws InterruptedException {
        try {
    
     // wait until buffer has space available, then write value;
        // while no empty locations, place thread in blocked state
         while (occupiedCells == bufArray.length) {
//               System.out.printf("Buffer is full. Producer waits.%n");
                wait(); // wait until a buffer cell is free
         }
         bufArray[writeIndex] = element; // set new buffer value

         // update circular write index
       writeIndex = (writeIndex + 1) % bufArray.length;

       ++occupiedCells; // one more buffer cell is full
//       displayState("Producer writes " + element.toString());
         notifyAll(); // notify threads waiting to read from buffer
        }catch(RuntimeException e) {
        throw new RuntimeException("method not implemented:blockingPut in CircularBuffer");}
    }

    /**display current operation and buffer state
     * function modified based on "Figure 23.18 in Paul J. Deitel, Harvey Deitel. (2017). Java How to Program, Early Objects (11th Edition) [Texidium version]. Retrieved from http://texidium.com"
     * **/
    public synchronized void displayState(String operation) {
        // output operation and number of occupied buffer cells
        System.out.printf("%s%s%s)%n%s", operation,
          " (buffer cells occupied: ", occupiedCells, "buffer cells:  ");

//       for (E value : bufArray) {
//          System.out.printf(" %s  ", value.toString()); // output values in buffer
//        }
//       System.out.printf("%n               ");

//         for (int i = 0; i < bufArray.length; i++) {
//          System.out.printf("---- ");     }
//
//        System.out.printf("%n               ");
//
//      for (int i = 0; i < bufArray.length; i++) {
//           if (i == writeIndex && i == readIndex) {
//            System.out.printf(" WR"); // both write and read index
//          }
//           else if (i == writeIndex) {
//              System.out.printf(" W  "); // just write index
//            }
//           else if (i == readIndex) {
//              System.out.printf("  R  "); // just read index
//           }
//         else {
//              System.out.printf("  "); // neither index
//           }
//        }
//
//         System.out.printf("%n%n");
     }

    

    @Override
    /**
     * Remove element from buffer (thread-safe); if none, block
     *function modified based on "Figure 23.18 in Paul J. Deitel, Harvey Deitel. (2017). Java How to Program, Early Objects (11th Edition) [Texidium version]. Retrieved from http://texidium.com"
     * @return element
     */
    public synchronized E blockingGet() throws InterruptedException {
//        try {
  // wait until buffer has data, then read value;
   // while no data to read, place thread in waiting state
       while (occupiedCells == 0) {
//        System.out.printf("Buffer is empty. Consumer waits.%n");
              wait(); // wait until a buffer cell is filled
        }

     E readValue = bufArray[readIndex]; // read value from buffer

     // update circular read index
      readIndex = (readIndex + 1) % bufArray.length;
     --occupiedCells; // one fewer buffer cells are occupied
//    displayState("Consumer reads " + readValue.toString());
      notifyAll(); // notify threads waiting to write to buffer

      return readValue;
//        }catch(RuntimeException e){
//   
//        throw new RuntimeException("method not implemented: blockingGet in CircularBuffer");
//    }
}
}