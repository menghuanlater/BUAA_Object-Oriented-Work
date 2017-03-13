package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ****** on 2017-03-12.
 * just use as input.
 */
public class Main {
    static List<String> illegalMessage = new ArrayList<>();
    public static void main(String[] args) {
        try{
            System.out.println("please enter all request divide by new line and end by input \"run\" or \"RUN\"!");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            RequestQueue requestQueue = new RequestQueue();

            String request = input.readLine().replaceAll(" ","");//get a String and delete all space
            while(!(request.equals("run")||request.equals("RUN"))){  //loop to recognize
                requestQueue.addRequest(request);
                request = input.readLine().replaceAll(" ","");
            }
            //we have recorder all the request but don't know whether it is legal request
            //the next we will send all the task to Dispatcher.
            Dispatcher dispatcher = new Dispatcher(requestQueue);
            dispatcher.carryOutTheElevator();
            //output the illegal message
            outIllegalMessage();
        }catch (Exception e){
            System.out.println("Error!");
        }
    }
    public static void outIllegalMessage(){
        for(int loop=0,loopLength = illegalMessage.size();loop<loopLength;loop++)
            System.out.println(illegalMessage.get(loop));
        System.exit(0);//normal exit
    }
}
