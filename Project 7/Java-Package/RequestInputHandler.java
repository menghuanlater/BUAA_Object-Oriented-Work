package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 * 接受查询请求和乘客请求
 */
class RequestInputHandler extends Thread implements GlobalConstant {
    private SearchRequestQueue srQueue;
    private PassengerRequestQueue prQueue;
    private BufferedReader input;
    RequestInputHandler(SearchRequestQueue srQueue,PassengerRequestQueue prQueue){
        this.prQueue = prQueue;
        this.srQueue =srQueue;
        this.input = new BufferedReader(new InputStreamReader(System.in));
    }
    public void run(){
        String line;
        try {
            while(!(line = input.readLine().replaceAll(" ","")).equalsIgnoreCase("end")){
                if(line.length()==0) continue;
                double requestTime = Double.valueOf(Main.decimalFormat.format((System.currentTimeMillis()-Main.startTime)/1000.0));
                List<SearchRequest> sList = new ArrayList<>();
                List<PassengerRequest> pList = new ArrayList<>();
                String[] requests = line.split(";");
                if(requests.length==0) continue;
                for (String obj : requests) {
                    if (obj.charAt(0) != '[' || obj.charAt(obj.length() - 1) != ']') {
                        Main.outPutInfoToTerminal(obj + " is illegal.");
                        continue;
                    }
                    String arg[] = obj.substring(1, obj.length() - 1).split(",");
                    SearchRequest temp1 = null;
                    PassengerRequest temp2 = null;
                    if (arg.length == ARGUMENT_P && arg[0].equals(PASSENGER))
                        temp2 = new PassengerRequest(obj, arg[1]+","+arg[2], arg[3]+","+arg[4],requestTime);
                    else if (arg.length == ARGUMENT_S && arg[0].equals(SEARCH))
                        temp1 = new SearchRequest(obj, arg[1],requestTime);
                    else {
                        Main.outPutInfoToTerminal(obj + " is illegal.");
                        continue;
                    }
                    if (temp1 != null && temp1.isLegacy() && checkSameRequest(sList,temp1)) sList.add(temp1);
                    else if (temp2 != null && temp2.isLegacy() && checkSameRequest(pList,temp2)) pList.add(temp2);
                    else Main.outPutInfoToTerminal(obj+" is illegal or is same.");
                }
                srQueue.addRequest(sList);
                prQueue.addRequest(pList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //返回true代表不是同质请求
    private boolean checkSameRequest(List<SearchRequest> objList,SearchRequest target){
        boolean check = true;
        for(SearchRequest temp:objList){
            if(target.getTaxiCode()==temp.getTaxiCode()){
                check = false;
                break;
            }
        }
        return check;
    }
    private boolean checkSameRequest(List<PassengerRequest> objList,PassengerRequest target){
        boolean check = true;
        for(PassengerRequest temp:objList){
            if(target.getStartCode()==temp.getStartCode() && target.getEndCode()==temp.getEndCode()){
                check = false;
                break;
            }
        }
        return check;
    }
}
