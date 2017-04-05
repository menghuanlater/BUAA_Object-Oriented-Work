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
            if(requestSets.length>LINE_REQUEST_MAX){
                Main.bufferedWriter.write(Main.getStandardOSTime()+":Warning:Found you have input more than "+
                        LINE_REQUEST_MAX+" request,we will regard over section as invalid request.\n");
                Main.bufferedWriter.flush();
                for(int i=LINE_REQUEST_MAX;i<requestSets.length;i++) {
                    Main.bufferedWriter.write(Main.getStandardOSTime() + ":INVALID [" + requestSets[i] + ", " +
                            decimalFormat.format(requestTime) + "]\n");
                    Main.bufferedWriter.flush();
                }
            }
            int loopLength = (requestSets.length>LINE_REQUEST_MAX)? LINE_REQUEST_MAX:requestSets.length;
            synchronized (requestQueue) {
                for (int loop = 0; loop < loopLength; loop++) {
                    SingleRequest request = new SingleRequest(requestSets[loop],requestTime);
                    if (request.isLegalRequest()) {
                        requestQueue.addRequest(request);
                    }
                }
            }
        }
    }
}
