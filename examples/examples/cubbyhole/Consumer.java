package examples.cubbyhole;
 
import static threadSketch.Thread2Sketch.threadPost;

/**
 *
 * @author Melvin S. Metzger
 */
public class Consumer implements Runnable {
    private Cubbyhole cubbyhole;
    private int number;
    
    public Consumer(Cubbyhole c, int number){
        cubbyhole = c;
        this.number = number;
    }
    
    public void run(){
        int value;
        for(int i  = 0 ; i < 10 ; i ++){
            value = cubbyhole.get();
            threadPost("Consumer #" + this. number + " got: " + value);
           // System.out.println("Consumer #" + this. number + " got: " + value);
        
        }
    }
}
