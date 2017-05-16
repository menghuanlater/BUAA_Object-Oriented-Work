package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 * 接受查询请求\乘客请求\道路修改请求
 */
public class RequestInputHandler extends Thread implements GlobalConstant {
    /*
    @Overview:从终端读取输入,根据输入挖掘其中的请求,按照请求分类送入相应的请求队列.
     */
    private RequestQueue<SearchRequest> srQueue;
    private RequestQueue<PassengerRequest> prQueue;
    private RequestQueue<RoadRequest> rrQueue;
    private BufferedReader input;
    RequestInputHandler(RequestQueue<SearchRequest> srQueue, RequestQueue<PassengerRequest> prQueue,
                        RequestQueue<RoadRequest> rrQueue){
        /*@REQUIRES:(\all argument !=null)
        @MODIFIES:\all member vars
        @EFFECTS:构造
        */
        this.prQueue = prQueue;
        this.srQueue =srQueue;
        this.rrQueue = rrQueue;
        this.input = new BufferedReader(new InputStreamReader(System.in));
    }
    /*@repOk
    check:1.srQueue!=null && prQueue!=null && rrQueue!=null input!=null
    2.srQueue,prQueue,rrQueue all are instanceof RequestQueue
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        if(srQueue==null || prQueue==null || rrQueue==null || input==null)
            return false;
        Object x = srQueue;
        Object y = prQueue;
        Object z = rrQueue;
        if(!(x instanceof RequestQueue) || !(y instanceof RequestQueue) || !(z instanceof RequestQueue))
            return false;
        return true;
    }
    public void run(){
        /*@REQUIRES:(\all arguments !=null)
        @MODIFIES:\all member vars
        @EFFECTS:normal_behavior:不停获取输入的请求,按类型送入对应的队列中,非法请求则报错
                 输入读取异常或者sleep出错==>exceptional_behavior:(IOException|InterruptedException)打印异常处理栈
        */
        String line;
        try {
            while(!(line = input.readLine().replaceAll(" ","")).equalsIgnoreCase("end")){
                if(line.length()==0) continue;
                if(line.equals("VIP")){
                    Main.itrTrackVIPTaxis();
                    continue;
                }
                double requestTime = Double.valueOf(Main.decimalFormat.format((System.currentTimeMillis()-Main.startTime)/1000.0));
                List<SearchRequest> sList = new ArrayList<>();
                List<PassengerRequest> pList = new ArrayList<>();
                List<RoadRequest> rList = new ArrayList<>();
                String[] requests = line.split(";");
                if(requests.length==0) continue;
                for (String obj : requests) {
                    if(obj.equals("")) continue;
                    if (obj.charAt(0) != '[' || obj.charAt(obj.length() - 1) != ']') {
                        Main.outPutInfoToTerminal(obj + " is illegal.");
                        continue;
                    }
                    String arg[] = obj.substring(1, obj.length() - 1).split(",");
                    SearchRequest temp1 = null;
                    PassengerRequest temp2 = null;
                    RoadRequest temp3 = null;
                    if (arg.length == ARGUMENT_P && arg[0].equals(PASSENGER))
                        temp2 = new PassengerRequest(obj, arg[1]+","+arg[2], arg[3]+","+arg[4],requestTime);
                    else if (arg.length == ARGUMENT_S && arg[0].equals(SEARCH))
                        temp1 = new SearchRequest(obj, arg[1],requestTime);
                    else if(arg.length == ARGUMENT_R && arg[0].equals(ROAD))
                        temp3 = new RoadRequest(arg[1]+","+arg[2], arg[3]+","+arg[4],arg[5]);
                    else {
                        Main.outPutInfoToTerminal(obj + " is illegal.");
                        continue;
                    }
                    //只检查乘客请求是否多余,如果搜索请求重复,仍然处理
                    if (temp1 != null && temp1.isLegacy()) sList.add(temp1);
                    else if (temp2 != null && temp2.isLegacy() && checkSameRequest(pList,temp2)) pList.add(temp2);
                    else if(temp3 != null && temp3.isLegacy()) rList.add(temp3);
                    else Main.outPutInfoToTerminal(obj+" is illegal or is same.");
                }
                srQueue.addRequest(sList);
                prQueue.addRequest(pList);
                rrQueue.addRequest(rList);
                sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    //返回true代表不是同质请求
    private boolean checkSameRequest(List<PassengerRequest> objList,PassengerRequest target){
        /*@REQUIRES:(\all argument !=null) && target.legacy==true && (\all objList.get(i).legacy==true,0<=i<objList.size())
        @MODIFIES:None
        @EFFECTS:返回请求target是否已经在objList中
        */
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
