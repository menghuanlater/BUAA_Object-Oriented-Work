package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 * 调度出租车的线程
 */
class CommandTaxi extends Thread implements GlobalConstant{
    private Taxi monitorTaxi;
    private int taxiCode;
    private PassengerRequest allocRequest;
    private double waitCount = 0.0;
    CommandTaxi(Taxi monitorTaxi,int taxiCode){
        this.monitorTaxi = monitorTaxi;
        this.taxiCode = taxiCode;
        this.allocRequest = null;
    }
    public void run(){
        try {
            while (true) {
                int status = monitorTaxi.getCurrentStatus();
                //新增的两个停止状态将在
                switch (status) {
                    case WAIT_SERVICE://只有等待服务状态可以抢单
                        monitorTaxi.searchAblePick();
                        carryWaitService();
                        break;
                    case STOP_SERVICE:
                        carryStopService();
                        break;
                    case GRAB_SERVICE:
                        carryGrabService();
                        break;
                    case IN_SERVICE:
                        carryInService();
                        break;
                    default:
                        break;
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    synchronized void setAllocRequest(PassengerRequest get){
        this.allocRequest = get;
        //将交付信息输入到HashMap
        String info = Main.getCurrentTime()+"s 请求被"+taxiCode+"号车在(" +
                +monitorTaxi.getCurrentRow()+","+monitorTaxi.getCurrentCol()+")接手.";
        Main.safeFilePassenger.writeToFile(get.toHashString(),info);
        Main.outPutInfoToTerminal(info);
        monitorTaxi.setCurrentStatus(GRAB_SERVICE);
        monitorTaxi.clearHashMap();//清空哈希表
    }
    //当出租车处于等待服务状态,则生成一个随机化的下一个去往位置
    private int randomNextPosition(int currentRow,int currentCol,int currentPosition){
        List<Integer> ableChoice = new ArrayList<>();
        if(currentRow-1>=0 && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow-1,currentCol)])
            ableChoice.add(Main.getCodeByRowCol(currentRow-1,currentCol));
        if(currentRow+1<ROW_NUMBER && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow+1,currentCol)])
            ableChoice.add(Main.getCodeByRowCol(currentRow+1,currentCol));
        if(currentCol-1>=0 && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow,currentCol-1)])
            ableChoice.add(Main.getCodeByRowCol(currentRow,currentCol-1));
        if(currentCol+1<COL_NUMBER && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow,currentCol+1)])
            ableChoice.add(Main.getCodeByRowCol(currentRow,currentCol+1));
        return ableChoice.get((int)(Math.random()*ableChoice.size()));
    }
    //等待服务状态
    private void carryWaitService() throws InterruptedException{
        //等待服务状态,先睡200ms.
        waitCount += gridConsume;
        sleep((long)(gridConsume*1000));
        int nextPosition = randomNextPosition(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),
                monitorTaxi.getCurrentPosition());
        int status;
        if(Math.abs(waitCount-singleWaitMax)<= precision){
            waitCount = 0.0;
            status = STOP_SERVICE;
        }else
            status = WAIT_SERVICE;
        //必须判断是否因为分配接单任务已经造成状态改变,如果改变,下面的语句则不允许执行
        if(monitorTaxi.getCurrentStatus()==WAIT_SERVICE) {
            monitorTaxi.setCurrentPosition(nextPosition);
            monitorTaxi.setCurrentStatus(status);
        }
    }
    //停止状态
    private void carryStopService() throws InterruptedException{
        sleep((long)(stopInterVal*1000));
        monitorTaxi.setCurrentStatus(WAIT_SERVICE);
    }
    //接客路途,最终状态改为IN_SERVICE
    private void carryGrabService() throws InterruptedException{
        //如果接客,则必须将等待服务的计数器清0
        waitCount = 0.0;
        String info = "接客路径:";
        int src = allocRequest.getStartCode();
        if(src == monitorTaxi.getCurrentPosition()){
            monitorTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long)(stopInterVal*1000));
            monitorTaxi.setCurrentStatus(IN_SERVICE);
            info+="由于正好出租车在乘客所在地,直接接客.";
        }else {
            info+="("+monitorTaxi.getCurrentRow()+","+monitorTaxi.getCurrentCol()+")";
            List<Integer> shortestPath = Main.getShortestPath(monitorTaxi.getCurrentPosition(), src);
            info+=driveByShortestPath(shortestPath);
            info+="\t Ok,到达乘客所在地.";
            monitorTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long) (stopInterVal * 1000));//到达接乘客停止一秒
            monitorTaxi.setCurrentStatus(IN_SERVICE);//转为正式服务状态
        }
        //输出实际行驶路径到文件和控制台.
        Main.outPutInfoToTerminal(info);
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
    }
    //服务状态
    private void carryInService() throws InterruptedException{
        int src = allocRequest.getStartCode();
        int dst = allocRequest.getEndCode();
        String info = "正式服务行驶路径:("+allocRequest.getSrcRow()+","+allocRequest.getSrcCol()+")";
        List<Integer> shortestPath = Main.getShortestPath(src,dst);
        info+=driveByShortestPath(shortestPath);
        info+="\t OK,完成服务,已到达目的地.";
        monitorTaxi.setCurrentStatus(STOP_ACHIEVE);//到达
        monitorTaxi.addCurrentCredit();//增加信用
        sleep((long)(stopInterVal*1000));//达到目的地停止运行1s
        monitorTaxi.setCurrentStatus(WAIT_SERVICE);//重新设置为等待服务状态
        //输出实际行驶路径到文件和控制台
        Main.outPutInfoToTerminal(info);
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
        Main.safeFilePassenger.outPutToFile(allocRequest.toHashString());
    }
    //为了避免代码冗余设计,新增的
    private String driveByShortestPath(List<Integer> shortestPath) throws InterruptedException{
        String info = "";
        int consumeTime = shortestPath.get(0);
        long firstSleep = (long)(gridConsume*1000 - consumeTime);
        if(firstSleep<0)
            firstSleep = 0;
        int position;
        for(int i=shortestPath.size()-2;i>0;i--){
            if(i==shortestPath.size()-2){
                sleep(firstSleep);
            }else{
                sleep((long)(gridConsume*1000));
            }
            position = shortestPath.get(i);
            monitorTaxi.setCurrentPosition(position);
            info+="->("+Main.getRowByCode(position)+","+Main.getColByCode(position)+")";
        }
        return info;
    }
}
