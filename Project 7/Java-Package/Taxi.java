package core;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 17-4-16.
 * 出租车的相关信息
 */
class Taxi implements GlobalConstant,Cloneable{
    private int currentStatus;
    private int currentCredit;
    private int currentPosition;
    private int taxiCode;
    private int currentRow;
    private int currentCol;
    private HashMap<String,PassengerRequest> grabRequest;
    Taxi(int code){
        taxiCode = code;
        currentStatus = WAIT_SERVICE;
        currentCredit = CREDIT_INIT;
        currentPosition = (int)(Math.random()*NODE_NUM);
        currentRow = Main.getRowByCode(currentPosition);
        currentCol = Main.getColByCode(currentPosition);
        Main.gui.SetTaxiStatus(taxiCode,new Point(currentRow,currentCol),currentStatus);
        grabRequest = new HashMap<>();
    }
    synchronized public Taxi clone(){
        Taxi taxi = null;
        try {
            taxi = (Taxi)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return taxi;
    }

    synchronized void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
        Main.gui.SetTaxiStatus(taxiCode,new Point(currentRow,currentCol),(currentStatus>GRAB_SERVICE)?STOP_SERVICE:currentStatus);
    }

    synchronized void addCurrentCredit() {
        this.currentCredit += ADD_PER_SERVICE;
    }

    synchronized void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        this.currentRow = Main.getRowByCode(currentPosition);
        this.currentCol = Main.getColByCode(currentPosition);
        Main.gui.SetTaxiStatus(taxiCode,new Point(currentRow,currentCol),(currentStatus>GRAB_SERVICE)?STOP_SERVICE:currentStatus);
    }

    /*synchronized void setCurrentPosition(int row,int col){
        this.currentRow = row;
        this.currentCol = col;
        this.currentPosition = Main.getCodeByRowCol(row,col);
    }*/

    synchronized void searchAblePick(){
        //只有当出租车当前的状态是等待服务状态才可以,其他的一律否决
        if(currentStatus==WAIT_SERVICE) {
            List<PassengerRequest> list = Main.mapSignal.getMapSignalAt(currentPosition);
            for (PassengerRequest aList : list) {
                String hashKey = aList.toHashString();
                if(grabRequest.get(hashKey)==null) {
                    grabRequest.put(hashKey,aList);
                    aList.addGrabTaxi(taxiCode);
                    currentCredit += ADD_PER_GRAB;
                    //一旦抢单成功就需要输出信息到SafeFile
                    String info = Main.getCurrentTime() + "s 被" + taxiCode +
                            "号出租车抢单.位置:(" + currentRow + "," + currentCol + ")\t信用值:" + currentCredit;
                    Main.safeFile.writetoFile(hashKey, info);
                    Main.outPutInfoToTerminal(hashKey + "\t" + info);
                }
            }
        }
    }

    synchronized void clearHashMap(){
        this.grabRequest.clear();
    }

    synchronized public String toString(){
        String info = "出租车当前位置:(";
        int row = Main.getRowByCode(currentPosition);
        int col = Main.getColByCode(currentPosition);
        info+=row+","+col+")\t出租车当前状态:";
        switch (currentStatus){
            case STOP_SERVICE:
                info+="停止服务.";
                break;
            case STOP_GRAB:
                info+="到达接客地,停车中.";
                break;
            case STOP_ACHIEVE:
                info+="完成服务,停车中.";
                break;
            case IN_SERVICE:
                info+="已接客,正在服务.";
                break;
            case WAIT_SERVICE:
                info+="等待服务状态.";
                break;
            case GRAB_SERVICE:
                info+="抢单成功并分配服务,正在赶往接客.";
                break;
            default:break;
        }
        return info+"\t出租车当前信用值:"+currentCredit;
    }
    //因为克隆,所以get方法不加锁,线程安全
    int getCurrentStatus() {
        return currentStatus;
    }

    int getCurrentCredit() {
        return currentCredit;
    }

    int getCurrentPosition() {
        return currentPosition;
    }

    int getCurrentRow(){
        return currentRow;
    }

    int getCurrentCol(){
        return currentCol;
    }

    int getTaxiCode() {
        return taxiCode;
    }
}
