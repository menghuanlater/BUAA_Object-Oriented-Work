package core;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    private final static String filename = "result.txt"; //the name of the output file
    private final static Charset charset = Charset.forName("US-ASCII");//set the file coding format as US-ASCII
    static BufferedWriter bufferedWriter = null;
    static double startTime;
    static InputHandle inputHandle = null;
    public static void main(String[] args) {
        try{
            startTime = System.currentTimeMillis();
            System.out.println("Welcome to use multi-threaded three elevators scheduling system.");
            /*set the out file related information*/
            Path outfile = Paths.get(filename);
            bufferedWriter = Files.newBufferedWriter(outfile,charset);
            /*the file set ok*/
            RequestQueue requestQueue = new RequestQueue();
            inputHandle = new InputHandle(requestQueue);
            inputHandle.start();//run the input thread.

            /*set dispatcher*/
            ALSThreadDispatcher alsThreadDispatcher = new ALSThreadDispatcher(requestQueue);
            Thread dispatcherThread = new Thread(alsThreadDispatcher);
            dispatcherThread.start();

            //bufferedWriter.close();//close the write stream.
        }catch (Exception e){
            System.out.println("Your input exist extremely illegal message,program will exit.");
        }
    }
    static String getStandardOSTime(){//for each expression in result.txt
        Date date = new Date();
        long sysTime = System.currentTimeMillis();
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(date)+"("+sysTime+")");
    }
}
