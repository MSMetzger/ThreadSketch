package examples.cubbyhole;


import threadSketch.Thread2Sketch;
import threadSketch.ThreadSketcher;

/**
 *
 * @author Melvin S. Metzger
 */
public class ProducerConsumerTest {
    
    public static void main(String[] args) throws InterruptedException{
     
        
        ThreadSketcher ts = new ThreadSketcher();
        Cubbyhole c = new Cubbyhole();
        Consumer c1 = new Consumer(c, 1);
        Producer p1 = new Producer(c,1);
        Thread2Sketch pt1 = new Thread2Sketch(ts, p1);
        pt1.setName("producer");
        
        Thread2Sketch ct1 = new Thread2Sketch(ts, c1);
        ct1.setName("consumer");
        
        pt1.start();
        ct1.start();
        
        pt1.join();
        ct1.join();
        
        
      ts.sketch();  
        
    }
    
}
