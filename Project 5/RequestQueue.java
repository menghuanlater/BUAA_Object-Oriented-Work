package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-03-12.
 */
class RequestQueue {
    private List<SingleRequest> requestSets = null; //request set for initial version
    private int indexOfFetch;
    RequestQueue(){
        requestSets = new ArrayList<>();
        indexOfFetch = 0;
    }
    synchronized void addRequest(SingleRequest request){
        this.requestSets.add(request);
    }
    synchronized SingleRequest getFrontRequest(){
        return (requestSets.size()>0)? requestSets.get(0):null;
    }
    synchronized void delRequestAt(int position){
        requestSets.remove(position);
    }
    int getSizeOfQueue(){
        return requestSets.size();
    }
    SingleRequest getRequestAt(int position){
        return requestSets.get(position);
    }
    void subIndexOfFetch(){ //use for: turn the main control power to the new not complete picked request.
        indexOfFetch--;
    }
    void setRequestAt(int position,SingleRequest request){
        requestSets.set(position,request);
    }
    SingleRequest getRequestNext(){
        return requestSets.get(indexOfFetch++);
    }
    boolean haveNext(){
        return indexOfFetch < requestSets.size();
    }
    int getIndexOfFetch(){
        return indexOfFetch;
    }
}
