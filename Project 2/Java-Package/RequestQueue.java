package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ****** on 2017-03-12.
 */
public class RequestQueue {
    private List<String> requestSets = null; //request set for initial version
    private int indexOfFetch;
    public RequestQueue(){
        requestSets = new ArrayList<>();
        indexOfFetch = 0;
    }
    public void addRequest(String request){
        this.requestSets.add(request);
    }
    public String getRequestNext(){
        return requestSets.get(indexOfFetch++);
    }
    public boolean haveNext(){
        return indexOfFetch < requestSets.size();
    }
    public int getIndexOfFetch(){
        return indexOfFetch;
    }
    public int getSizeOfQueue(){
        return requestSets.size();
    }
    public String getRequestAt(int position){
        return requestSets.get(position);
    }
    public void delRequestAt(int position){
        requestSets.remove(position);
    }
}
