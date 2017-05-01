package core;

/**
 * Created on 2017-04-30.
 * 处理道路请求
 */
class DealRoad extends Thread implements GlobalConstant{
    private RequestQueue<RoadRequest> queue;
    DealRoad(RequestQueue<RoadRequest> queue){
        this.queue = queue;
    }
    public void run(){
        while(true){
            Main.modifyRoad(queue.getAllRequest());
            Main.outPutInfoToTerminal(" 完成道路修改.");
        }
    }
}
