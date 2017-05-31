package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-03-12.
 */
public class RequestQueue {
    /*
    Overview:请求队列类
     */
    private List<SingleRequest> requestSets = null; //request set for initial version
    private int indexOfFetch;
    public RequestQueue(){
        /*
        @MODIFIES:this
        @EFFECTS:requestSets is instanceof ArrayList && indexOfFetch==0
         */
        requestSets = new ArrayList<>();
        indexOfFetch = 0;
    }
    public boolean addRequest(SingleRequest request){
        /*
        @MODIFIES:this.requestSets
        @EFFECTS:if(request==null) ==> \result == false;
                if(request.legacy == false) == > \result == false;
                if(requestSets.contains(request)==true) ==> \result == false//同一个的实例化请求不可以重复添加
                (this.requestSets == \old(this.requestSets) + request) && \result == true
         */
        if(request==null || !request.isLegalRequest() || isContain(request))
            return false;
        else
            this.requestSets.add(request);
        return true;
    }
    public SingleRequest getRequestNext() throws NoNextRequestException {
        /*
        @MODIFIES: this.indexOfFetch
        @EFFECTS: if(haveNext()==true) ==> (\result == requestSets.get(indexOfFetch)) && (indexOfFetch == \old(indexOfFetch)+1)
                 else ==> exceptional_behavior:(NoNextRequestException)输出提示信息
         */
        if(haveNext())
            return requestSets.get(indexOfFetch++);
        else{
            throw new NoNextRequestException("请求队列已经取完,无下一个请求");
        }
    }
    public boolean haveNext(){
        /*
        @EFFECTS: if(indexOfFetch < requestSets.size()) ==> \result == true
                  else ==> \result == false
         */
        return indexOfFetch < requestSets.size();
    }
    public int getIndexOfFetch(){
        /*
        @EFFECTS:\result == indexOfFetch
         */
        return indexOfFetch;
    }
    public int getSizeOfQueue(){
        /*
        @EFFECTS: \result == requestSets.size()
         */
        return requestSets.size();
    }
    public SingleRequest getRequestAt (int position)throws NoNextRequestException{
        /*
        @EFFECTS:if(position < requestSets.size() && position>=0) ==> \result == requestSets.get(i)
            else ==> exceptional_behavior(NoNextRequestException)输出提示信息
         */
        if(position<requestSets.size() && position>=0)
            return requestSets.get(position);
        else
            throw new NoNextRequestException("index at "+position+" has no request");
    }
    public boolean delRequestAt(int position){
        /*
        @MODIFIES:this.requestSets
        @EFFECTS:if(position < requestSets.size() && position>=0) ==> (\result == true) &&
                    (requestSets == \old(requestSets)-\old(requestSets).get(position))
                   else ==> \result == false
         */
        if(position<requestSets.size() && position>=0) {
            requestSets.remove(position);
            return true;
        }
        return false;
    }
    //为junit测试写的方法,判断add和del方法是否正确执行,判断引用
    public boolean isContain(SingleRequest request){
        /*
        @EFFECTS:if(request==null || request.legacy==false) ==> \result == false;
                if(requestSets.contain(request)==false) ==> \result == false;
                \result == true
         */
        if(request==null || !request.isLegalRequest())
            return false;
        for (SingleRequest requestSet : requestSets) {
            if (requestSet == request)
                return true;
        }
        return false;
    }
    public boolean subIndexOfFetch(){ //use for: turn the main control power to the new not complete picked request.
        /*
        @MODIFIES:this.indexOfFetch
        @EFFECTS:if(indexOfFetch==0) ==> \result == false
                else ==> \result == true  && indexOfFetch == \old(indexOfFetch)-1
         */
        if (indexOfFetch==0)
            return false;
        indexOfFetch--;
        return true;
    }
    public boolean setRequestAt(int position,SingleRequest request){
        /*
        @MODIFIES:requestSets
        @EFFECTS:if(position<0 || position>=requestSets.size || request==null || request.legacy==false) ==> \result == false;
                 (\result == true) && (requestSets[position] == request) && (requestSets.contains(\old(requestSets[position]))==false)
         */
        if(position<0 || position>=requestSets.size())
            return false;
        if(request==null || !request.isLegalRequest())
            return false;
        requestSets.set(position,request);
        return true;
    }
}
