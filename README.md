# ThreadSketch
ThreadSketch Java Library to support learning multithreading by drawing an illustration of all active Threads and their interaction after runntime.
# Example
This example illustrates ThreadSketch on [The SimpleThreads Example](https://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html).
### ThreadSketch (output)
This ThreadSketch is the output of the [example code](#example-code) and illustrates the execution of the implemented Thread(s).
 ![picture alt](ThreadSketchExample.png "Example ThreadSketch")
### <a name="example-code"></a>Code 
```java

import threadSketch.ThreadSketcher;
import threadSketch.Thread2Sketch;

/**
 * This Example implements the Simple Threads Example found here:
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html
 * using ThreadSketch.
 */
public class Example { 

    private static class MessageLoopReporter implements Runnable {
        public void run() {
            String importantInfo[] = {
                    "Mares eat oats",
                    "Does eat oats",
                    "Little lambs eat ivy",
                    "A kid will eat ivy too"
            };
            try {
                for (int i = 0;
                     i < importantInfo.length;
                     i++) {
                    // Pause for 2 seconds
                    Thread2Sketch.sleep(2000);
                    // post a message in the TreadSketch
                    Thread2Sketch.threadPost(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                //post a message in ThreadSketch
                Thread2Sketch.threadPost("I wasn't done!");
            }
        }
    }

    public static void main(String args[])
            throws InterruptedException {

        // patience, in milliseconds before we interrupt MessageLoop thread
        long patience = 3000;
        long startTime = System.currentTimeMillis();

        //initialize a ThreadSketcher
        ThreadSketcher ts = new ThreadSketcher();

        MessageLoopReporter rn = new MessageLoopReporter();

        // creates a Thread2Sketch, assigned to a ThreadSketcher and implementing a given Runnable
        Thread2Sketch t = new Thread2Sketch(ts, rn);
        t.start();


        while (t.isAlive()) {
            //post a custom message in ThreadSketch
            Thread2Sketch.threadPost("Still waiting...\nfor thread:\n" + t.getName());

            // Wait maximum of 1 second
            // for MessageLoop thread to join
            t.join(1000);

            //interrupt when patience is exceeded
            if (((System.currentTimeMillis() - startTime) > patience)
                    && t.isAlive()) {

                //post a custom message in ThreadSketch before interrupting
                Thread2Sketch.threadPost("Tired of waiting...\nfor thread:\n" + t.getName());
                t.inthappened();
                // Shouldn't be long now
                // -- wait indefinitely
                t.join();
            }
        }

        //post a custom message in ThreadSketch when join was successful
        Thread2Sketch.threadPost("Finally!");


        //The single line of code that makes the ThreadSketcher sketch a ThreadSketch of everything that happened above
        ts.sketch();



    }
}
`````