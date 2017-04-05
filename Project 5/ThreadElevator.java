package core;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created on 2017-04-03.
 */
public class ThreadElevator extends Thread implements ElevatorConstant{
    private boolean[] buttonStatus = new boolean[MAX_FLOOR];
    private double[] lightButtonTime = new double[MAX_FLOOR];
    private int moveDire;
    private int currentFloor;
    private int moveCount;
    private int code;
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");//standard
    private int stillToMove; //when status in STATUS_OPEN_CLOSE,set next status when 6.0s over.
    ThreadElevator(int code){
        moveDire = STATUS_STILL;
        currentFloor = MIN_FLOOR;
        moveCount = 0;
        stillToMove = STATUS_STILL;
        this.code = code;
        for(int i=0;i<MAX_FLOOR;i++) {
            buttonStatus[i] = false;
            lightButtonTime[i] = 0.000;
        }
    }
    public void run() {//start the running
        try {
            startElevator();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    private void startElevator() throws InterruptedException,IOException{
        while(true){
            Floors temp = ALSThreadDispatcher.floors;
            int myDire;
            synchronized (this){myDire = this.moveDire;}
            boolean sleepFlag,alongFlag,againstFlag;
            if(myDire==STATUS_UP){
                sleep((int)eachFloorResume*1000);
                synchronized (this){
                    synchronized (ALSThreadDispatcher.floors){
                        moveCount++;
                        currentFloor++;
                        sleepFlag = outPut(temp,"UP");
                        alongFlag = againstFlag = false;
                        for(int i=currentFloor+1;i<=MAX_FLOOR;i++){
                            if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1) && temp.getExecUpElevatorAt(i-1)==code)
                                    || (temp.getFloorDownLightStatusAt(i-1) && temp.getExecDownElevatorAt(i-1)==code)){
                                alongFlag = true;
                                break;
                            }
                        }
                        if(!alongFlag){
                            for(int i=currentFloor-1;i>=MIN_FLOOR;i--){
                                if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1) && temp.getExecUpElevatorAt(i-1)==code)
                                        || (temp.getFloorDownLightStatusAt(i-1) && temp.getExecDownElevatorAt(i-1)==code)){
                                    againstFlag = true;
                                    break;
                                }
                            }
                        }
                        if(!alongFlag && !againstFlag)
                            this.moveDire = STATUS_STILL;
                        else if(!alongFlag)
                            this.moveDire = STATUS_DOWN_WAIT;
                    }
                }
                if(sleepFlag) {
                    sleep((int) openCloseInterval * 1000);
                    synchronized (this) {//off the relevant light
                        synchronized (ALSThreadDispatcher.floors) {
                            offLight(currentFloor, temp);
                            if(this.moveDire == STATUS_DOWN_WAIT)
                                this.moveDire = STATUS_DOWN;
                        }
                    }
                }
            }else if(myDire==STATUS_DOWN){
                sleep((int)eachFloorResume*1000);
                synchronized (this){
                    synchronized (ALSThreadDispatcher.floors) {
                        moveCount++;
                        currentFloor--;
                        sleepFlag = outPut(temp,"DOWN");
                        alongFlag = againstFlag = false;
                        for(int i=currentFloor-1;i>=MIN_FLOOR;i--){
                            if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1) && temp.getExecUpElevatorAt(i-1)==code)
                                    || (temp.getFloorDownLightStatusAt(i-1) && temp.getExecDownElevatorAt(i-1)==code)){
                                alongFlag = true;
                                break;
                            }
                        }
                        if(!alongFlag){
                            for(int i=currentFloor+1;i<=MAX_FLOOR;i++){
                                if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1) && temp.getExecUpElevatorAt(i-1)==code)
                                        || (temp.getFloorDownLightStatusAt(i-1) && temp.getExecDownElevatorAt(i-1)==code)){
                                    againstFlag = true;
                                    break;
                                }
                            }
                        }
                        if(!alongFlag && !againstFlag)
                            this.moveDire = STATUS_STILL;
                        else if(!alongFlag)
                            this.moveDire = STATUS_UP_WAIT;
                    }
                }
                if(sleepFlag) {
                    sleep((int) openCloseInterval * 1000);
                    synchronized (this) {//off the relevant light
                        synchronized (ALSThreadDispatcher.floors) {
                            offLight(currentFloor, temp);
                            this.moveDire = (this.moveDire==STATUS_UP_WAIT)? STATUS_UP:this.moveDire;
                        }
                    }
                }
            }else if(myDire==STATUS_OPEN_CLOSE){
                //this.moveDire = STATUS_STILL;
                sleep((int)openCloseInterval*1000);
                synchronized (this) {
                    synchronized (ALSThreadDispatcher.floors) {
                        outPut(temp,"STILL");
                        offLight(currentFloor, temp);
                        this.moveDire = this.stillToMove;
                        this.stillToMove = STATUS_STILL;
                    }
                }
            }
        }
    }
    //when dispatcher find ele is in still status,but new request give it.
    void dispatcherSetMoveDire(int targetFloor){
        synchronized (this) {
            if (targetFloor == currentFloor)
                this.moveDire = STATUS_OPEN_CLOSE;
            else if (targetFloor > currentFloor)
                this.moveDire = STATUS_UP;
            else if (targetFloor < currentFloor)
                this.moveDire = STATUS_DOWN;
        }
    }
    //when status is STATUS_OPEN_CLOSE
    void setStillToMove(int targetFloor){
        synchronized (this) {
            if (targetFloor == currentFloor)
                this.stillToMove = STATUS_STILL;
            else if (targetFloor > currentFloor) {
                this.stillToMove = STATUS_UP;
            }else if(targetFloor < currentFloor){
                this.stillToMove = STATUS_DOWN;
            }
        }
    }
    int getStillToMove(){
        synchronized (this){
            return this.stillToMove;
        }
    }
    int getMoveDire(){
        synchronized (this) {
            return this.moveDire;
        }
    }
    int getCurrentFloor(){
        synchronized (this) {
            return this.currentFloor;
        }
    }
    int getMoveCount(){
        synchronized (this) {
            return this.moveCount;
        }
    }
    void lightButtonAt(int floor,double time){
        synchronized (this) {
            buttonStatus[floor] = true;
            lightButtonTime[floor] = time;
        }
    }
    boolean getButtonAt(int floor){
        synchronized (this) {
            return buttonStatus[floor];
        }
    }
    int getCode(){
        return code;
    }
    private void offLight(int currentFloor,Floors temp){
        if (this.buttonStatus[currentFloor - 1])
            buttonStatus[currentFloor-1] = false;
        if (temp.getFloorUpLightStatusAt(currentFloor - 1) && temp.getExecUpElevatorAt(currentFloor - 1) == code)
            temp.setFloorUpLightStatusAt(currentFloor - 1, false);
        if (temp.getFloorDownLightStatusAt(currentFloor - 1) && temp.getExecDownElevatorAt(currentFloor - 1) == code)
            temp.setFloorDownLightStatusAt(currentFloor - 1, false);
    }
    private boolean outPut(Floors temp,String status) throws IOException {
        boolean flag =false;
        if (this.buttonStatus[currentFloor - 1]) {
            Main.bufferedWriter.write(Main.getStandardOSTime() + ":[ER,#" + code + "," + currentFloor + ", " +
                    decimalFormat.format(lightButtonTime[currentFloor - 1]) + "] / (#" + code + ", " + currentFloor + ", " +status+", "+
                    moveCount + ", " + decimalFormat.format((System.currentTimeMillis() - Main.startTime-TIME_RESUME) / 1000.0) + ")\n");
            Main.bufferedWriter.flush();
            flag = true;
        }

        if (temp.getFloorUpLightStatusAt(currentFloor - 1) && temp.getExecUpElevatorAt(currentFloor - 1) == code) {
            Main.bufferedWriter.write(Main.getStandardOSTime() + ":[FR," + currentFloor + ",UP, " +
                    decimalFormat.format(temp.getLightUpTimeAt(currentFloor - 1)) + "] / (#" + code + ", " + currentFloor + ", "+status+", "+
                    moveCount + ", " + decimalFormat.format((System.currentTimeMillis() - Main.startTime-TIME_RESUME) / 1000.0) + ")\n");
            Main.bufferedWriter.flush();
            flag = true;
        }
        if (temp.getFloorDownLightStatusAt(currentFloor - 1) && temp.getExecDownElevatorAt(currentFloor - 1) == code) {
            Main.bufferedWriter.write(Main.getStandardOSTime() + ":[FR," + currentFloor + ",DOWN, " +
                    decimalFormat.format(temp.getLightDownTimeAt(currentFloor - 1)) + "] / (#" + code + ", " + currentFloor + ", "+status+", "+
                    moveCount + ", " + decimalFormat.format((System.currentTimeMillis() - Main.startTime-TIME_RESUME) / 1000.0) + ")\n");
            Main.bufferedWriter.flush();
            flag = true;
        }
        return flag;
    }
    //find the pick FR max
    int limitFloor(Floors temp){
        synchronized (this) {
            synchronized (ALSThreadDispatcher.floors) {
                if (this.moveDire == STATUS_UP) {
                    int max = currentFloor;
                    for(int i=max+1;i<=MAX_FLOOR;i++){
                        if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1)&& temp.getExecUpElevatorAt(i-1)==code)
                                || (temp.getFloorDownLightStatusAt(i-1)&& temp.getExecDownElevatorAt(i-1)==code)){
                            max = i;
                        }
                    }
                    return max;
                }else if(this.moveDire==STATUS_DOWN){
                    int min = currentFloor;
                    for(int i=min-1;i>=MIN_FLOOR;i--){
                        if(buttonStatus[i-1] || (temp.getFloorUpLightStatusAt(i-1)&& temp.getExecUpElevatorAt(i-1)==code)
                                || (temp.getFloorDownLightStatusAt(i-1)&& temp.getExecDownElevatorAt(i-1)==code)){
                            min = i;
                        }
                    }
                    return min;
                }
                return currentFloor;
            }
        }
    }
}
