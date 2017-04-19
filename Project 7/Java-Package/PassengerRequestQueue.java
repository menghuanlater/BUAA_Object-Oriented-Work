package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 * 乘客请求队列
 */
class PassengerRequestQueue implements GlobalConstant {
    private List<PassengerRequest> requestSets = new ArrayList<>();
    synchronized void addRequest(List<PassengerRequest> requests){
        if(requests.size()>0) {
            requestSets.addAll(requests);
            notify();
        }
    }
    synchronized List<PassengerRequest> getAllRequest(){
        if(requestSets.size()==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<PassengerRequest> temp = new ArrayList<>();
        temp.addAll(requestSets); //全部转存,交出去
        requestSets.clear();//清空
        return temp;
    }
}
