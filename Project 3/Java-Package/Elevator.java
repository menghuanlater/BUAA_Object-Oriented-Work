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
            if(moveDire==0){
                System.out.println(completeRequest.toString()+" / ("+completeRequest.getTargetFloor()+",STILL,"+decimalFormat.format(completeTime)+")");
            }else if(moveDire==1){
                System.out.println(completeRequest.toString()+" / ("+completeRequest.getTargetFloor()+",UP,"+decimalFormat.format(completeTime-1.0)+")");
            }else if(moveDire==2){
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
}