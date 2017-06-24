package threadSketch;


 
import java.util.LinkedList;
import java.util.Queue;
/**
 * @author Melvin S. Metzger
 */
public class ThreadSketcher {




    private Queue<ThreadReport> reportQueue = new LinkedList<ThreadReport>();

    public  synchronized void receiveThreadReport(ThreadReport statusReport) {
        this.reportQueue.add(statusReport);
    }

    public ThreadSketcher() {
    }

    public String toString() {

        String printString = "";
        for (ThreadReport statusReport : reportQueue) {

            printString = printString + statusReport.toString() + "\n\n";
        }
        return (printString);

    }

    public void sketch() {
        ThreadSketch sketcher;
        sketcher = new ThreadSketch(reportQueue);
        sketcher.drawAll(reportQueue);
    }
}
