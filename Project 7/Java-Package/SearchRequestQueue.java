package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 */
class SearchRequestQueue implements GlobalConstant{
    private List<SearchRequest> requestSets = new ArrayList<>();
    synchronized void addRequest(List<SearchRequest> requests){
        if(requests.size()>0) {
            requestSets.addAll(requests);
            notify();
        }
    }
    synchronized SearchRequest getFrontRequest(){
        if(requestSets.size()==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SearchRequest temp = requestSets.get(0);
        requestSets.remove(0);
        return temp;
    }
}
