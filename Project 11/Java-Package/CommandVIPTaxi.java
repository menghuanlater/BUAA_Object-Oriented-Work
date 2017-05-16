package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-05-14.
 */
class CommandVIPTaxi extends CommandNMTaxi {
    CommandVIPTaxi(NMTaxi monitorNMTaxi, int taxiCode) {
        super(monitorNMTaxi, taxiCode);
    }
    /*@repOk
    check:1.super.repOk()==true
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        return super.repOk();
    }
    @Override
    void setAllocRequest(PassengerRequest get) {
        /*
        @REQUIRES:
        @MODIFIES:
        @EFFECTS:
         */
        super.setAllocRequest(get);
        VIPTaxi monitor = (VIPTaxi) monitorNMTaxi;
        monitor.addRequest(get);
    }
    //当出租车处于等待服务状态,则生成一个随机化的下一个去往位置(加上流量最小判断)
    protected int randomNextPosition(int currentRow,int currentCol,int currentPosition){
        /*@REQUIRES:0=<(currentRow and currentCol)<=79 && 0<=currentPosition<=6399 &&
                    Main.getCodeByRowCol(currentRow,currentCol)==currentPosition && (Main.carFlow have been build)
        @MODIFIES:None
        @EFFECTS:返回出租车即将走向的下一个路口编号
        */
        //---------------------------//
        //assert Main.carFlow.repOk();
        //---------------------------//
        List<Integer> ableChoice = new ArrayList<>();
        if(currentRow-1>=0 && Main.matrixInit[currentPosition][Main.getCodeByRowCol(currentRow-1,currentCol)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow - 1, currentCol));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow-1,currentCol));
        }
        if(currentRow+1<ROW_NUMBER && Main.matrixInit[currentPosition][Main.getCodeByRowCol(currentRow+1,currentCol)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow + 1, currentCol));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow+1,currentCol));
        }
        if(currentCol-1>=0 && Main.matrixInit[currentPosition][Main.getCodeByRowCol(currentRow,currentCol-1)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow,currentCol-1));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow,currentCol-1));
        }
        if(currentCol+1<COL_NUMBER && Main.matrixInit[currentPosition][Main.getCodeByRowCol(currentRow,currentCol+1)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow,currentCol+1));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow,currentCol+1));
        }
        List<Integer> best = new ArrayList<>();
        int pos = INF;
        for(int i=0;i<ableChoice.size()/2;i++){
            if(ableChoice.get(2*i+1)<pos){
                best.clear();
                best.add(ableChoice.get(2*i));
                pos = ableChoice.get(2*i+1);
            }else if(ableChoice.get(2*i+1)==pos){
                best.add(ableChoice.get(2*i));
            }
        }
        return best.get((int)(Math.random()*best.size()));
    }
    //接客路途,最终状态改为IN_SERVICE
    protected void carryGrabService() throws InterruptedException{
        /*@REQUIRES:monitorNMTaxi!=null && monitorNMTaxi.taxiCode == this.taxiCode && Main.safeFilePassenger有效
        @MODIFIES:this.waitCount,this.monitorNMTaxi
        @EFFECTS:normal_behavior:waitCount清零,等待20s睡眠状态取消,调用Main.getShortestPath获得最短路径,提交给driveByShortestPath执行
                                 最短路径,执行完将处理信息一方面交给终端，一方面提交给文件输出流(调用相关方法实现)
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        //如果接客,则必须将等待服务的计数器清0
        waitCount = 0.0;
        String info = "接客路径:";
        int src = allocRequest.getStartCode();
        VIPTaxi temp = (VIPTaxi) monitorNMTaxi;
        if(src == monitorNMTaxi.getCurrentPosition()){
            monitorNMTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long)(stopInterval*1000));
            monitorNMTaxi.setCurrentStatus(IN_SERVICE);
            info+="由于正好出租车在乘客所在地,直接接客.";
            temp.addPickPath(src);
        }else {
            int row = monitorNMTaxi.getCurrentRow();int col = monitorNMTaxi.getCurrentCol();
            temp.addPickPath(Main.getCodeByRowCol(row,col));
            info+="("+ row +","+ col +")";
            List<Integer> shortestPath = Main.getShortestPath(monitorNMTaxi.getCurrentPosition(), src,true);
            info+=driveByShortestPath(shortestPath,false);
            info+="\t Ok,到达乘客所在地.";
            monitorNMTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long)(stopInterval*1000));
            monitorNMTaxi.setCurrentStatus(IN_SERVICE);//转为正式服务状态
        }
        //输出实际行驶路径到文件和控制台.
        Main.outPutInfoToTerminal(info);
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
    }
    //服务状态
    protected void carryInService() throws InterruptedException{
        /*@REQUIRES:monitorNMTaxi!=null && monitorNMTaxi.taxiCode == this.taxiCode &&
                    Main.safeFilePassenger有效
        @MODIFIES:this.monitorNMTaxi
        @EFFECTS:normal_behavior:调用Main.getShortestPath获得最短路径,提交给driveByShortestPath执行最短路径,
                 执行完将处理信息一方面交给终端，一方面提交给文件输出流(调用相关类的方法实现),到达乘客所在地,睡眠1s
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        VIPTaxi temp = (VIPTaxi) monitorNMTaxi;
        int src = allocRequest.getStartCode();
        int dst = allocRequest.getEndCode();
        temp.addServePath(src);
        String info = "正式服务行驶路径:("+allocRequest.getSrcRow()+","+allocRequest.getSrcCol()+")";
        List<Integer> shortestPath = Main.getShortestPath(src,dst,true);
        info+=driveByShortestPath(shortestPath,true);
        info+="\t OK,完成服务,已到达目的地.";
        temp.completeService();
        monitorNMTaxi.setCurrentStatus(STOP_ACHIEVE);//到达
        monitorNMTaxi.addCurrentCredit();//增加信用
        sleep((long)(stopInterval*1000));
        monitorNMTaxi.setCurrentStatus(WAIT_SERVICE);//重新设置为等待服务状态
        //输出实际行驶路径到文件和控制台
        Main.outPutInfoToTerminal(info);
        /*assert Main.safeFileRequest.repOk();
        assert Main.safeFilePassenger.repOk();*/
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
        Main.safeFilePassenger.outPutToFile(allocRequest.toHashString());
    }
    //为了避免代码冗余设计,在执行接客以及服务时走最短路径
    private String driveByShortestPath(List<Integer> shortestPath,boolean mode) throws InterruptedException{
        /*@REQUIRES:shortestPath.size>=3 && (\all 0<=shortestPath.get(i)<=6399 for 1<=i<=shortestPath.size-1) &&
                    monitorNMTaxi!=null && monitorNMTaxi.taxiCode == this.taxiCode && Main.carFlow有效
        @MODIFIES:shortestPath,this.monitorNMTaxi,checkRoadChange
        @EFFECTS:normal_behavior:如果中途不出现改路的情况，按照最短路径集逐个走下去(修改出租车位置,调用车流量监控类修改流量,模拟睡眠)
                                如果出现改路,重新规划,并把checkRoadChange置为false,避免重复规划路径
                                最终返回出租车达到目的地的所有处理信息字符串.
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        String info = "";
        VIPTaxi temp = (VIPTaxi) monitorNMTaxi;
        int consumeTime = shortestPath.get(0);
        long firstSleep = (long)(gridConsume*1000 - consumeTime);
        if(firstSleep<0)
            firstSleep = 0;
        int position;
        for(int i=shortestPath.size()-2;i>0;i--){
            //-----------------------------------//
            //assert Main.carFlow.repOk();
            //----------------------------------//
            position = shortestPath.get(i);
            if(checkRoadChange){
                checkRoadChange = false;
                shortestPath = Main.getShortestPath(monitorNMTaxi.getCurrentPosition(),shortestPath.get(1),true);
                return info+driveByShortestPath(shortestPath,mode);
            }
            Main.carFlow.subCarFlowAt(monitorNMTaxi.getCurrentRow(), monitorNMTaxi.getCurrentCol(),Main.getRowByCode(frontPosition),
                    Main.getColByCode(frontPosition));//减少流量
            waitRedLight(position);//检查是否需要等红绿灯
            Main.carFlow.addCarFlowAt(monitorNMTaxi.getCurrentRow(), monitorNMTaxi.getCurrentCol(),Main.getRowByCode(position),
                    Main.getColByCode(position));//增加流量
            if(i==shortestPath.size()-2){
                sleep(firstSleep);
            }else{
                sleep((long)(gridConsume*1000));
            }
            frontPosition = monitorNMTaxi.getCurrentPosition();
            monitorNMTaxi.setCurrentPosition(position);
            if(mode) temp.addServePath(position);
            else temp.addPickPath(position);
            info+="->("+Main.getRowByCode(position)+","+Main.getColByCode(position)+")";
        }
        return info;
    }
}
