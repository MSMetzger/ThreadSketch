package examples.cubbyhole;

import static threadSketch.Thread2Sketch.threadPost;
import static threadSketch.Thread2Sketch.sleep;

/**
 *
 * @author Melvin S. Metzger
 */
public class Producer implements Runnable{
    private Cubbyhole cubbyhole;
    private int number;
    
    public Producer(Cubbyhole c, int number){
        this.cubbyhole = c;
        this.number = number;
        
    }
    
    public void run(){
        for(int i = 0; i < 10; i ++){
            cubbyhole.put(i);
            
            threadPost("Producer #" + this.number + " put: " + i);

            try{
                sleep((int) (Math.random() * 100));
            }catch(InterruptedException e){
        }
    }
}}
