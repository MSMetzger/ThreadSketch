package threadSketch;



import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Melvin S. Metzger
 */
public class ThreadReport {

    static final String REPORT_REASON_CONSTRUCTOR_END = "CONSTRUCTOR_END";
    static final String REPORT_REASON_INTERRUPT_START = "INTERRUPT_START";
    static final String REPORT_REASON_INTERRUPT_END = "INTERRUPT_END";
    static final String REPORT_REASON_JOIN_START = "JOIN_START";
    static final String REPORT_REASON_JOIN_END = "JOIN_END";
    static final String REPORT_REASON_RUN_START = "RUN_START";
    static final String REPORT_REASON_RUN_END = "RUN_END";
    static final String REPORT_REASON_SET_DAEMON_START = "SET_DAEMON_START";
    static final String REPORT_REASON_SET_DAEMON_END = "SET_DAEMON_END";
    static final String REPORT_REASON_SET_NAME_START = "SET_NAME_START";
    static final String REPORT_REASON_SET_NAME_END = "SET_NAME_END";
    static final String REPORT_REASON_SET_PRIO_START = "SET_PRIO_START";
    static final String REPORT_REASON_SET_PRIO_END = "SET_PRIO_END";
    static final String REPORT_REASON_SLEEP_START = "SLEEP_START";
    static final String REPORT_REASON_SLEEP_END = "SLEEP_END";
    static final String REPORT_REASON_START_START = "START_START";
    static final String REPORT_REASON_START_END = "START_END";
    static final String REPORT_REASON_YIELD_START = "YIELD_START";
    static final String REPORT_REASON_YIELD_END = "YIELD_END";
    static final String REPORT_REASON_POST = "POST";

    private final String reportReason;
    private final long threadId;
    private final String name;
    private final int priority;
    private final Thread.State state;
    private final boolean isAlive;
    private final boolean isDaemon;
    private final boolean isInterrupted;
    private final Date timestamp;
    private final String post;

    private final long currentThreadId;

    public ThreadReport(String reportReason, long threadId, String name, int priority, Thread.State state, boolean isAlive, boolean isDaemon, boolean isInterrupted, Date timestamp, long currentThreadId) {
        this.reportReason = reportReason;
        this.threadId = threadId;
        this.name = name;
        this.priority = priority;
        this.state = state;
        this.isAlive = isAlive;
        this.isDaemon = isDaemon;
        this.isInterrupted = isInterrupted;
        this.timestamp = timestamp;
        this.currentThreadId = currentThreadId;
        this.post = null;
    }

    public ThreadReport(String reportReason, Thread reportingThread) {
        this.reportReason = reportReason;
        this.threadId = reportingThread.getId();
        this.name = reportingThread.getName();
        this.priority = reportingThread.getPriority();
        this.state = reportingThread.getState();
        this.isAlive = reportingThread.isAlive();
        this.isDaemon = reportingThread.isDaemon();
        this.isInterrupted = reportingThread.isInterrupted();
        this.timestamp = new Date();
        this.currentThreadId = reportingThread.currentThread().getId();
        this.post = null;
    }

    public ThreadReport(Thread reportingThread, String post) {
        this.reportReason = REPORT_REASON_POST;
        this.threadId = reportingThread.getId();
        this.name = reportingThread.getName();
        this.priority = reportingThread.getPriority();
        this.state = reportingThread.getState();
        this.isAlive = reportingThread.isAlive();
        this.isDaemon = reportingThread.isDaemon();
        this.isInterrupted = reportingThread.isInterrupted();
        this.timestamp = new Date();
        this.currentThreadId = reportingThread.currentThread().getId();
        this.post = post;
    }

    public String csvString(){
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss:SS");
        return("" + threadId + ";" + name + ";" + reportReason + ";" + priority + ";" + state.toString() + ";" + isAlive + ";" + isDaemon + ";" + isInterrupted + ";" + stf.format(timestamp)+ ";" + Long.toString(currentThreadId));

    }
    public String toString() {
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss:SS");
        return ("threadId: " + threadId + "\nname: " + name + "\nreport reason: " + reportReason + "\npriority: " + priority
                + "\nstate: " + state.toString() + "\nisAlive: " + isAlive
                + "\nisDaemon: " + isDaemon + "\nisInterrupted: " + isInterrupted
                + "\ntimestamp: " + stf.format(timestamp)
                + "\ncurrentThreadId: " + Long.toString(currentThreadId));
    }

    public long getThreadId() {
        return threadId;
    }

    public long getCurrentThreadId() {
        return currentThreadId;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getPost() {
        return post;
    }

    public Thread.State getState() {
        return state;
    }


    public String getReportReason() {
        return reportReason;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    public boolean isIsDaemon() {
        return isDaemon;
    }

    public boolean isIsInterrupted() {
        return isInterrupted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
