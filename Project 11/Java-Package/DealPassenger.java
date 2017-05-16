package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-04-16.
 * 处理所有乘客请求，建立一个辅助线程类AllocTaxi，去计算3s后请求的处理情况和分配情况
 */
class AllocTaxi extends Thread implements GlobalConstant{
    /*
    Overview:同时间的乘客请求处理线程,模拟3s的时间窗,睡眠3s后根据每个请求的响应出租车寻找最优出租车
             同时间的多个请求按照先来先服务策略依次搜索查询最好出租车
     */
    private List<PassengerRequest> copeList;
    AllocTaxi(List<PassengerRequest> copeList){
        /*@REQUIRES:copeList!=null && copeList.size()>0 && (\all copeList.get(i).legacy == true, 0<=i<copeList.size())
        @MODIFIES:this.copeList
        @EFFECTS:构造
        */
        this.copeList = copeList;
    }
    /*@repOk.
    check:copeList!=null && copeList.size>0 && each elem in copeList, elem is instanceof PassengerRequest
    and elem!=null && elem.legacy = true.
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this);
         */
        if(copeList==null) return false;
        if(copeList.size()==0) return false;
        for (PassengerRequest aCopeList : copeList) {
            Object x = aCopeList;
            if (x == null) return false;
            if (!(x instanceof PassengerRequest)) return false;
            if (!((PassengerRequest) x).isLegacy()) return false;
        }
        return true;
    }
    public void run(){
        /*@REQUIRES:Main.mapSignal have been build
        @MODIFIES:None
        @EFFECTS:normal_behavior:调用相关类方法将请求辐射的区域打上标记,睡眠3s,并清除辐射信号,调用chooseBestTaxi为每个请求寻求最佳出租车.
                Thread.sleep()出现异常==>exceptional_behavior(InterruptedException) 打印异常处理栈信息.
        */
        try {
            //首先给相关辐射节点打上信号
            Main.mapSignal.setMapSignal(copeList);
            sleep((long) (reactTime*1000));//睡眠3s后看哪些出租车抢到了单，进行请求的出租车分配
            Main.mapSignal.clearMapSignal(copeList);//清除请求信息.
            //goto check and alloc.
            for(PassengerRequest aCopeList : copeList)
                chooseBestTaxi(aCopeList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void chooseBestTaxi(PassengerRequest target){
        /*@REQUIRES:target!=null && target.legacy = true && Main.taxiSets以及Main.commandTaxis集合对象全部实例化且有效
        @MODIFIES:None
        @EFFECTS:normal_behavior:为请求分配最佳出租车,没有可分配出租车,输出无响应.如果有响应的出租车,调用Main类中的方法输出信息到终端
        */
        List<Integer> taxisCode = target.getGrabTaxis();
        if(taxisCode.size()==0) Main.outPutInfoToTerminal(target.getRequest()+" 没有出租车响应.");
        else{
            List<NMTaxi> NMTaxis = new ArrayList<>();
            //System.out.println("the size:"+taxisCode.size());
            for (Integer aTaxisCode : taxisCode) {
                NMTaxi temp = (Main.NM_TAXI_SETS[aTaxisCode]).clone();
                if (temp.getCurrentStatus() == WAIT_SERVICE)
                    NMTaxis.add(temp);
            }
            if(NMTaxis.size()==0){
                Main.outPutInfoToTerminal(target.getRequest()+" 没有出租车响应.");
                return;
            }
            HashMap<Integer,Integer> sameNMBestCreditTaxis = new HashMap<>();
            HashMap<Integer,Integer> sameVIPBestCreditTaxis = new HashMap<>();
            int bestCredit = -1; boolean isJustOne = true;//判断是否只有一个最大信用的出租车,避免不必要的地图搜索
            int tempTaxiCode = -1;
            for (NMTaxi temp : NMTaxis) {
                if (temp.getCurrentCredit() > bestCredit) {
                    bestCredit = temp.getCurrentCredit();
                    sameNMBestCreditTaxis.clear();
                    sameVIPBestCreditTaxis.clear();
                    isJustOne = true;
                    tempTaxiCode = temp.getTaxiCode();
                    if(!temp.getJudge())
                        sameNMBestCreditTaxis.put(temp.getCurrentPosition(), temp.getTaxiCode());
                    else
                        sameVIPBestCreditTaxis.put(temp.getCurrentPosition(), temp.getTaxiCode());
                } else if (temp.getCurrentCredit() == bestCredit) {
                    if(!temp.getJudge() && sameNMBestCreditTaxis.get(temp.getCurrentPosition())==null) {
                        isJustOne = false;
                        sameNMBestCreditTaxis.put(temp.getCurrentPosition(), temp.getTaxiCode());
                    }else if(temp.getJudge() && sameVIPBestCreditTaxis.get(temp.getCurrentPosition())==null){
                        isJustOne = false;
                        sameVIPBestCreditTaxis.put(temp.getCurrentPosition(),temp.getTaxiCode());
                    }
                }
            }
            int bestCode=0;
            if(isJustOne){
                bestCode = tempTaxiCode;
            }else{
                int nm[] = null,vip[] = null;
                if(sameNMBestCreditTaxis.keySet().size()>0)
                    nm = Main.getNearestTaxi(target.getStartCode(),sameNMBestCreditTaxis,false);
                if(sameVIPBestCreditTaxis.keySet().size()>0)
                    vip = Main.getNearestTaxi(target.getStartCode(),sameVIPBestCreditTaxis,true);
                if(nm==null && vip!=null) {
                    bestCode = vip[0];
                }else if(vip==null && nm!=null){
                    bestCode = nm[0];
                }else if(vip != null){
                    bestCode = (nm[1]<vip[1])? nm[0]:vip[0];
                }
            }
            sameNMBestCreditTaxis.clear();
            sameVIPBestCreditTaxis.clear();
            Main.COMMAND_NM_TAXIS[bestCode].setAllocRequest(target);//任务分配,yes.
            //输出分配提示信息.
            Main.outPutInfoToTerminal(target.getRequest()+" 分配给"+bestCode+"号出租车");
        }
    }
}

public class DealPassenger extends Thread implements GlobalConstant{
    /*
    Overview:乘客请求抓取线程,不停访问乘客请求队列中的请求,抓取开启时间窗线程
     */
    private RequestQueue<PassengerRequest> queue;
    DealPassenger(RequestQueue<PassengerRequest> queue){
        /*@REQUIRES:queue!=null
        @MODIFIES:this.queue
        @EFFECTS:构造
        */
        this.queue = queue;
    }
    /*@repOk.
    check:queue!=null && queue is instanceof RequestQueue
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        if(queue==null) return false;
        Object x = queue;
        if(!(x instanceof RequestQueue)) return false;
        return true;
    }
    public void run(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:不停向queue请求队列申请获取请求,创建请求时间窗线程.
        */
        while (true){
            AllocTaxi allocTaxi = new AllocTaxi(queue.getAllRequest());
            allocTaxi.start();
        }
    }
}
