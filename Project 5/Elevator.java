package core;

import java.text.DecimalFormat;

/**
 * Created on 2017-03-13.
 */
class Elevator implements ElevatorConstant,ElevatorInterface{
    private int startFloor;
    private double startTime;
    //if request time > completeTime,we set startTime = request time
    private double completeTime;//target time
    private int moveDire;//move direction
    private SingleRequest completeRequest;
    private DecimalFormat decimalFormat;
    Elevator() {
        startFloor = 1;
        startTime = 0.0; //just for initial
        completeTime = 0.0;
        moveDire = STATUS_STILL;
        completeRequest = null;
        decimalFormat = new DecimalFormat("0.0");
    }
    public void setCompleteRequest(SingleRequest completeRequest){
        if(this.completeRequest!=null)
            startFloor = this.completeRequest.getTargetFloor();
        this.completeRequest = completeRequest;
    }
    public void setMoveDire(){
        if(completeRequest.getTargetFloor()>startFloor)
            moveDire = STATUS_UP; //UP
        else if(completeRequest.getTargetFloor()<startFloor)
            moveDire = STATUS_DOWN; //DOWN
        else if(completeRequest.getTargetFloor()==startFloor)
            moveDire = STATUS_STILL; //STILL
    }
    public void setCompleteTime(){
        if(completeRequest.getRequestTime() > completeTime){
            startTime = completeRequest.getRequestTime();
            completeTime = startTime + Math.abs(completeRequest.getTargetFloor() - startFloor)*eachFloorResume+ openCloseInterval;
        }else{
            startTime = completeTime;
        }
        completeTime = startTime + Math.abs(completeRequest.getTargetFloor() - startFloor)*eachFloorResume + openCloseInterval;
    }
    public void outPut(){
        if (completeTime != 0.0) {
            if(moveDire==STATUS_STILL){
                System.out.println(completeRequest.toString()+" / ("+completeRequest.getTargetFloor()+",STILL,"+decimalFormat.format(completeTime)+")");
            }else if(moveDire==STATUS_UP){
                System.out.println(completeRequest.toString()+" / ("+completeRequest.getTargetFloor()+",UP,"+decimalFormat.format(completeTime-1.0)+")");
            }else if(moveDire==STATUS_DOWN){
                System.out.println(completeRequest.toString()+" / ("+completeRequest.getTargetFloor()+",DOWN,"+decimalFormat.format(completeTime-1.0)+")");
            }
        }
    }
    public double getCompleteTime(){
        return completeTime;
    }
    public SingleRequest getCompleteRequest(){
        return completeRequest;
    }
    public int getMoveDire(){
        return moveDire;
    }
    public double getArriveTime(int targetFloor){
        return startTime + Math.abs(targetFloor - startFloor)*eachFloorResume;
    }
    public boolean isAblePick(double requestTime,int targetFloor,boolean mode){
        return (mode)?targetFloor > startFloor && (getArriveTime(targetFloor) > requestTime):
                      targetFloor < startFloor && (getArriveTime(targetFloor) > requestTime);
    }
    public void resetMemberVars(int pickFloor){ //because of pick request.
        this.completeTime += openCloseInterval;
        this.startTime = getArriveTime(pickFloor) + openCloseInterval;
        this.startFloor = pickFloor;
        setMoveDire();
    }
    public void accomplishPickedRequest(SingleRequest objRequest){
        double time = getArriveTime(objRequest.getTargetFloor());
        if(moveDire==STATUS_UP)
            System.out.println(objRequest.toString()+" / ("+objRequest.getTargetFloor()+",UP,"+decimalFormat.format(time)+")");
        else if(moveDire==STATUS_DOWN)
            System.out.println(objRequest.toString()+" / ("+objRequest.getTargetFloor()+",DOWN,"+decimalFormat.format(time)+")");
    }
}