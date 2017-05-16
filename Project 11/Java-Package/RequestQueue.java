package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-05-01.
 */
public class RequestQueue<T> implements GlobalConstant{
    /*
    Overview:请求队列类,负责存储输入发送的请求,取出发送给处理线程
     */
    private List<T> requestSets = new ArrayList<T>();

    /*@repOk.
    check:requestSets!=null
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        return requestSets != null;
    }

    synchronized void addRequest(List<T> requests){
        /*@REQUIRES:request！=null
        @MODIFIES:requestSets
        @EFFECTS:add request all to requestSets
        @THREAD_REQUIRES:\locked(requestSets)
        @THREAD_EFFECTS:\locked();方法同步
        */
        //assert this.repOk();
        if(requests.size()>0) {
            requestSets.addAll(requests);
            notifyAll();
        }
    }

    synchronized T getFrontRequest(){
        /*@REQUIRES:None
        @MODIFIES:requestSets
        @EFFECTS:normal_behavior:取出队列最前的请求,返回,如果没有请求,等待
                 Thread.wait()异常==>exceptional_behavior:(InterruptedException)打印异常处理栈
        @THREAD_REQUIRES:\locked(requestSets)
        @THREAD_EFFECTS:\locked();方法同步
        */
        //assert this.repOk();
        if(requestSets.size()==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        T temp = requestSets.get(0);
        requestSets.remove(0);
        return temp;
    }
    synchronized List<T> getAllRequest(){
        /*@REQUIRES:None
        @MODIFIES:requestSets
        @EFFECTS:normal_behavior:取出队列当前所有的请求,返回,如果没有请求,等待
                 Thread.wait()异常==>exceptional_behavior:(InterruptedException)打印异常处理栈
        @THREAD_REQUIRES:\locked(requestSets)
        @THREAD_EFFECTS:\locked();方法同步
        */
        //assert this.repOk();
        if(requestSets.size()==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<T> temp = new ArrayList<>();
        temp.addAll(requestSets); //全部转存,交出去
        requestSets.clear();//清空
        return temp;
    }
}
