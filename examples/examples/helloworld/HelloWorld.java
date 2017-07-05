package examples.helloworld;

import threadSketch.ThreadSketcher;
import threadSketch.Thread2Sketch;
import static threadSketch.Thread2Sketch.threadPost;


public class HelloWorld implements Runnable {
    public void run(){
        threadPost("HelloWorld");
    }

    public static void main(String args[]) throws InterruptedException {
        //initialize the ThreadSketcher
        ThreadSketcher ts = new ThreadSketcher();

        //use Thread2Sketch instead of Thread
        Thread2Sketch t = new Thread2Sketch(ts, new HelloWorld());
        t.start();

        t.join();

        //sketch the output
        ts.sketch();
    }
}
