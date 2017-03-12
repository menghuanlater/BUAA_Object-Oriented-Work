package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by ****** on 2017-03-12.
 */
public class Main {
    public static void main(String[] args) {
        try{
            System.out.println("please enter all request divide by new line and end by input run!");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            RequestQueue requestQueue = new RequestQueue();

            String request = input.readLine().replaceAll(" ","");//get a String and delete all space
            while(!request.equals("run")){  //loop to recognize
                requestQueue.addRequest(request);
                request = input.readLine().replaceAll(" ","");
            }
            //we have recorder all the request but don't know whether it is legal request



        }catch (Exception e){
            System.out.println("Error!");
        }
    }
}
