package examples.simplethreads;

/**
 *
 * @author Melvin S. Metzger
 */


import threadSketch.ThreadSketcher;
import threadSketch.Thread2Sketch;

public class SimpleThreads {

    

    private static class MessageLoopReporter
        implements Runnable {
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
                    // Pause for 4 seconds 
                    Thread2Sketch.sleep(4000);
                    // Print a message
                     Thread2Sketch.threadPost(importantInfo[i]);
                }
           } catch (InterruptedException e) {
              Thread2Sketch.threadPost("I wasn't done!");
            }
        }
    }

    public static void main(String args[])
        throws InterruptedException {

        // Delay, in milliseconds before
        // we interrupt MessageLoop
        // thread (default one hour).
        long patience = 100 * 30;

        long startTime = System.currentTimeMillis(); 
        
        ThreadSketcher ts = new ThreadSketcher();

        MessageLoopReporter rn = new MessageLoopReporter(); 
        Thread2Sketch t = new Thread2Sketch(ts, rn);
        t.start(); 
       
        Thread2Sketch t1 = new Thread2Sketch(ts, rn);
        t1.start();   
        
        
        Thread2Sketch t2 = new Thread2Sketch(ts, rn); 
        t2.start();   

        Thread2Sketch t3 = new Thread2Sketch(ts, rn); 
        t3.start();   

        
        Thread2Sketch.threadPost("Waiting for MessageLoop\nthread to finish");
        // loop until MessageLoop
        // thread exits
        
     while (t.isAlive()) {
            Thread2Sketch.threadPost("Still waiting...\nfor thread:\n" + t.getName());
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience)
                  && t.isAlive()) {
                Thread2Sketch.threadPost("Tired of waiting...\nfor thread:\n" + t.getName());
                t.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t.join();
            }
        }  
     
     
       
         while (t1.isAlive()) {
            Thread2Sketch.threadPost("Still waiting...\nfor thread:\n" + t1.getName());
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            
            t1.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience)
                  && t1.isAlive()) {
                Thread2Sketch.threadPost("Tired of waiting...\nfor thread:\n" + t1.getName());
                t1.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t1.join();
            }
        } 
         
        while (t2.isAlive()) {
            Thread2Sketch.threadPost("Still waiting...\nfor thread:\n" + t2.getName());
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            
            t2.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience)
                  && t2.isAlive()) {
                Thread2Sketch.threadPost("Tired of waiting...\nfor thread:\n" + t2.getName());
                t2.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t2.join();
            }
        } 
             
        while (t3.isAlive()) {
            Thread2Sketch.threadPost("Still waiting...\nfor thread:\n" + t3.getName());
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            
            t3.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience)
                  && t3.isAlive()) {
                Thread2Sketch.threadPost("Tired of waiting...\nfor thread:\n" + t3.getName());
                t3.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t3.join();
            }
        } 
         
      
        Thread2Sketch.threadPost("Finally!");

        ts.sketch();
        
         
        
    }
}
