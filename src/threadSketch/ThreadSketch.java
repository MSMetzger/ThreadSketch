package threadSketch;



import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * @author Melvin S. Metzger
 */
public class ThreadSketch extends JFrame implements ActionListener {


    private final Queue<ThreadReport> reportQueue;
    private double nextVerticalPosition = 100;
    private static final double horizontalDistance = 200;
    private double verticalDistance = 160;
    private double nodeHeight = 30;
    private double postHeight = 80; 
    private double minPostNodeLength = 120;
    private double nodeLength = 80;
    private static final double swimlaneMargin = 30;

    private double nextThreadAxis = nextVerticalPosition;
    private double horizontalMargin = ((horizontalDistance - nodeLength + swimlaneMargin) / 2);

    private static HashMap<Long, ThreadLane> threadLaneMap;
    private static HashMap<Long, Double> sketchSwimlaneLengthMap;

    private static HashMap<ThreadPair, Date> joinTimestampMap;
    private static HashMap<Long, Date> sleepTimestampMap;

    private mxGraph graph;
    
    private static final String VERTEX_STYLE_START = "VERTEX_STYLE_START";
    private static final String VERTEX_STYLE_RUN = "VERTEX_STYLE_RUN";
    private static final String VERTEX_STYLE_SLEEP_START = "VERTEX_STYLE_SLEEP_START";
    private static final String VERTEX_STYLE_SLEEP_END = "VERTEX_STYLE_SLEEP_END";
    private static final String VERTEX_STYLE_CONSTRUCTOR = "VERTEX_STYLE_CONSTRUCTOR";
    private static final String VERTEX_STYLE_INTERRUPT = "VERTEX_STYLE_INTERRUPT";
    private static final String VERTEX_STYLE_JOIN_TRY = "VERTEX_STYLE_JOIN_TRY";
    private static final String VERTEX_STYLE_JOIN_CANCEL = "NODE STYLE_JOIN_CANCEL";
    private static final String VERTEX_STYLE_JOIN_SUCCESS = "VERTEX_STYLE_JOIN_SUCCESS";
    private static final String VERTEX_STYLE_SEND = "VERTEX_STYLE_SEND";
    private static final String VERTEX_STYLE_YIELD = "VERTEX_STYLE_YIELD";
    private static final String VERTEX_STYLE_SWIMLANE = "VERTEX_STYLE_SWIMLANE";
    private static final String EDGE_STYLE_DASHED = "EDGE_STYLE_DASHED";
    private static final String EDGE_STYLE_DOTTED = "EDGE_STYLE_DOTTED";
    private static final String EDGE_STYLE_CONNECTOR = "EDGE_STYLE_CONNECTOR";    
    private static final String EDGE_STYLE_DIRECTED = "EDGE_STYLE_DIRECTED";
    private static final String VERTEX_STYLE_INIT = "VERTEX_STYLE_INIT";
    private static final String VERTEX_STYLE_TERMINATE = "VERTEX_STYLE_TERMINATE";
    private static final String VERTEX_STYLE_POST = "VERTEX_STYLE_POST";
    private static final String VERTEX_STYLE_ATTRIBUTE =  "VERTEX_STYLE_ATTRIBUTE";
    
    
    private static final int ICON_SIZE = 48;
    
    private static final int NO_ICON_OFFSET = ICON_SIZE/2;
    
  

    private javax.swing.JMenu jMenu;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItemPNG;
    private javax.swing.JMenuItem jMenuItemCSV;

    private mxStylesheet stylesheet;
    private double VERTICAL_CONNECTOR_OFFSET = 15;
    private double CONSTRUCTOR_OFFSET = 40;
      
    private void initComponents() {
        




        jMenuBar = new javax.swing.JMenuBar();
        jMenu = new javax.swing.JMenu();
        jMenuItemPNG = new javax.swing.JMenuItem();

        jMenuItemPNG.addActionListener(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu.setText("File");

        jMenuItemPNG.setText("Export as PNG");
        jMenuItemPNG.setToolTipText("");

        jMenuItemCSV = new javax.swing.JMenuItem();
        jMenuItemCSV.addActionListener(this);

        jMenuItemCSV.setText("Export as CSV");
        jMenuItemCSV.setToolTipText("");

        jMenu.add(jMenuItemPNG);
        jMenu.add(jMenuItemCSV);

        jMenuBar.add(jMenu);

        setJMenuBar(jMenuBar);

    }// </editor-fold>
    
    public void actionPerformed(ActionEvent event){
        Object source = event.getSource();
        if(source == jMenuItemPNG){

            exportIMG();
        }
        if(source == jMenuItemCSV){
            exportCSV(this.reportQueue);
        }

    }

    private void getThreadLaneLengths(Queue<ThreadReport> reportQueue){

        sketchSwimlaneLengthMap = new HashMap<>();
        for(ThreadReport statusReport: reportQueue){
            if(statusReport.getReportReason().equals(ThreadReport.REPORT_REASON_POST)){
                Double swimlaneLength = sketchSwimlaneLengthMap.get(statusReport.getThreadId());

                if(swimlaneLength == null ){
                    swimlaneLength = horizontalDistance;
                }
                double postNodeLength = getPostNodeLength(statusReport.getPost());
                if(swimlaneLength.doubleValue() < postNodeLength){
                    swimlaneLength = postNodeLength;
                    sketchSwimlaneLengthMap.put(statusReport.getThreadId(), swimlaneLength);
                }

            }

        }


    }

    private double getPostNodeLength(String post){
        int stringLength = 0;
        int maxStringLength = 0;
        char[] postCharArray = post.toCharArray();
        for(char c : postCharArray){
            if(c == '\n'){
                if(stringLength > maxStringLength){
                    maxStringLength = stringLength;
                }

                stringLength = 0;
            }else{
                stringLength ++;

            }
        }
        if(stringLength > maxStringLength){
            maxStringLength = stringLength;
        }


        double postNodeLength = (maxStringLength * 5) + 10;
        if(postNodeLength < minPostNodeLength){
            postNodeLength = minPostNodeLength;
        }

        return postNodeLength;

    }


    public ThreadSketch(Queue<ThreadReport> reportQueue) {

        this.graph = new mxGraph() {
            public String getToolTipForCell(Object cell) {
                if (model.isEdge(cell)) {
                    return ("");
                }
                return (super.getToolTipForCell(cell));
            }
        };

        this.reportQueue = reportQueue;


        graph.setConnectableEdges(false);
        graph.setAllowDanglingEdges(false);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
        graphComponent.setToolTips(true);

        stylesheet = graph.getStylesheet();

        Map<String, Object> default_edge = stylesheet.getDefaultEdgeStyle();
        default_edge.put(mxConstants.STYLE_FONTCOLOR, "black");
        default_edge.put(mxConstants.STYLE_STROKECOLOR, "black");
        default_edge.put(mxConstants.STYLE_ROUNDED, true);
        default_edge.put(mxConstants.STYLE_STROKEWIDTH, "2");
        default_edge.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "lightgrey");

        Map<String, Object> default_vertex = stylesheet.getDefaultVertexStyle();
        default_vertex.put(mxConstants.STYLE_FONTCOLOR, "black");
        default_vertex.put(mxConstants.STYLE_STROKECOLOR, "black");
        default_vertex.put(mxConstants.STYLE_FILLCOLOR, "white");
        default_vertex.put(mxConstants.STYLE_STROKEWIDTH, "2");
        default_vertex.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "white");

        Map<String, Object> vertexStyle_init = new HashMap<String, Object>();
        vertexStyle_init.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        vertexStyle_init.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
        vertexStyle_init.put(mxConstants.STYLE_ROUNDED, false);

        stylesheet.putCellStyle(this.VERTEX_STYLE_INIT, vertexStyle_init);

        Map<String, Object> vertexStyle_terminate = new HashMap<String, Object>();
        vertexStyle_terminate.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
        vertexStyle_terminate.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
        vertexStyle_terminate.put(mxConstants.STYLE_ROUNDED, false);

        stylesheet.putCellStyle(this.VERTEX_STYLE_TERMINATE, vertexStyle_terminate);

        Map<String, Object> vertexStyle_start = new HashMap<String, Object>();
        vertexStyle_start.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_start.put(mxConstants.STYLE_IMAGE, "/icons/ic_play_circle_outline_black_48dp_1x.png");
        vertexStyle_start.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM); 

        stylesheet.putCellStyle(this.VERTEX_STYLE_START, vertexStyle_start);


        Map<String, Object> vertexStyle_run = new HashMap<String, Object>();
        vertexStyle_run.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_run.put(mxConstants.STYLE_IMAGE, "/icons/ic_add_circle_outline_black_48dp_1x.png");
        vertexStyle_run.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_RUN, vertexStyle_run);

        Map<String, Object> vertexStyle_construct = new HashMap<String, Object>();
        vertexStyle_construct.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);


        vertexStyle_construct.put(mxConstants.STYLE_IMAGE, "/icons/ic_add_circle_outline_black_48dp_1x.png");
        vertexStyle_construct.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_CONSTRUCTOR, vertexStyle_construct);

        Map<String, Object> vertexStyle_sleep_start = new HashMap<String, Object>();
        vertexStyle_sleep_start.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_sleep_start.put(mxConstants.STYLE_IMAGE, "/icons/ic_pause_circle_outline_black_48dp_1x.png" );
        vertexStyle_sleep_start.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_SLEEP_START, vertexStyle_sleep_start);

        Map<String, Object> vertexStyle_sleep_end = new HashMap<String, Object>();
        vertexStyle_sleep_end.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_sleep_end.put(mxConstants.STYLE_IMAGE,  "/icons/ic_replay_black_48dp_1x.png" );
        vertexStyle_sleep_end.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_SLEEP_END, vertexStyle_sleep_end);

        Map<String, Object> vertexStyle_interrupt = new HashMap<String, Object>();
        vertexStyle_interrupt.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_interrupt.put(mxConstants.STYLE_IMAGE, "/icons/ic_flash_on_black_48dp_1x.png");
        vertexStyle_interrupt.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_INTERRUPT, vertexStyle_interrupt);

        Map<String, Object> vertexStyle_send = new HashMap<String, Object>();
        vertexStyle_send.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_send.put(mxConstants.STYLE_IMAGE, "/icons/ic_send_black_48dp_1x.png");
        vertexStyle_send.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_SEND, vertexStyle_send);

        Map<String, Object> vertexStyle_joinTry = new HashMap<String, Object>();
        vertexStyle_joinTry.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_joinTry.put(mxConstants.STYLE_IMAGE,  "/icons/joinTry.png");
        vertexStyle_joinTry.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_JOIN_TRY, vertexStyle_joinTry);

        Map<String, Object> vertexStyle_joinCancel = new HashMap<String, Object>();
        vertexStyle_joinCancel.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_joinCancel.put(mxConstants.STYLE_IMAGE, "/icons/joinCancel.png");
        vertexStyle_joinCancel.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_JOIN_CANCEL, vertexStyle_joinCancel);

        Map<String, Object> vertexStyle_joinSuccess = new HashMap<String, Object>();
        vertexStyle_joinSuccess.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_joinSuccess.put(mxConstants.STYLE_IMAGE, "/icons/joinSuccess.png");
        vertexStyle_joinSuccess.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_JOIN_SUCCESS, vertexStyle_joinSuccess);

        Map<String, Object> vertexStyle_yield = new HashMap<String, Object>();
        vertexStyle_yield.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        vertexStyle_yield.put(mxConstants.STYLE_IMAGE, "/icons/yield.png");
        vertexStyle_yield.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        stylesheet.putCellStyle(this.VERTEX_STYLE_YIELD, vertexStyle_yield);

        Map<String, Object> vertexStyle_post = new HashMap<String, Object>();
        vertexStyle_post.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        vertexStyle_post.put(mxConstants.STYLE_VERTICAL_ALIGN, "middle");
        vertexStyle_post.put(mxConstants.STYLE_ROUNDED, "true");

        stylesheet.putCellStyle(this.VERTEX_STYLE_POST, vertexStyle_post);

        Map<String, Object> vertexStyle_attribute = new HashMap<String, Object>();
        vertexStyle_attribute.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        vertexStyle_attribute.put(mxConstants.STYLE_VERTICAL_ALIGN, "middle");
        vertexStyle_attribute.put(mxConstants.STYLE_ROUNDED, "false");

        stylesheet.putCellStyle(this.VERTEX_STYLE_ATTRIBUTE, vertexStyle_attribute);

        Map<String, Object> vertexStyle_swimlane = new HashMap<String, Object>();
        vertexStyle_swimlane.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
        vertexStyle_swimlane.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        vertexStyle_swimlane.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "white");
        vertexStyle_swimlane.put(mxConstants.STYLE_SWIMLANE_FILLCOLOR, "white");

        vertexStyle_swimlane.put(mxConstants.STYLE_STROKEWIDTH, "1");
        stylesheet.putCellStyle(this.VERTEX_STYLE_SWIMLANE, vertexStyle_swimlane);

        String dashPattern = "6 4 2 4";

        Map<String, Object> edgeStyle_dashed = new HashMap<String, Object>();
        edgeStyle_dashed.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle_dashed.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        edgeStyle_dashed.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
        edgeStyle_dashed.put(mxConstants.STYLE_DASHED, true);
        edgeStyle_dashed.put(mxConstants.STYLE_DASH_PATTERN, dashPattern);
        edgeStyle_dashed.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "lightgrey");
        edgeStyle_dashed.put(mxConstants.STYLE_STROKECOLOR, "blue");

        stylesheet.putCellStyle(this.EDGE_STYLE_DASHED, edgeStyle_dashed);

        dashPattern = "2 4 2 4";

        Map<String, Object> edgeStyle_dotted = new HashMap<String, Object>();
        edgeStyle_dotted.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle_dotted.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
        edgeStyle_dotted.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OVAL);
        edgeStyle_dotted.put(mxConstants.STYLE_DASHED, true);
        edgeStyle_dotted.put(mxConstants.STYLE_DASH_PATTERN, dashPattern);
        edgeStyle_dotted.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "lightgrey");
        edgeStyle_dotted.put(mxConstants.STYLE_STROKECOLOR, "blue");

        stylesheet.putCellStyle(this.EDGE_STYLE_DOTTED, edgeStyle_dotted);

        Map<String, Object> edgeStyle_connector = new HashMap<String, Object>();
        edgeStyle_connector.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle_connector.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        edgeStyle_connector.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
        edgeStyle_connector.put(mxConstants.STYLE_DASHED, false);
        edgeStyle_connector.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "lightgrey");
        edgeStyle_connector.put(mxConstants.STYLE_STROKECOLOR, "blue");
        stylesheet.putCellStyle(this.EDGE_STYLE_CONNECTOR, edgeStyle_connector);
        
        
        Map<String, Object> edgeStyle_directed = new HashMap<String, Object>();
        edgeStyle_directed.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle_directed.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
        edgeStyle_directed.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
        edgeStyle_directed.put(mxConstants.STYLE_DASHED, false);
        edgeStyle_directed.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "lightgrey");
        edgeStyle_directed.put(mxConstants.STYLE_STROKECOLOR, "blue");
        stylesheet.putCellStyle(this.EDGE_STYLE_DIRECTED, edgeStyle_directed);
         
         

        getThreadLaneLengths(reportQueue);

        threadLaneMap = new HashMap<Long, ThreadLane>();
        threadLaneMap.put(new Long(1), getNewSketchSwimlane(new Long(1), "Main"));


        joinTimestampMap = new HashMap<ThreadPair, Date>();
        sleepTimestampMap = new HashMap<Long, Date>();
        initComponents();
    }

    private ThreadLane getNewSketchSwimlane(Long threadID, String threadName) {
        Double swimlaneLength = sketchSwimlaneLengthMap.get(threadID);
        if(swimlaneLength == null){
            swimlaneLength = horizontalDistance ;
        }
        ThreadLane tmp = new ThreadLane(threadID, this.graph.insertVertex(this.graph.getDefaultParent(), null, threadName, this.nextThreadAxis, 0, swimlaneLength+swimlaneMargin, this.verticalDistance, this.VERTEX_STYLE_SWIMLANE), this.nextThreadAxis );

        nextThreadAxis = nextThreadAxis + swimlaneLength + swimlaneMargin *2;
        return tmp;
    }

    private double getNextVerticalPosition() {
        double tmp = this.nextVerticalPosition;
        nextVerticalPosition = nextVerticalPosition + verticalDistance;
        return tmp;

    }

    public void drawAll(Queue<ThreadReport> reportQueue) {

        drawInit();
        for (ThreadReport tsr : reportQueue) {
         //   System.out.print(tsr);
          //  System.out.println("\n\n###\n\n");

            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_CONSTRUCTOR_END) {
                drawNode_constructor(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_RUN_START) {
                drawNode_run(tsr);
            }

            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_START_START) {
                drawNode_start(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_RUN_START) {
                drawNode_run(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_JOIN_START) {
                drawNode_join_start(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_JOIN_END) {
                drawNode_join_end(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_SLEEP_START) {
                drawNode_sleep_start(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_SLEEP_END) {
                drawNode_sleep_end(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_INTERRUPT_START) {
                drawNode_interrupt(tsr);
            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_YIELD_START) {
                drawNode_yield(tsr);

            }
            if (tsr.getReportReason() == ThreadReport.REPORT_REASON_POST) {
                drawNode_post(tsr);
            }

            if(tsr.getReportReason() == ThreadReport.REPORT_REASON_SET_DAEMON_END){
                drawNode_setDaemon(tsr);
            }
            if((tsr.getReportReason() == ThreadReport.REPORT_REASON_SET_NAME_END)){
                drawNode_setName(tsr);
            }
            if((tsr.getReportReason() == ThreadReport.REPORT_REASON_SET_PRIO_END)){
                drawNode_setPriority(tsr);
            }
        }
        drawTerminate();

        
        
        // get maximum swimlane height
        mxCell tmp;
        double maxHeight = 0;
        for (ThreadLane threadLane : threadLaneMap.values()) {
            tmp = ((mxCell) threadLane.getThreadLane());
            if (maxHeight < tmp.getGeometry().getHeight()) {
                maxHeight = tmp.getGeometry().getHeight();
            }
        }
        
       //align ThreadLanes with maximum height
        for (ThreadLane threadLane : threadLaneMap.values()) {

            this.graph.getModel().beginUpdate();

            try {

                tmp = ((mxCell) threadLane.getThreadLane());
                // code altered from http://forum.jgraph.com/questions/262/how-to-auto-adjust-the-size-of-vertices-in-
                mxGeometry g = (mxGeometry) tmp.getGeometry().clone();

                mxRectangle bounds = graph.getView().getState(tmp).getLabelBounds();
                g.setHeight(maxHeight + horizontalDistance);
                graph.cellsResized(new Object[]{tmp}, new mxRectangle[]{g});
                // end of code altered from http://forum.jgraph.com/questions/262/how-to-auto-adjust-the-size-of-vertices-in-

            } finally {

                this.graph.getModel().endUpdate();
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height);
        setVisible(true);

    }

    private void drawInit() {
        this.graph.getModel().beginUpdate();
        ThreadLane mainSwimlane = threadLaneMap.get(new Long(1));

        try {
            Object v1 = this.graph.insertVertex(mainSwimlane.getThreadLane(), null, null, horizontalMargin + NO_ICON_OFFSET , getNextVerticalPosition(), nodeHeight, nodeHeight, this.VERTEX_STYLE_INIT);
            appendToThread(mainSwimlane, v1, null);
            mainSwimlane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    private void drawTerminate() {
        this.graph.getModel().beginUpdate();
        ThreadLane mainSwimlane = threadLaneMap.get(new Long(1));

        try {
            Object v1 = this.graph.insertVertex(mainSwimlane.getThreadLane(), null, null, horizontalMargin + NO_ICON_OFFSET, getNextVerticalPosition(), nodeHeight, nodeHeight, this.VERTEX_STYLE_TERMINATE);
            appendToThread(mainSwimlane, v1, null);
            mainSwimlane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    private void appendToThread(ThreadLane thread, Object vertex, String label) {
        if (!(thread.getLastVertex() == null)) {
            this.graph.getModel().beginUpdate();

            try {
                if (thread.getOpenWait() == false) { 
                    stylesheet = graph.getStylesheet(); 
                     
                    
                    graph.insertEdge(thread.getThreadLane(), null, label, thread.getLastVertex(), vertex, this.EDGE_STYLE_CONNECTOR);
                } else {

                    graph.insertEdge(thread.getThreadLane(), null, label, thread.getLastVertex(), vertex, this.EDGE_STYLE_DASHED);
                }

            } finally {
                graph.getModel().endUpdate();
            }
        }
    }

    public void drawNode_constructor(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = getNewSketchSwimlane(statusReport.getThreadId(), statusReport.getName());
        threadLaneMap.put(statusReport.getThreadId(), reportingThreadLane);

        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());

        double newVPosition = getNextVerticalPosition();

        try {
            //create node on constructing thread axis
            mxCell v1 = (mxCell) this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "Thread\nConstructed", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_SEND);

            appendToThread(currentThreadLane, v1, null);
            currentThreadLane.setLastVertex(v1);

            //create node on new thread axis
            mxCell v2 = (mxCell) this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, statusReport.getName() + "\n[Constructor]", horizontalMargin, getNextVerticalPosition(), nodeLength, nodeHeight, this.VERTEX_STYLE_CONSTRUCTOR);

            reportingThreadLane.setLastVertex(v2);

            mxCell e = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "create", v1, v2, this.EDGE_STYLE_DIRECTED);

            java.util.List<mxPoint> pointCoordinates = new ArrayList<>();
            
            pointCoordinates.add(new mxPoint(reportingThreadLane.getAxis() + horizontalMargin + CONSTRUCTOR_OFFSET , newVPosition + VERTICAL_CONNECTOR_OFFSET));
            e.getGeometry().setPoints(pointCoordinates);

        } finally {
           mxIGraphModel model= this.graph.getModel();
            this.graph.getModel().endUpdate();
            int y = 1;
        }

    }

    public void drawNode_start(ThreadReport statusReport) {

        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());

        double newVPosition = getNextVerticalPosition();
        try {
            //Node in running thread: Thread\nRun -
            mxCell v1 = (mxCell) this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "Thread\nstarted", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_SEND);
            appendToThread(currentThreadLane, v1, null);
            currentThreadLane.setLastVertex(v1);

            mxCell v2 = (mxCell) this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, statusReport.getName() + "\n[start]", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_START);
            appendToThread(reportingThreadLane, v2, null);
            reportingThreadLane.setLastVertex(v2);

            mxCell e = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "start", v1, v2, this.EDGE_STYLE_DOTTED);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    public void drawNode_run(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());

        double newVPosition = getNextVerticalPosition();
        try {
            //Node in running thread: Thread\nRun -
            Object v1 = this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "Thread\nstarted", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_SEND);
            appendToThread(currentThreadLane, v1, null);
            currentThreadLane.setLastVertex(v1);

            Object v2 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, statusReport.getName() + "\n[run]", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_START);
            appendToThread(reportingThreadLane, v2, null);
            reportingThreadLane.setLastVertex(v2);

            graph.insertEdge(graph.getDefaultParent(), null, "run", v1, v2, this.EDGE_STYLE_DOTTED);

        } finally {
            this.graph.getModel().endUpdate();
        }

    }

    public void drawNode_interrupt(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();
        double newVPosition = getNextVerticalPosition();
        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());
        try {

            Object v1 = this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "interrupting\nthread", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_SEND);
            appendToThread(currentThreadLane, v1, null);
            currentThreadLane.setLastVertex(v1);

            Object v2 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, "interrupt", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_INTERRUPT);
            appendToThread(reportingThreadLane, v2, null);
            reportingThreadLane.setLastVertex(v2);

            graph.insertEdge(graph.getDefaultParent(), null, "interrput", v1, v2, this.EDGE_STYLE_DOTTED);

            reportingThreadLane.setOpenWait(false);
        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    public void drawNode_sleep_start(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        sleepTimestampMap.put(statusReport.getThreadId(), statusReport.getTimestamp());

        try {

            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, " Sleep\n[start]", horizontalMargin, getNextVerticalPosition(), nodeLength, nodeHeight, this.VERTEX_STYLE_SLEEP_START);
            appendToThread(reportingThreadLane, v1, null);
            reportingThreadLane.setLastVertex(v1);

            reportingThreadLane.setOpenWait(true);

        } finally {
            this.graph.getModel().endUpdate();

        }

    }

    public void drawNode_sleep_end(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        Date sleepStartTime = this.sleepTimestampMap.get(statusReport.getThreadId());

        long sleepDuration = statusReport.getTimestamp().getTime() - sleepStartTime.getTime();
        try {
            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, " Sleep\n[end]", horizontalMargin, getNextVerticalPosition(), nodeLength, nodeHeight, this.VERTEX_STYLE_SLEEP_END);

            appendToThread(reportingThreadLane, v1, sleepDuration + "ms");
            reportingThreadLane.setLastVertex(v1);

            reportingThreadLane.setOpenWait(false);
        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    
    public void drawNode_yield(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        try {
            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, " Yield ", horizontalMargin, getNextVerticalPosition(), nodeLength, nodeHeight, this.VERTEX_STYLE_YIELD);

            appendToThread(reportingThreadLane, v1, "");
            reportingThreadLane.setLastVertex(v1);

            reportingThreadLane.setOpenWait(true);
        } finally {
            this.graph.getModel().endUpdate();
        }
    }
    public void drawNode_setName(ThreadReport statusReport){
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        try {
            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, "name set to:\n" + statusReport.getName(),(horizontalDistance + NO_ICON_OFFSET - minPostNodeLength)/2 + 3, getNextVerticalPosition(), minPostNodeLength, postHeight, this.VERTEX_STYLE_ATTRIBUTE);

            appendToThread(reportingThreadLane, v1, "");
            reportingThreadLane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }


    public void drawNode_setDaemon(ThreadReport statusReport){
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        try {
            String daemonState = Boolean.toString(statusReport.isIsDaemon());
            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, "daemon set to:\n" + daemonState,(horizontalDistance + NO_ICON_OFFSET - minPostNodeLength)/2 + 3, getNextVerticalPosition(), minPostNodeLength, postHeight, this.VERTEX_STYLE_ATTRIBUTE);

            appendToThread(reportingThreadLane, v1, "");
            reportingThreadLane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    public void drawNode_setPriority(ThreadReport statusReport){
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        try {

            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, "priority set to:\n" + statusReport.getPriority(),(horizontalDistance + NO_ICON_OFFSET - minPostNodeLength)/2 + 3, getNextVerticalPosition(), minPostNodeLength, postHeight, this.VERTEX_STYLE_ATTRIBUTE);

            appendToThread(reportingThreadLane, v1, "");
            reportingThreadLane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }



    public void drawNode_post(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        try {
            // (horizontalDistance - minPostNodeLength) / 2
            Object v1 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, statusReport.getPost(),(horizontalDistance + NO_ICON_OFFSET - minPostNodeLength)/2 + 3, getNextVerticalPosition(), minPostNodeLength, postHeight, this.VERTEX_STYLE_POST);

            appendToThread(reportingThreadLane, v1, "");
            reportingThreadLane.setLastVertex(v1);

        } finally {
            this.graph.getModel().endUpdate();
        }
    } 

    public void drawNode_join_start(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();
        // draw node in initiating task

        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());

        ThreadPair tmp = new ThreadPair(statusReport.getThreadId(), statusReport.getCurrentThreadId());
        joinTimestampMap.put(tmp, statusReport.getTimestamp());

        double newVPosition = getNextVerticalPosition();
        try {
            //node in running thread
            Object v1 = this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "join\n[try]", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_JOIN_TRY);
            appendToThread(currentThreadLane, v1, null);
            currentThreadLane.setLastVertex(v1);
            currentThreadLane.setOpenWait(true);

            Object v2 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, null, horizontalMargin + 40, newVPosition + 15, 0, 0);
            appendToThread(reportingThreadLane, v2, null);
            reportingThreadLane.setLastVertex(v2);
            
            graph.insertEdge(graph.getDefaultParent(), null, "", v1, v2, this.EDGE_STYLE_DOTTED);
        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    public void drawNode_join_end(ThreadReport statusReport) {
        this.graph.getModel().beginUpdate();
        ThreadLane reportingThreadLane = threadLaneMap.get(statusReport.getThreadId());
        ThreadLane currentThreadLane = threadLaneMap.get(statusReport.getCurrentThreadId());

        //if Thread is Terminated -> join successful
        ThreadPair tmp = new ThreadPair(statusReport.getThreadId(), statusReport.getCurrentThreadId());
        Date joinStartTime = this.joinTimestampMap.get(tmp);
        long joinDuration = statusReport.getTimestamp().getTime() - joinStartTime.getTime();

        double newVPosition = getNextVerticalPosition();

        if (!statusReport.isIsAlive()) {
            //draw vertex in current thread, link to child thread
            Object v1 = this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "join\n[success]", horizontalMargin , newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_JOIN_SUCCESS);
            appendToThread(currentThreadLane, v1, joinDuration + "ms");
            currentThreadLane.setLastVertex(v1);

            currentThreadLane.setOpenWait(false);
            mxCell e = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "join", reportingThreadLane.getLastVertex(), v1,  this.EDGE_STYLE_DIRECTED);

            java.util.List<mxPoint> pointCoordinates = new ArrayList<>();
            pointCoordinates.add(new mxPoint(reportingThreadLane.getAxis() + horizontalMargin + 40, newVPosition + 15));
            e.getGeometry().setPoints(pointCoordinates);

        } else {

            //insert join incomplete vertex, note time diff. on connecting edge
            Object v1 = this.graph.insertVertex(currentThreadLane.getThreadLane(), null, "join\n[Canceled]", horizontalMargin, newVPosition, nodeLength, nodeHeight, this.VERTEX_STYLE_JOIN_CANCEL);
            appendToThread(currentThreadLane, v1, joinDuration + "ms");
            currentThreadLane.setLastVertex(v1);

            currentThreadLane.setOpenWait(false);
            Object v2 = this.graph.insertVertex(reportingThreadLane.getThreadLane(), null, "", horizontalMargin  + CONSTRUCTOR_OFFSET, newVPosition + 15, 0, 0);
        }

        //  reportingThreadLane.setOpenWait(false);
        this.graph.getModel().endUpdate();

    }


    public void exportCSV(Queue<ThreadReport> statusReports){

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(this);
        File fileToSave = null;
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
        }
        if (fileToSave != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave, true));


                String csvHeader = "ThreadId;name;reportReason;priority;state;isAlive;isDaemon;isInterrupted;timestamp;currentThreadId\n";

                FileWriter fileWriter = new FileWriter(fileToSave);
                bw.write(csvHeader);

                for(ThreadReport statusReport : statusReports){
                    bw.write(statusReport.csvString() + "\n");
                }
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

 
    public void exportIMG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(this);
        File fileToSave = null;
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
        }
        if (fileToSave != null) {
            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
            try {
                ImageIO.write(image, "PNG", fileToSave);
            } catch (IOException ex) {
                Logger.getLogger(ThreadSketch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }




/**
 *
 * @author Melvin S. Metzger
 */
private class ThreadLane {

    
      
     
    private Long threadId;
    private Object swimlane;
    private Double axis;
    private Object lastVertex;

    private Boolean openWait;

    public ThreadLane(Long threadId, Object swimLane, Double axis) {
        this.threadId = threadId;
        this.swimlane = swimLane;
        this.axis = axis ;
        this.openWait = false; 
    }
    
    
    @Override
    public int hashCode(){
        return(threadId.hashCode() ^ swimlane.hashCode() ^ axis.hashCode() ^openWait.hashCode());
    }
    
    
    @Override
    public boolean equals(Object obj){
        return ((obj instanceof ThreadLane)
                && ((ThreadLane) obj).threadId == threadId)
                && ((ThreadLane) obj).swimlane == swimlane
                && ((ThreadLane) obj).axis == axis
                &&((ThreadLane) obj).openWait.compareTo(openWait) == 0;
    }

    public Long getThreadId() {
        return threadId;
    }

    public boolean getOpenWait() {
        return openWait;
        
    }
    public void setOpenWait(boolean openWait){
        this.openWait = openWait;
    }
    


    public Object getThreadLane() {
        return swimlane;
    }

    public Double getAxis() {
        return axis;
    }

    public Object getLastVertex() {
        return lastVertex;
    }

    public void setLastVertex(Object lastVertex) {
        this.lastVertex = lastVertex;
    }

    
    
    
    
    
}


/**
 * Created by Melvin S. Metzger on 1/10/2017, 10:44 PM.
 */
private class ThreadPair {

        private final Long parentThread;
        private final Long thread;

        public ThreadPair(Long parentThread, Long thread){

            this.parentThread = parentThread;
            this.thread = thread;
        }

        public Long getParentThread() {
            return parentThread;
        }

        public Long getThread() {
            return thread;
        }

        @Override
        public int hashCode(){
            return(parentThread.hashCode() ^ thread.hashCode());
        }

        @Override
        public boolean equals(Object obj){
            return ((obj instanceof ThreadPair) && ((ThreadPair) obj).parentThread == parentThread) && ((ThreadPair) obj).thread == thread;
        }



    }

}


