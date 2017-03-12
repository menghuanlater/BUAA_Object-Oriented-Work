package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ****** on 2017-03-12.
 */
public class RequestQueue {
    private List<String> requestSets = null; //request set for initial version
    private int indexOfFetch;
    private final String LIMIT_INFO = "NULL";
    public RequestQueue(){
        requestSets = new ArrayList<>();
        indexOfFetch = 0;
    }
    public void addRequest(String request){
        this.requestSets.add(request);
    }
    public String getRequestNext(){
        if(indexOfFetch<requestSets.size()) {
            return requestSets.get(indexOfFetch++);
        }else{
            return LIMIT_INFO;
        }
    }
}
