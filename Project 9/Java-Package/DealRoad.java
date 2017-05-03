package core;

/**
 * Created on 2017-04-30.
 * 处理道路请求
 */
class DealRoad extends Thread implements GlobalConstant{
    private RequestQueue<RoadRequest> queue;
    DealRoad(RequestQueue<RoadRequest> queue){
        /*@REQUIRES:queue!=null
        @MODIFIES:this.queue
        @EFFECTS:构造
        */
        this.queue = queue;
    }
    public void run(){
        /*@REQUIRES:None
        @MODIFIES:this.queue(内部实现修改)
        @EFFECTS:不停扫描请求队列，没有则等待，有则取出修改道路情况
        */
        while(true){
            Main.modifyRoad(queue.getAllRequest());
            Main.outPutInfoToTerminal(" 完成道路修改.");
        }
    }
}
