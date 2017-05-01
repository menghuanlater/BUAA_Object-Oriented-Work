package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-05-01.
 */
class RequestQueue<T> implements GlobalConstant{
    private List<T> requestSets = new ArrayList<T>();
    synchronized void addRequest(List<T> requests){
        if(requests.size()>0) {
            requestSets.addAll(requests);
            notify();
        }
    }
    synchronized T getFrontRequest(){
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
