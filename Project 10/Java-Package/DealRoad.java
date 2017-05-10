package core;

/**
 * Created on 2017-04-30.
 * 处理道路请求
 */
public class DealRoad extends Thread implements GlobalConstant{
    /*
    Overview:从道路请求队列抓取道路修改请求,实现道路的修改
     */
    private RequestQueue<RoadRequest> queue;
    DealRoad(RequestQueue<RoadRequest> queue){
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
        @EFFECTS:\result = invariant(this)
         */
        if(queue==null) return false;
        Object x = queue;
        if(!(x instanceof RequestQueue)) return false;
        return true;
    }
    public void run(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:不停扫描请求队列，没有则等待，有则取出调用Main中的方法修改道路
        */
        while(true){
            Main.modifyRoad(queue.getAllRequest());
        }
    }
}
