package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<String> illegalMessage = new ArrayList<>();
    public static void main(String[] args) {
        try{
            System.out.println("please enter all request divide by new line and end by input \"run\" or \"RUN\"!");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            RequestQueue requestQueue = new RequestQueue();

            String request = input.readLine().replaceAll(" ","");//get a String and delete all space
            int beforeRequestTime = 0; boolean isFirst = true;
            while(!(request.equals("run")||request.equals("RUN"))){  //loop to recognize
                SingleRequest myRequest = new SingleRequest(request,beforeRequestTime);
                if(myRequest.isLegalRequest()) {
                    if(isFirst){
                        if(myRequest.toString().equals("[FR,1,UP,0]"))
                            isFirst = false;
                        else{
                            illegalMessage.add("INVALID ["+request+"]");
                            request = input.readLine().replaceAll(" ","");
                            continue;
                        }
                    }
                    requestQueue.addRequest(myRequest);
                    beforeRequestTime = myRequest.getRequestTime();
                }
                request = input.readLine().replaceAll(" ","");
            }
            //If find all the request is illegal,we will exit.
            if(requestQueue.getSizeOfQueue()==0)
                outIllegalMessage();
            //we have recorder all the request but don't know whether it is legal request
            //the next we will send all the task to Dispatcher.
            ALSDispatcher alsDispatcher = new ALSDispatcher(requestQueue);
            alsDispatcher.carryOutTheElevator();
            //output the illegal message
            outIllegalMessage();
        }catch (Exception e){
            System.out.println("Error!");
        }
    }
    private static void outIllegalMessage(){
        for (String anIllegalMessage : illegalMessage) System.out.println(anIllegalMessage);
        System.exit(0);//normal exit
    }
}
