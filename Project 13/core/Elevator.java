package core;

import java.text.DecimalFormat;

/**
 * Created on 2017-03-13.
 * Overview:电梯类
 */
public class Elevator implements ElevatorConstant,ElevatorInterface{
    private int startFloor;
    private double startTime;
    //if request time > completeTime,we set startTime = request time
    private double completeTime;//target time
    private int moveDire;//move direction
    private SingleRequest completeRequest;
    private DecimalFormat decimalFormat;
    public Elevator() {
        /*
        @MODIFIES:this
        @EFFECTS:startFloor == 1 && startTime == 0.0 && completeTime == 0.0 && moveDire == STATUS_STILL
                && completeRequest == null && decimalFormat == new DecimalFormat("0.0")
         */
        startFloor = 1;
        startTime = 0.0; //just for initial
        completeTime = 0.0;
        moveDire = STATUS_STILL;
        completeRequest = null;
        decimalFormat = new DecimalFormat("0.0");
    }
    public void setCompleteRequest(SingleRequest completeRequest){
        /*
        @REQUIRES:completeRequest!=null && completeRequest.legacy == true
        @MODIFIES:this.completeRequest,this.startFloor
        @EFFECTS:if(\old(this.completeRequest)!=null) ==> startFloor == \old(this.completeRequest).targetFloor;
                 this.completeRequest == completeRequest
         */
        if(this.completeRequest!=null)
            startFloor = this.completeRequest.getTargetFloor();
        this.completeRequest = completeRequest;
    }
    public void setMoveDire(){
        /*
        @REQUIRES:this.completeRequest!=null && this.completeRequest.legacy == true
        @MODIFIES:this.moveDire
        @EFFECTS:if(completeRequest.targetFloor>startFloor) ==> moveDire == STATUS_UP
                else if (completeRequest.targetFloor<startFloor) ==> moeDire == STATUS_DOWN
                else ==> moveDire == STATUS_STILL
         */
        if(completeRequest.getTargetFloor()>startFloor)
            moveDire = STATUS_UP; //UP
        else if(completeRequest.getTargetFloor()<startFloor)
            moveDire = STATUS_DOWN; //DOWN
        else if(completeRequest.getTargetFloor()==startFloor)
            moveDire = STATUS_STILL; //STILL
    }
    public void setCompleteTime(){
        /*
        @REQUIRES:this.completeRequest!=null && this.completeRequest.legacy == true
        @MODIFIES:this.startTime,this.completeTime
        @EFFECTS::if(completeRequest.requestTime > completeTime) ==> startTime == completeRequest.requestTime
                  else ==> startTime == completeTime;
                  completeTime == startTime + |dstFloor - startFloor|*每层运行消耗时间 + 开关门时间
         */
        if(completeRequest.getRequestTime() > completeTime){
            startTime = completeRequest.getRequestTime();
        }else{
            startTime = completeTime;
        }
        completeTime = startTime + Math.abs(completeRequest.getTargetFloor() - startFloor)*eachFloorResume + openCloseInterval;
    }
    public void outPut(){
        /*
        @EFFECTS:输出主请求完成时相关信息(时间处理为要求的格式) 前提条件:completeTime!=0.0
                  completeRequest + " / (" + completeRequest.targetFloor + "," + status + ","+ format(completeTime) + ")"
                    电梯运动状态是向上==>  status == UP
                    电梯运动状态是向下==>status == DOWN
                    电梯楼层没有发生变化==>status == STILL
         */
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
        /*
        @EFFECTS: \result == completeTime
         */
        return completeTime;
    }
    public SingleRequest getCompleteRequest(){
        /*
        @EFFECTS:\result == completeRequest
         */
        return completeRequest;
    }
    public int getMoveDire(){
        /*
        @EFFECTS:\result == moveDire
         */
        return moveDire;
    }
    public double getArriveTime(int targetFloor){
        /*
        @REQUIRES:targetFloor>=1 && targetFloor<=10
        @EFFECTS: \result == startTime + |targetFloor - startFloor|*每层楼消耗的时间
         */
        return startTime + Math.abs(targetFloor - startFloor)*eachFloorResume;
    }
    public boolean isAblePick(int requestTime,int targetFloor,boolean mode){
        //mode == true ==> 上行pick判断
        /*
        @REQUIRES:requestTime >= startTime && targetFloor in [1,10]
        @EFFECTS:if(mode==true) ==> {
            if targetFloor > startFloor && getArriveTime(targetFloor) > requestTime  ==> \result == true
            else ==> \result == false
        }else{
            if targetFloor < startFloor && (getArriveTime(targetFloor) > requestTime) ==> \result == true
            else ==> \result == false
        }
         */
        return (mode)?targetFloor > startFloor && (getArriveTime(targetFloor) > requestTime):
                      targetFloor < startFloor && (getArriveTime(targetFloor) > requestTime);
    }
    public void resetMemberVars(int pickFloor){ //because of pick request.
        /*
        @REQUIRES: pickFloor in [1,10]
        @MODIFIES:this
        @EFFECTS: (this.completeTime == \old(this.completeTime)+开关门时间) &&
                  (this.startTime == getArriveTime(pickFloor) + 开关门时间) &&
                  (this.startTime == pickFloor);
                  调用setMoveDire();
         */
        this.completeTime += openCloseInterval;
        this.startTime = getArriveTime(pickFloor) + openCloseInterval;
        this.startFloor = pickFloor;
        setMoveDire();
    }
    public void accomplishPickedRequest(SingleRequest objRequest){
        /*
        @REQUIRES:objRequest!=null && objRequest.legacy == true
        @EFFECTS:输出完成捎带请求的相关信息:
                    objRequest + " / (" + objRequest.targetFloor + "," + status + ","+ format(捎带请求完成时间) + ")"
                 if电梯向上==>status == UP
                 else 电梯向下==> status == DOWN
         */
        double time = getArriveTime(objRequest.getTargetFloor());
        if(moveDire==STATUS_UP)
            System.out.println(objRequest.toString()+" / ("+objRequest.getTargetFloor()+",UP,"+decimalFormat.format(time)+")");
        else if(moveDire==STATUS_DOWN)
            System.out.println(objRequest.toString()+" / ("+objRequest.getTargetFloor()+",DOWN,"+decimalFormat.format(time)+")");
    }
    /*
    以下方法是为了junit测试而新加的方法
     */
    public int getStartFloor() {
        /*
        @EFFECTS:\result == startFloor
         */
        return startFloor;
    }

    public double getStartTime() {
        /*
        @EFFECTS:\result == startTime
         */
        return startTime;
    }
}