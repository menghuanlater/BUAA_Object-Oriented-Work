package core;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created on 2017-03-12.
 */
class SingleRequest implements ElevatorConstant{
    private int requestType; //type--> if 0, illegal, if 1 ER, if 2 FR
    private int moveType; //type---> if 0,illegal, if 1 UP, if 2 DOWN
    private double requestTime;// time
    private int targetFloor;
    private int targetEle;//target elevator just for ER
    private String requestStr;//request String
    private boolean legalRequest = true;
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");//standard
    SingleRequest(String request,double requestTime){ //include check legacy
        this.requestStr = request;
        this.requestTime = requestTime;
        if(request.length()==0){
            legalRequest = false;
        }else if(request.charAt(0)!='(' || request.charAt(request.length()-1)!=')'){
            errorBuild(request);
        }else{
            String args[] = request.substring(1,request.length()-1).split(",");
            if(args.length==ARGS){
                if(args[0].equals(ELEVATOR_INNER)) {//ER
                    try{
                        requestType = INNER_REQUEST;
                        targetEle = Integer.parseInt(args[1].substring(1));
                        targetFloor = Integer.parseInt(args[2]);
                        if(targetEle<=0 || targetEle>ELE_NUM || !(checkFloorLegacy(targetFloor)))
                            errorBuild(request);
                    }catch(Exception e){
                        errorBuild(request);
                    }
                }else if(args[0].equals(ELEVATOR_OUTER)) {//FR
                    try{
                        requestType = OUTER_REQUEST;
                        targetFloor = Integer.parseInt(args[1]);
                        if(args[2].equals(ELEVATOR_UP)){
                            moveType = STATUS_UP;
                            if(!checkFloorLegacy(targetFloor) || targetFloor==MAX_FLOOR)
                                errorBuild(request);
                        }else if(args[2].equals(ELEVATOR_DOWN)){
                            moveType = STATUS_DOWN;
                            if(!checkFloorLegacy(targetFloor) || targetFloor==MIN_FLOOR)
                                errorBuild(request);
                        }else
                            errorBuild(request);
                    }catch(Exception e){
                        errorBuild(request);
                    }
                }else
                    errorBuild(request);
            }else{
                errorBuild(request);
            }
        }
    }
    //if the request is illegal,will carry out this function.
    private void errorBuild(String request){
        try {
            Main.bufferedWriter.write(Main.getStandardOSTime()+":INVALID ["+request+", "+
                    decimalFormat.format(Math.floor(requestTime))+"]\n");
            Main.bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        legalRequest = false;
    }
    //to check whether floor and elevator is legal
    private boolean checkFloorLegacy(int floor){
        return !(floor > MAX_FLOOR || floor < MIN_FLOOR);
    }
    int getRequestType(){
        return requestType;
    }
    int getTargetFloor(){
        return targetFloor;
    }
    int getTargetEle(){return targetEle;}
    int getMoveType(){
        return moveType;
    }
    double getRequestTime(){
        return requestTime;
    }
    boolean isLegalRequest(){
        return legalRequest;
    }
    String getRequestStr(){return requestStr;}
}
