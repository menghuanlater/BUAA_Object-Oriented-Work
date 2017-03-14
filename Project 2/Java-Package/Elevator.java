package core;

import java.text.DecimalFormat;

/**
 * Created on 2017-03-13.
 */
public class Elevator implements ElevatorConstant{
    private int startFloor;
    private double startTime;
    //if request time > completeTime,we set startTime = request time
    private double completeTime;//target time
    private int moveDire;//move direction
    private SingleRequest completeRequest;
    private DecimalFormat decimalFormat;
    public Elevator() {
        startFloor = 1;
        startTime = 0.0; //just for initial
        completeTime = 0.0;
        moveDire = 0;
        completeRequest = null;
        decimalFormat = new DecimalFormat(".0");
    }
    public void setCompleteRequest(SingleRequest completeRequest){
        if(this.completeRequest!=null)
            startFloor = this.completeRequest.getTargetFloor();
        this.completeRequest = completeRequest;
    }
    public void setMoveDire(){
        if(completeRequest.getTargetFloor()>startFloor)
            moveDire = 1; //UP
        else if(completeRequest.getTargetFloor()<startFloor)
            moveDire = 2; //DOWN
        else if(completeRequest.getTargetFloor()==startFloor)
            moveDire = 0; //STILL
    }
    public void setCompleteTime(){
        if(completeRequest.getRequestTime() > completeTime){
            startTime = completeRequest.getRequestTime();
            completeTime = startTime + Math.abs(completeRequest.getTargetFloor() - startFloor)*0.5 + openCloseInterval;
        }else{
            startTime = completeTime;
        }
        completeTime = startTime + Math.abs(completeRequest.getTargetFloor() - startFloor)*0.5 + openCloseInterval;
    }
    public void outPut(){
        if(completeTime==0.0)
            return;
        else if(moveDire==0){
            System.out.println("("+completeRequest.getTargetFloor()+",STILL,"+decimalFormat.format(completeTime)+")");
        }else if(moveDire==1){
            System.out.println("("+completeRequest.getTargetFloor()+",UP,"+decimalFormat.format(completeTime-1.0)+")");
        }else if(moveDire==2){
            System.out.println("("+completeRequest.getTargetFloor()+",DOWN,"+decimalFormat.format(completeTime-1.0)+")");
        }
    }
    public double getCompleteTime(){
        return completeTime;
    }
    public SingleRequest getCompleteRequest(){
        return completeRequest;
    }
}
