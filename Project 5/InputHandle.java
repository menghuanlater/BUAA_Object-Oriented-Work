package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created on 2017-04-02.
 * this class is for input requests thread
 */
class InputHandle extends Thread implements ElevatorConstant{
    private final RequestQueue requestQueue;
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");//standard
    InputHandle(RequestQueue requestQueue){
        this.requestQueue = requestQueue;
    }
    public void run(){
        try {
            getRequest();
        } catch (IOException e) {
            System.out.println("occur IO error!");
        }catch (InterruptedException k){
            System.out.println("occur thread error!");
        }catch(NullPointerException m){
            System.out.println("you may input ctrl-Z / ctrl-D");
        }
    }
    private void getRequest() throws IOException, InterruptedException,NullPointerException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //read a line,care that a line contains many
        while(true){
            String requestSets[] = bufferedReader.readLine().replaceAll(" ", "").split(";");
            double requestTime = (System.currentTimeMillis()-Main.startTime)/1000.0;
            //if just a empty line,next loop
            if(requestSets[0].equals("end")){
                break;
            }
            if(requestSets.length==0)
                continue;
            int count = 0;
            for (String requestSet : requestSets) {
                if(count>=LINE_REQUEST_MAX){
                    Main.bufferedWriter.write(Main.getStandardOSTime() + ":INVALID [" + requestSet + ", " +
                            decimalFormat.format(Math.floor(requestTime)) + "]\n");
                    Main.bufferedWriter.flush();
                    break;
                }
                SingleRequest request = new SingleRequest(requestSet, requestTime);
                if (request.isLegalRequest()) {
                    requestQueue.addRequest(request);
                    count++;
                }
            }
        }
    }
}
