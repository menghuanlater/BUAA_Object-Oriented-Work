package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-04-16.
 * 处理所有乘客请求，建立一个辅助线程类AllocTaxi，去计算3s后请求的处理情况和分配情况
 */
class AllocTaxi extends Thread implements GlobalConstant{
    private List<PassengerRequest> copeList;
    AllocTaxi(List<PassengerRequest> copeList){
        this.copeList = copeList;
    }
    public void run(){
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
        List<Integer> taxisCode = target.getGrabTaxis();
        if(taxisCode.size()==0) Main.outPutInfoToTerminal(target.getRequest()+" 没有出租车响应.");
        else{
            List<Taxi> taxis = new ArrayList<>();
            for (Integer aTaxisCode : taxisCode){
                Taxi temp = Main.taxiSets[aTaxisCode].clone();
                if(temp.getCurrentStatus()==WAIT_SERVICE)
                    taxis.add(Main.taxiSets[aTaxisCode].clone());
            }
            if(taxis.size()==0){
                Main.outPutInfoToTerminal(target.getRequest()+" 没有出租车响应.");
                return;
            }
            HashMap<Integer,Integer> sameBestCreditTaxis = new HashMap<>();
            int bestCredit = -1; boolean isJustOne = true;//判断是否只有一个最大信用的出租车,避免不必要的地图搜索
            int tempTaxiCode = -1;
            for(int i=0;i<taxis.size();i++){
                Taxi temp = taxis.get(i);
                if(temp.getCurrentCredit()>bestCredit) {
                    bestCredit = temp.getCurrentCredit();
                    sameBestCreditTaxis.clear();
                    isJustOne = true;tempTaxiCode = temp.getTaxiCode();
                    sameBestCreditTaxis.put(temp.getCurrentPosition(),temp.getTaxiCode());
                }
                else if(temp.getCurrentCredit()==bestCredit && sameBestCreditTaxis.get(temp.getCurrentPosition())==null) {
                    isJustOne = false;
                    sameBestCreditTaxis.put(temp.getCurrentPosition(), temp.getTaxiCode());
                }
            }
            int bestCode = (isJustOne)? tempTaxiCode : Main.getNearestTaxi(target.getStartCode(),sameBestCreditTaxis);
            sameBestCreditTaxis.clear();
            Main.commandTaxis[bestCode].setAllocRequest(target);//任务分配,yes.
            //输出分配提示信息.
            Main.outPutInfoToTerminal(target.getRequest()+" 分配给"+bestCode+"号出租车");
        }
    }
}

public class DealPassenger extends Thread implements GlobalConstant{
    private PassengerRequestQueue queue;
    DealPassenger(PassengerRequestQueue queue){
        this.queue = queue;
    }
    public void run(){
        while (true){
            AllocTaxi allocTaxi = new AllocTaxi(queue.getAllRequest());
            allocTaxi.start();
        }
    }
}
