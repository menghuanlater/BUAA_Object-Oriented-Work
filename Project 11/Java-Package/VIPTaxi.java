package core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created on 2017-05-14.
 */
class VIPTaxi extends NMTaxi {
    /*
     Overview:可追踪出租车,归为VIP出租车(可以走断路),继承自普通出租车,新增记录服务历史的属性和方法
     */
    //记录所有接单请求的队列
    private final List<PassengerRequest> trackRequest = new ArrayList<>();
    //记录所有接客的行驶路线
    private final List<ArrayList<Integer>> trackPickPassenger = new ArrayList<>();
    //记录所有服务的行驶路线
    private final List<ArrayList<Integer>> trackServePassenger = new ArrayList<>();
    //三个arrayList的元素个数保持一致
    private int count;//记录数
    VIPTaxi(int code) {
        /*
        @REQUIRES:0<=code<=99
        @MODIFIES:this.count
        @EFFECTS:先构造父类,再构造子类,置count为0
         */
        super(code);
        count = 0;
    }
    /*@repOk
    check:1.count>=0
    2:if(trackRequest.size==count)==> trackPickPassenger.size == count && trackServePassenger.size == count
      else ==> (trackRequest.size(),trackPickPassenger.size(),trackServePassenger.size()) all in range [count,count+1]
    3:\all trackRequest.get(i) != null && trackRequest.get(i) is instanceof PassengerRequest &&
        trackRequest.get(i).legacy == true,0<=i<trackRequest.size
    4:trackPickPassenger.get(i) != null && trackPickPassenger.get(i) is instanceof ArrayList &&
           (\all List<Integer> temp = trackPickPassenger.get(i), 0<=temp.get(j)<=6399,0<=j<temp.size()),
           0<=i<trackPickPassenger.size
    5.trackServePassenger.get(i) != null && trackServePassenger.get(i) is instanceof ArrayList &&
           (\all List<Integer> temp = trackServePassenger.get(i), 0<=temp.get(j)<=6399,0<=j<temp.size()),
           0<=i<trackServePassenger.size
    6.super.repOk() == true
     */
    @Override
    public synchronized boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
        @THREAD_REQUIRES:\locked(this)
        @THREAD_EFFECTS:\locked()
         */
        if(count<0) return false;
        if(trackRequest.size()==count){
            if(trackPickPassenger.size()!=count || trackServePassenger.size()!=count)
                return false;
        }else{
            if(trackRequest.size()<count || trackRequest.size()>count+1 || trackPickPassenger.size()<count ||
                    trackPickPassenger.size()>count+1 || trackServePassenger.size()<count || trackServePassenger.size()>count+1)
                return false;
        }
        for (PassengerRequest aTrackRequest : trackRequest) {
            Object x = aTrackRequest;
            if (x == null || !(x instanceof PassengerRequest)) return false;
            if (!aTrackRequest.isLegacy()) return false;
        }
        for (ArrayList<Integer> aTrackPickPassenger : trackPickPassenger) {
            Object y = aTrackPickPassenger;
            if (y == null || !(y instanceof ArrayList)) return false;
            ArrayList<Integer> _y = aTrackPickPassenger;
            for (Integer a_y : _y) {
                if (a_y < 0 || a_y >= NODE_NUM)
                    return false;
            }
        }
        for (ArrayList<Integer> aTrackServePassenger : trackServePassenger) {
            Object z = aTrackServePassenger;
            if (z == null || !(z instanceof ArrayList)) return false;
            ArrayList<Integer> _z = aTrackServePassenger;
            for (Integer a_z : _z) {
                if (a_z < 0 || a_z >= NODE_NUM)
                    return false;
            }
        }
        return super.repOk();
    }
    //整个请求服务全部完成后,更新count
    @Override
    synchronized void completeService(){
        /*
        @MODIFIES:this.count
        @EFFECTS: this.count == \old(this.count)+1
        @THREAD_REQUIRES:\locked(this.count)
        @THREAD_EFFECTS:\locked()
         */
        this.count++;
    }
    //增加服务请求
    @Override
    synchronized void addRequest(PassengerRequest obj){
        /*
        @REQUIRES:obj.legacy == true
        @MODIFIES:this.trackRequest
        @EFFECTS: trackRequest insert obj
        @THREAD_REQUIRES:\locked(this.trackRequest)
        @THREAD_EFFECTS:\locked()
         */
        this.trackRequest.add(obj);
    }
    //增加接客服务路线
    @Override
    synchronized void addPickPath(int code){
        /*
        @REQUIRES:0<=code<=6399
        @MODIFIES:trackPickPassenger.get(count)
        @EFFECTS:trackPickPassenger.get(count) insert code
        @THREAD_REQUIRES:\locked(this.trackPickPassenger)
        @THREAD_EFFECTS:\locked()
         */
        if(trackPickPassenger.size()<=count){
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(code);
            trackPickPassenger.add(temp);
        }else
            trackPickPassenger.get(count).add(code);
    }
    //增加服务路线
    @Override
    synchronized void addServePath(int code){
        /*
        @REQUIRES:0<=code<=6399
        @MODIFIES:trackServePassenger.get(count)
        @EFFECTS:trackServePassenger.get(count) insert code
        @THREAD_REQUIRES:\locked(this.trackServePassenger)
        @THREAD_EFFECTS:\locked()
         */
        if(trackServePassenger.size()<=count){
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(code);
            trackServePassenger.add(temp);
        }else
            trackServePassenger.get(count).add(code);
    }
    //乘客请求迭代器
    @Override
    synchronized ListIterator<PassengerRequest> getRequestItr(){
        /*
        @EFFECTS:新建一个trackRequest的副本,返回副本的双向迭代器
        @THREAD_REQUIRES:\locked(this.trackRequest)
        @THREAD_EFFECTS:\locked()
         */
        List<PassengerRequest> temp = new ArrayList<>();
        temp.addAll(this.trackRequest);
        return temp.listIterator();
    }
    //接客路线迭代器
    @Override
    synchronized ListIterator<ArrayList<Integer>> getPickItr(){
        /*
        @EFFECTS:新建一个trackPickPassenger的副本,返回副本的双向迭代器
        @THREAD_REQUIRES:\locked(this.trackPickPassenger)
        @THREAD_EFFECTS:\locked()
         */
        List<ArrayList<Integer>> temp = new ArrayList<>();
        for (ArrayList<Integer> aTrackPickPassenger : this.trackPickPassenger) {
            ArrayList<Integer> x = new ArrayList<>();
            x.addAll(aTrackPickPassenger);
            temp.add(x);
        }
        return temp.listIterator();
    }
    //服务路线迭代器
    @Override
    synchronized ListIterator<ArrayList<Integer>> getServeItr(){
        /*
        @EFFECTS:新建一个trackServePassenger的副本,返回副本的双向迭代器
        @THREAD_REQUIRES:\locked(this.trackServePassenger)
        @THREAD_EFFECTS:\locked()
         */
        List<ArrayList<Integer>> temp = new ArrayList<>();
        for (ArrayList<Integer> aTrackServePassenger : this.trackServePassenger) {
            ArrayList<Integer> x = new ArrayList<>();
            x.addAll(aTrackServePassenger);
            temp.add(x);
        }
        return temp.listIterator();
    }
    @Override
    public boolean getJudge(){
        /*
        @EFFECTS: \result == true
         */
        return true;
    }
}
