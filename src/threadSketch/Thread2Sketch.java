package threadSketch;


/**
 *
 * @author Melvin S. Metzger
 */
public class Thread2Sketch {


    private static ThreadSketcher missionControl;
    private Thread thread;


    private static  void sendStatusReport(String reportReason, Thread thread) {
        ThreadReport report = (new ThreadReport(reportReason, thread)); 
        missionControl.receiveThreadReport(report);
//        System.out.println("...status report sent...thread: " + thread.getId() +" count: " +  statusReportsCount);
    }


    public static void threadPost(String post) {
        ThreadReport report = (new ThreadReport(Thread.currentThread(), post)); 
        missionControl.receiveThreadReport(report); 
    }

    public String getName(){
        return(thread.getName());
    }

    public boolean isAlive(){
        return(thread.isAlive());
    }

    public static  Thread currentThread(){
        return(Thread.currentThread());
    }

    public Thread2Sketch(ThreadSketcher missionControl) {
        this.thread = new Thread();
        this.missionControl = missionControl;

        sendStatusReport(ThreadReport.REPORT_REASON_CONSTRUCTOR_END, thread);
    }

    public Thread2Sketch(ThreadSketcher missionControl, Runnable target) {
        this.thread = new Thread(target);
        this.missionControl = missionControl;

        sendStatusReport(ThreadReport.REPORT_REASON_CONSTRUCTOR_END, thread);
    }

    public Thread2Sketch(ThreadSketcher missionControl, String name) {
        this.thread = new Thread(name);
        this.missionControl = missionControl;

        sendStatusReport(ThreadReport.REPORT_REASON_CONSTRUCTOR_END, thread);
    }

    public Thread2Sketch(ThreadSketcher missionControl, Runnable target, String name) {

        this.thread = new Thread(target, name);
        this.missionControl = missionControl;

        sendStatusReport(ThreadReport.REPORT_REASON_CONSTRUCTOR_END, thread);
    }

    public void interrupt() {

        sendStatusReport(ThreadReport.REPORT_REASON_INTERRUPT_START, thread);
        thread.interrupt();
        sendStatusReport(ThreadReport.REPORT_REASON_INTERRUPT_END, thread);
    }

    public void join() throws InterruptedException {
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_START, thread);
        thread.join();
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_END, thread);
    }

    public void join(long millis) throws InterruptedException {
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_START, thread);
        thread.join(millis);
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_END, thread);
    }

    public void join(long millis, int nanos) throws InterruptedException {
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_START, thread);
        thread.join(millis, nanos);
        sendStatusReport(ThreadReport.REPORT_REASON_JOIN_END, thread);
    }

    public void run() {
        sendStatusReport(ThreadReport.REPORT_REASON_RUN_START, thread);
        thread.run();
        sendStatusReport(ThreadReport.REPORT_REASON_RUN_END, thread);
    }

    public void setDaemon(boolean on) {
        sendStatusReport(ThreadReport.REPORT_REASON_SET_DAEMON_START, thread);
        thread.setDaemon(on);
        sendStatusReport(ThreadReport.REPORT_REASON_SET_DAEMON_END, thread);
    }

    public void setName(String name) {
        sendStatusReport(ThreadReport.REPORT_REASON_SET_NAME_START, thread);
        thread.setName(name);
        sendStatusReport(ThreadReport.REPORT_REASON_SET_NAME_END, thread);
    }
    public void setPriority(int newPriority) {
        sendStatusReport(ThreadReport.REPORT_REASON_SET_PRIO_START, thread);
        thread.setPriority(newPriority);
        sendStatusReport(ThreadReport.REPORT_REASON_SET_PRIO_END, thread);
    }

    public static void sleep(long millis) throws InterruptedException {
        sendStatusReport(ThreadReport.REPORT_REASON_SLEEP_START, Thread.currentThread());
        Thread.sleep(millis);
        sendStatusReport(ThreadReport.REPORT_REASON_SLEEP_END,  Thread.currentThread());
    }

    public static void sleep(long millis, int nanos) throws InterruptedException {
        sendStatusReport(ThreadReport.REPORT_REASON_SLEEP_START, Thread.currentThread());
        Thread.sleep(millis, nanos);
        sendStatusReport(ThreadReport.REPORT_REASON_SLEEP_END, Thread.currentThread());
    }

    public void start() {
        sendStatusReport(ThreadReport.REPORT_REASON_START_START, thread);
        thread.start();
        sendStatusReport(ThreadReport.REPORT_REASON_START_END, thread);
    }

    public static void yield() {
        sendStatusReport(ThreadReport.REPORT_REASON_YIELD_START,  Thread.currentThread());
        Thread.yield();
        sendStatusReport(ThreadReport.REPORT_REASON_YIELD_END,  Thread.currentThread());
    }



}
