package examples.cubbyhole;


/**
 *
 * @author Melvin S. Metzger
 */
public class Cubbyhole {

    private int contents;
    private boolean available = false;

    public synchronized int get() {
        try {
            while (available == false) {
                try {
                    wait();
                    
                } catch (InterruptedException e) {
                    
                }
            }
            available = false;
            
        } finally {
        }
        return contents;
        
    }

    public synchronized void put(int value) {
        try {
            while (available == true) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            contents = value;
            available = true;
            notifyAll();
            
        } finally {
        }
    }
}
