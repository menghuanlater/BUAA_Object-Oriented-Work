package core;

/**
 * Created by ****** on 2017-03-12.
 */
public class SingalRequest {
    private final String ELEVATOR_INNER = "ER"; //instruction of ER
    private final String ELEVATOR_OUTER = "FR"; //instruction of FR
    private final String ELEVATOR_UP    = "UP"; //go up
    private final String ELEVATOR_DOWN  = "DOWN"; //go down
    private final int MAX_FLOOR = 10;
    private final int MIN_FLOOR = 1;
    private final int ER_ARGS = 3;
    private final int FR_ARGS = 4;
    private int requestType; //type--> if 0, illegal, if 1 ER, if 2 FR
    private int moveType; //type---> if 0,illegal, if 1 UP, if 2 DOWN
    private int requestTime;// time
    private int targetFloor;

    public SingalRequest(String request){ //include check legacy
        if(request.charAt(0)!='(' || request.charAt(request.length()-1)!=')'){
            errorBuild(request);
        }else{
            String args[] = request.substring(1,request.length()-2).split(",");
            if(args.length==ER_ARGS && args[0].equals(ELEVATOR_INNER)){
                try{
                    requestType = 1;
                    targetFloor = Integer.parseInt(args[1]);
                    requestTime = Integer.parseInt(args[2]);
                    if(!checkFoorAndTimeLegacy(targetFloor,requestTime)){
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
                    if(!checkFoorAndTimeLegacy(targetFloor,requestTime)){
                        errorBuild(request);
                    }
                    if(args[2].equals(ELEVATOR_UP)){
                        moveType = 1;
                    }else if(args[2].equals(ELEVATOR_DOWN)){
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
    }
    //to check whether floor and time is legal
    private boolean checkFoorAndTimeLegacy(int floor,int time){
        if(floor>MAX_FLOOR || floor<MIN_FLOOR || time<0){
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
}
