package core;

import java.util.ArrayList;
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
            //goto check and alloc.
            for(PassengerRequest aCopeList : copeList)
                chooseBestTaxi(aCopeList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            Main.mapSignal.clearMapSignal(copeList);//清除请求信息.
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
            Taxi bestTaxi = taxis.get(0);
            for(int i=1;i<taxis.size();i++){
                Taxi temp = taxis.get(i);
                if(temp.getCurrentCredit()>bestTaxi.getCurrentCredit())
                    bestTaxi = temp;
                else if(temp.getCurrentCredit()==bestTaxi.getCurrentCredit() && guigv.m.distance(temp.getCurrentRow(),
                        temp.getCurrentCol(),target.getSrcRow(),target.getSrcCol()) < guigv.m.distance(bestTaxi.getCurrentRow(),
                        bestTaxi.getCurrentCol(),target.getSrcRow(),target.getSrcCol()))
                    bestTaxi = temp;
            }
            Main.commandTaxis[bestTaxi.getTaxiCode()].setAllocRequest(target);//任务分配,yes.
            //输出分配提示信息.
            Main.outPutInfoToTerminal(target.getRequest()+" 分配给"+bestTaxi.getTaxiCode()+"号出租车");
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
