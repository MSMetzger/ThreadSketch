package examples.HelloWorld;

import threadSketch.ThreadSketcher;
import threadSketch.Thread2Sketch;

import static threadSketch.Thread2Sketch.threadPost;


public class HelloWorld implements Runnable {
        public void run(){
            threadPost("HelloWorld");
        }

    public static void main(String args[]) throws InterruptedException {
        ThreadSketcher ts = new ThreadSketcher();

        Thread2Sketch t = new Thread2Sketch(ts, new HelloWorld());
        t.start();

        t.join();
        ts.sketch();
    }
}
