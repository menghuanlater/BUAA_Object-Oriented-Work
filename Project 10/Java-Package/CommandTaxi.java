package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 * 调度出租车的线程
 */
/*
@Overview:出租车行驶模式常量接口--->对应左转、右转、掉头、直行等
    一共13种模式
 */
interface DriveMode{
    int driveBack = 0;//掉头--->不考虑红绿灯
    int goStraightE = 1;//向东直行
    int goStraightW = 2;//向西直行
    int goStraightS = 3;//向南直行
    int goStraightN = 4;//向北直行
    int E_N = 5;//东向北左转
    int E_S = 6;//东向南右转
    int W_S = 7;//西向南左转
    int W_N = 8;//西向北右转
    int S_E = 9;//南向东左转
    int S_W = 10;//南向西右转
    int N_W = 11;//北向西左转
    int N_E = 12;//北向东右转
}

class CommandTaxi extends Thread implements GlobalConstant,DriveMode{
    /*
    @Overview:出租车控制线程,通过出租车的当前状态位置以及是否被分配请求等,执行相关的调度处理以及状态
    位置转换,主要通过sleep()睡眠模拟出租车行驶
     */
    private Taxi monitorTaxi;//监控的出租车
    private int taxiCode;//出租车编号
    private PassengerRequest allocRequest;//被分配的请求
    private double waitCount = 0.0;//等待服务时间计数器
    private boolean checkRoadChange;//改路信号
    private int frontPosition;//出租车之前的位置
    CommandTaxi(Taxi monitorTaxi,int taxiCode){
        /*@REQUIRES:monitorTaxi!=null && taxiCode>=0 && taxiCode<=99
        @MODIFIES:this.monitorTaxi,this.taxiCode,this.allocRequest,this.checkRoadChange,this.frontPosition
        @EFFECTS:类对象构造
        */
        this.monitorTaxi = monitorTaxi;
        this.taxiCode = taxiCode;
        this.allocRequest = null;
        this.checkRoadChange = false;
        this.frontPosition = -1;//初始化为-1
    }
    /*@repOk
    check:0<=taxiCode<=99; monitorTaxi!=null &&  monitorTaxi is an instance of Taxi && monitorTaxi.taxiCode == taxiCode;
    -1<=frontPosition<=6399 (由于一开始初始化在-1)
    其余成员无需检查
     */
    public boolean repOk(){
        /*@EFFECTS:\result = invariant(this)
         */
        if(taxiCode<0 || taxiCode>99) return false;
        if(monitorTaxi==null) return false;
        Object x = monitorTaxi;
        if(!(x instanceof Taxi)) return false;
        if(monitorTaxi.getTaxiCode()!=taxiCode) return false;
        if(frontPosition<-1 || frontPosition>6399) return false;
        return true;
    }
    public void run(){
        /*@REQUIRES:None
        @MODIFIES:None(调用函数将修改)
        @EFFECTS:normal_behavior:连续不停的根据出租车的状态调度相关函数运行出租车
                 if(carryWaitService() or carryStopService() or carryGrabService() or carryInService() throws
                 InterruptedException)==>exceptional_behavior(InterruptedException)打印异常处理栈
        */
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
    void setAllocRequest(PassengerRequest get){
        /*@REQUIRES:get!=null && get.legacy = true;
        @MODIFIES:this.allocRequest,this.monitorTaxi
        @EFFECTS:设置出租车即将执行的请求,修改出租车的相关状态,清空该出租车抢到的订单集合,调用输出信息到终端以及文件的其他类方法
        */
        this.allocRequest = get;
        //将交付信息输入到HashMap
        String info = "请求被"+taxiCode+"号车接手.";
        Main.safeFilePassenger.writeToFile(get.toHashString(),info);
        Main.outPutInfoToTerminal(info);
        monitorTaxi.setCurrentStatus(GRAB_SERVICE);
        monitorTaxi.clearHashSet();//清空哈希表
    }
    //出现道路关闭后,如果车处于serving或者grabbing状态,则将check置为true
    void setCheckRoadChange(){
        /*@REQUIRES:None
        @MODIFIES:this.checkRoadChange
        @EFFECTS:如果出租车此时为接单状态或者已经接到乘客正在赶往目的地的状态 ==> checkRoadChange=true;
        */
        if(monitorTaxi.getCurrentStatus()==GRAB_SERVICE || monitorTaxi.getCurrentStatus()==IN_SERVICE){
            this.checkRoadChange = true;
        }
    }
    //当出租车处于等待服务状态,则生成一个随机化的下一个去往位置(加上流量最小判断)
    private int randomNextPosition(int currentRow,int currentCol,int currentPosition){
        /*@REQUIRES:0=<(currentRow and currentCol)<=79 && 0<=currentPosition<=6399 &&
                    Main.getCodeByRowCol(currentRow,currentCol)==currentPosition
        @MODIFIES:None
        @EFFECTS:返回出租车即将走向的下一个路口编号
        */
        List<Integer> ableChoice = new ArrayList<>();
        if(currentRow-1>=0 && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow-1,currentCol)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow - 1, currentCol));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow-1,currentCol));
        }
        if(currentRow+1<ROW_NUMBER && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow+1,currentCol)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow + 1, currentCol));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow+1,currentCol));
        }
        if(currentCol-1>=0 && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow,currentCol-1)]) {
            ableChoice.add(Main.getCodeByRowCol(currentRow,currentCol-1));
            ableChoice.add(Main.carFlow.getCarFlowAt(currentRow,currentCol,currentRow,currentCol-1));
        }
        if(currentCol+1<COL_NUMBER && Main.matrix[currentPosition][Main.getCodeByRowCol(currentRow,currentCol+1)]) {
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
    //等待服务状态
    private void carryWaitService() throws InterruptedException{
        /*@REQUIRES:None
        @MODIFIES:this.waitCount,this.monitorTaxi,this.frontPosition
        @EFFECTS:normal_behavior:线程进行模拟睡眠(先检查是否需要等待红绿灯),修改出租车的相关状态、出租车上一次所在的点、以及等待服务时间累加器的值
                 调用车流量类的方法增加以及减少相应边的流量.
                 Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        //等待服务状态,先睡200ms.
        waitCount += gridConsume;
        int nextPosition = randomNextPosition(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),
                monitorTaxi.getCurrentPosition());
        //排除第一次选择的流量减问题
        if(frontPosition>=0){
            Main.carFlow.subCarFlowAt(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),Main.getRowByCode(frontPosition),
                    Main.getColByCode(frontPosition));
        }
        Main.carFlow.addCarFlowAt(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),Main.getRowByCode(nextPosition),
                Main.getColByCode(nextPosition));
        waitRedLight(nextPosition);//检查是否需要等红绿灯
        sleep((long)(gridConsume*1000));
        //设置新坐标
        frontPosition = monitorTaxi.getCurrentPosition();
        monitorTaxi.setCurrentPosition(nextPosition);
        int status;
        if(Math.abs(waitCount-singleWaitMax)<= precision){
            waitCount = 0.0;
            status = STOP_SERVICE;
        }else
            status = WAIT_SERVICE;
        //必须判断是否因为分配接单任务已经造成状态改变,如果改变,下面的语句则不允许执行
        if(monitorTaxi.getCurrentStatus()==WAIT_SERVICE)
            monitorTaxi.setCurrentStatus(status);
    }
    //停止状态
    private void carryStopService() throws InterruptedException{
        /*@REQUIRES:None
        @MODIFIES:this.monitorTaxi
        @EFFECTS:睡眠1s,修改出租车状态为等待服务状态
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        sleep((long)(stopInterval*1000));
        monitorTaxi.setCurrentStatus(WAIT_SERVICE);
    }
    //接客路途,最终状态改为IN_SERVICE
    private void carryGrabService() throws InterruptedException{
        /*@REQUIRES:None
        @MODIFIES:this.waitCount,this.monitorTaxi
        @EFFECTS:normal_behavior:waitCount清零,等待20s睡眠状态取消,调用Main.getShortestPath获得最短路径,提交给driveByShortestPath执行
                                 最短路径,执行完将处理信息一方面交给终端，一方面提交给文件输出流(调用相关方法实现)
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        //如果接客,则必须将等待服务的计数器清0
        waitCount = 0.0;
        String info = "接客路径:";
        int src = allocRequest.getStartCode();
        if(src == monitorTaxi.getCurrentPosition()){
            monitorTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long)(stopInterval*1000));
            monitorTaxi.setCurrentStatus(IN_SERVICE);
            info+="由于正好出租车在乘客所在地,直接接客.";
        }else {
            info+="("+monitorTaxi.getCurrentRow()+","+monitorTaxi.getCurrentCol()+")";
            List<Integer> shortestPath = Main.getShortestPath(monitorTaxi.getCurrentPosition(), src);
            info+=driveByShortestPath(shortestPath);
            info+="\t Ok,到达乘客所在地.";
            monitorTaxi.setCurrentStatus(STOP_GRAB);
            sleep((long)(stopInterval*1000));
            monitorTaxi.setCurrentStatus(IN_SERVICE);//转为正式服务状态
        }
        //输出实际行驶路径到文件和控制台.
        Main.outPutInfoToTerminal(info);
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
    }
    //服务状态
    private void carryInService() throws InterruptedException{
        /*@REQUIRES:None
        @MODIFIES:this.monitorTaxi
        @EFFECTS:normal_behavior:调用Main.getShortestPath获得最短路径,提交给driveByShortestPath执行最短路径,
                 执行完将处理信息一方面交给终端，一方面提交给文件输出流(调用相关类的方法实现),到达乘客所在地,睡眠1s
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        int src = allocRequest.getStartCode();
        int dst = allocRequest.getEndCode();
        String info = "正式服务行驶路径:("+allocRequest.getSrcRow()+","+allocRequest.getSrcCol()+")";
        List<Integer> shortestPath = Main.getShortestPath(src,dst);
        info+=driveByShortestPath(shortestPath);
        info+="\t OK,完成服务,已到达目的地.";
        monitorTaxi.setCurrentStatus(STOP_ACHIEVE);//到达
        monitorTaxi.addCurrentCredit();//增加信用
        sleep((long)(stopInterval*1000));
        monitorTaxi.setCurrentStatus(WAIT_SERVICE);//重新设置为等待服务状态
        //输出实际行驶路径到文件和控制台
        Main.outPutInfoToTerminal(info);
        Main.safeFilePassenger.writeToFile(allocRequest.toHashString(),info);
        Main.safeFilePassenger.outPutToFile(allocRequest.toHashString());
    }
    //为了避免代码冗余设计,在执行接客以及服务时走最短路径
    private String driveByShortestPath(List<Integer> shortestPath) throws InterruptedException{
        /*@REQUIRES:shortestPath.size>=3 && (\all 0<=shortestPath.get(i)<=6399 for 1<=i<=shortestPath.size-1)
        @MODIFIES:shortestPath,this.monitorTaxi,checkRoadChange
        @EFFECTS:normal_behavior:如果中途不出现改路的情况，按照最短路径集逐个走下去(修改出租车位置,调用车流量监控类修改流量,模拟睡眠)
                                如果出现改路,重新规划,并把checkRoadChange置为false,避免重复规划路径
                                最终返回出租车达到目的地的所有处理信息字符串.
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) throw it.
        */
        String info = "";
        int consumeTime = shortestPath.get(0);
        long firstSleep = (long)(gridConsume*1000 - consumeTime);
        if(firstSleep<0)
            firstSleep = 0;
        int position;
        for(int i=shortestPath.size()-2;i>0;i--){
            position = shortestPath.get(i);
            if(checkRoadChange){
                checkRoadChange = false;
                shortestPath = Main.getShortestPath(monitorTaxi.getCurrentPosition(),shortestPath.get(1));
                return info+driveByShortestPath(shortestPath);
            }
            Main.carFlow.subCarFlowAt(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),Main.getRowByCode(frontPosition),
                    Main.getColByCode(frontPosition));//减少流量
            Main.carFlow.addCarFlowAt(monitorTaxi.getCurrentRow(),monitorTaxi.getCurrentCol(),Main.getRowByCode(position),
                    Main.getColByCode(position));//增加流量
            waitRedLight(position);//检查是否需要等红绿灯
            if(i==shortestPath.size()-2){
                sleep(firstSleep);
            }else{
                sleep((long)(gridConsume*1000));
            }
            frontPosition = monitorTaxi.getCurrentPosition();
            monitorTaxi.setCurrentPosition(position);
            info+="->("+Main.getRowByCode(position)+","+Main.getColByCode(position)+")";
        }
        return info;
    }
    //@Overview:design for checking whether needs to wait for red light
    private void waitRedLight(int nextPosition) throws InterruptedException{
        /*@REQUIRES:0<=nextPosition=6399
        @EFFECTS:根据出租车之前的位置,现在的位置,下一步要去的位置并根据当前路口是否有红绿灯以及红绿灯的状态
        判断出租车是否需要等待红灯进入睡眠(调用红绿灯类中的方法获得需要睡眠多长时间).
        睡眠出错==>exception_behavior:(InterruptedException)throw it.
         */
        int currentPosition = monitorTaxi.getCurrentPosition();
        int mode = 0;//行驶模式识别
        int currentRow = Main.getRowByCode(currentPosition);
        int currentCol = Main.getColByCode(currentPosition);
        int nextRow = Main.getRowByCode(nextPosition);
        int nextCol = Main.getColByCode(nextPosition);
        //first,if the frontPosition is -1,then mode only can be goStraight
        if(frontPosition<0){
            if(currentRow==nextRow)
                mode = goStraightE;//东西向统一归为向东
            else if(currentCol==nextCol){
                mode = goStraightN;//南北向统一归为向北
            }
        }else{//normal judge
            int frontRow = Main.getRowByCode(frontPosition);
            int frontCol = Main.getColByCode(frontPosition);
            if(nextPosition==frontPosition)
                mode = driveBack;
            else if(frontRow==currentRow && currentRow==nextRow && nextCol-currentCol==1 && currentCol-frontCol==1)
                mode = goStraightE;
            else if(frontRow==currentRow && currentRow==nextRow && frontCol-currentCol==1 && currentCol-nextCol==1)
                mode = goStraightW;
            else if(frontCol==currentCol && currentCol==nextCol && nextRow-currentRow==1 && currentRow-frontRow==1)
                mode = goStraightS;
            else if(frontCol==currentCol && currentCol==nextCol && frontRow-currentRow==1 && currentRow-nextRow==1)
                mode = goStraightN;
            else if(frontRow==currentRow && currentCol==nextCol && currentCol-frontCol==1 && currentRow-nextRow==1)
                mode = E_N;
            else if(frontRow==currentRow && currentCol==nextCol && currentCol-frontCol==1 && nextRow-currentRow==1)
                mode = E_S;
            else if(frontRow==currentRow && currentCol==nextCol && frontCol-currentCol==1 && nextRow-currentRow==1)
                mode = W_S;
            else if(frontRow==currentRow && currentCol==nextCol && frontCol-currentCol==1 && currentRow-nextRow==1)
                mode = W_N;
            else if(frontCol==currentCol && currentRow==nextRow && currentRow-frontRow==1 && nextCol-currentCol==1)
                mode = S_E;
            else if(frontCol==currentCol && currentRow==nextRow && currentRow-frontRow==1 && currentCol-nextCol==1)
                mode = S_W;
            else if(frontCol==currentCol && currentRow==nextRow && frontRow-currentRow==1 && currentCol-nextCol==1)
                mode = N_W;
            else
                mode = N_E;
        }
        //next
        switch (mode){
            case driveBack:
            case E_S:
            case W_N:
            case S_W:
            case N_E:
                break; //右转以及掉头不需要考虑红绿灯
            case goStraightE:
            case goStraightW:
            case N_W:
            case S_E:
                sleep(Main.redGreenLight.getWaitTime(EW,currentPosition));//东西向红绿灯
                break;
            case goStraightS:
            case goStraightN:
            case W_S:
            case E_N:
                sleep(Main.redGreenLight.getWaitTime(SN,currentPosition));//南北向红绿灯
                break;
            default:break;
        }
    }
}
