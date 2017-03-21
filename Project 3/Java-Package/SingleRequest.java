package core;

/**
 * Created on 2017-03-12.
 */
class SingleRequest implements ElevatorConstant{
    private int requestType; //type--> if 0, illegal, if 1 ER, if 2 FR
    private int moveType; //type---> if 0,illegal, if 1 UP, if 2 DOWN
    private int requestTime;// time
    private int targetFloor;
    private boolean legalRequest = true;

    SingleRequest(String request, int beforeRequestTime){ //include check legacy
        if(request.length()==0){
            legalRequest = false;
        }else if(request.charAt(0)!='(' || request.charAt(request.length()-1)!=')'){
            errorBuild(request);
        }else{
            String args[] = request.substring(1,request.length()-1).split(",");
            if(args.length==ER_ARGS && args[0].equals(ELEVATOR_INNER)){
                try{
                    requestType = INNER_REQUEST;
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
                    requestType = OUTER_REQUEST;
                    targetFloor = Integer.parseInt(args[1]);
                    requestTime = Integer.parseInt(args[3]);
                    if(!checkFloorAndTimeLegacy(targetFloor,requestTime,beforeRequestTime)){
                        errorBuild(request);
                        return;
                    }
                    if(args[2].equals(ELEVATOR_UP) && targetFloor<MAX_FLOOR){
                        moveType = STATUS_UP;
                    }else if(args[2].equals(ELEVATOR_DOWN) && targetFloor>MIN_FLOOR){
                        moveType = STATUS_DOWN;
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
        Main.illegalMessage.add("INVALID ["+request+"]");
        requestType = 0;
        moveType = 0;
        requestTime = -1;
        targetFloor = 0;
        legalRequest = false;
    }
    //to check whether floor and time is legal
    private boolean checkFloorAndTimeLegacy(int floor,int time,int beforeRequestTime){
        return !(floor > MAX_FLOOR || floor < MIN_FLOOR || time < 0 || time < beforeRequestTime);
    }
    int getRequestType(){
        return requestType;
    }
    int getTargetFloor(){
        return targetFloor;
    }
    int getMoveType(){
        return moveType;
    }
    int getRequestTime(){
        return requestTime;
    }
    boolean isLegalRequest(){
        return legalRequest;
    }
    public String toString(){
        String result = "["; //give a blank str bu not null
        if(this.requestType == OUTER_REQUEST) {
            result += ELEVATOR_OUTER;
            result += String.format(",%d,",this.getTargetFloor());
            if(this.getMoveType() == STATUS_UP)
                result += ELEVATOR_UP;
            else if(this.getMoveType() == STATUS_DOWN)
                result += ELEVATOR_DOWN;
            result += String.format(",%d]",this.getRequestTime());
        }else if(this.requestType == INNER_REQUEST) {
            result += ELEVATOR_INNER;
            result += String.format(",%d,%d]",this.getTargetFloor(),this.getRequestTime());
        }else
            result += "]";
        return result;
    }
}
