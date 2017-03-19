package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-03-12.
 */
class RequestQueue {
    private List<String> requestSets = null; //request set for initial version
    private int indexOfFetch;
    RequestQueue(){
        requestSets = new ArrayList<>();
        indexOfFetch = 0;
    }
    void addRequest(String request){
        this.requestSets.add(request);
    }
    String getRequestNext(){
        return requestSets.get(indexOfFetch++);
    }
    boolean haveNext(){
        return indexOfFetch < requestSets.size();
    }
    int getIndexOfFetch(){
        return indexOfFetch;
    }
    int getSizeOfQueue(){
        return requestSets.size();
    }
    String getRequestAt(int position){
        return requestSets.get(position);
    }
    void delRequestAt(int position){
        requestSets.remove(position);
    }
}
