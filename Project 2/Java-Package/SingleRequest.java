package core;

/**
 * Created by ****** on 2017-03-12.
 */
public class SingleRequest implements ElevatorConstant{
    private int requestType; //type--> if 0, illegal, if 1 ER, if 2 FR
    private int moveType; //type---> if 0,illegal, if 1 UP, if 2 DOWN
    private int requestTime;// time
    private int targetFloor;
    private boolean legalRequest = true;

    public SingleRequest(String request,int beforeRequestTime){ //include check legacy
        if(request.length()==0){
            legalRequest = false;
        }else if(request.charAt(0)!='(' || request.charAt(request.length()-1)!=')'){
            errorBuild(request);
        }else{
            String args[] = request.substring(1,request.length()-1).split(",");
            if(args.length==ER_ARGS && args[0].equals(ELEVATOR_INNER)){
                try{
                    requestType = 1;
                    targetFloor = Integer.parseInt(args[1]);
                    requestTime = Integer.parseInt(args[2]);
                    if(!checkFloorAndTimeLegacy(targetFloor,requestTime,beforeRequestTime)){
                        errorBuild(request);
                    }
                }catch(Exception e){
                    errorBuild(request);
                }
            }else if(args.length==FR_ARGS && args[0].equals(ELEVATOR_OUTER)){
                try{
                    requestType = 2;
                    targetFloor = Integer.parseInt(args[1]);
                    requestTime = Integer.parseInt(args[3]);
                    if(!checkFloorAndTimeLegacy(targetFloor,requestTime,beforeRequestTime)){
                        errorBuild(request);
                        return;
                    }
                    if(args[2].equals(ELEVATOR_UP) && targetFloor<MAX_FLOOR){
                        moveType = 1;
                    }else if(args[2].equals(ELEVATOR_DOWN) && targetFloor>MIN_FLOOR){
                        moveType = 2;
                    }else{
                        errorBuild(request);
                    }
                }catch(Exception e){
                    errorBuild(request);
                }
            }else{
                errorBuild(request);
            }
        }
    }
    //if the request is illegal,will carry out this function.
    private void errorBuild(String request){
        System.out.println(request+" is illegal, the program will ignore this request.");
        requestType = 0;
        moveType = 0;
        requestTime = -1;
        targetFloor = 0;
        legalRequest = false;
    }
    //to check whether floor and time is legal
    private boolean checkFloorAndTimeLegacy(int floor,int time,int beforeRequestTime){
        if(floor>MAX_FLOOR || floor<MIN_FLOOR || time<0 || time<beforeRequestTime){
            return false;
        }else{
            return true;
        }
    }
    public int getRequestType(){
        return requestType;
    }
    public int getTargetFloor(){
        return targetFloor;
    }
    public int getMoveType(){
        return moveType;
    }
    public int getRequestTime(){
        return requestTime;
    }
    public boolean isLegalRequest(){
        return legalRequest;
    }
}
