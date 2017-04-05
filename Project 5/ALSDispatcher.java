package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-03-19.
 * we use extend to reuse Dispatcher class
 * we build a local class to record the pick able requests -->we only need one instance
 * */
class Pick{
    private int indexOfFetch;
    private SingleRequest pickAbleRequest;
    Pick(int indexOfFetch,SingleRequest pickAbleRequest){
        this.indexOfFetch = indexOfFetch;
        this.pickAbleRequest = pickAbleRequest;
    }
    int getIndexOfFetch(){
        return indexOfFetch;
    }
    SingleRequest getPickAbleRequest(){
        return pickAbleRequest;
    }
}
class ALSDispatcher extends Dispatcher implements ElevatorConstant {
    ALSDispatcher(RequestQueue requestQueue){ //with parameter
        super(requestQueue);
    }
    /**rewrite the carryOutTheElevator()
    *  achieve the elevator main task.
    * */
    void carryOutTheElevator(){
        while(requestQueue.haveNext()){
            //check later,reset beforeRequestTime
            checkSameRequest(requestQueue.getIndexOfFetch(),myElevator.getCompleteTime(),myElevator.getCompleteRequest());
            //if the while continue,we continue the main scan continue.
            while(pickDirectRequest())/* Find all can piggyback request,and it is a loop*loop... */
                checkSameRequest(requestQueue.getIndexOfFetch(),myElevator.getCompleteTime(),myElevator.getCompleteRequest());
            boolean isPickedPower = findPickedPower();//exist not complete picked request.Just for ER
            if(isPickedPower)
                requestQueue.subIndexOfFetch();
            SingleRequest request;
            if(requestQueue.haveNext())
                request = requestQueue.getRequestNext();
            else{
                myElevator.outPut();
                return;
            }
            myElevator.outPut();
            myElevator.setCompleteRequest(request);
            myElevator.setCompleteTime();
            myElevator.setMoveDire();
        }
        myElevator.outPut();
    }
    //resolve problem of pick request(those we can resolve before the main request)
    //checkPick() is use as common section of pickDirectRequest.
    private boolean pickDirectRequest(){
        int loopStart = requestQueue.getIndexOfFetch();
        //SingleRequest objRequest = myElevator.getCompleteRequest();
        if(myElevator.getMoveDire()==STATUS_STILL)
            return false;
        List<Pick> tempStack = new ArrayList<>();
        int bestPickFloor = (myElevator.getMoveDire() == STATUS_UP)? (MAX_FLOOR+1) : (MIN_FLOOR-1);
        for(;loopStart<requestQueue.getSizeOfQueue();loopStart++){
            SingleRequest targetRequest = requestQueue.getRequestAt(loopStart);
            if(targetRequest.getRequestTime()>=myElevator.getCompleteTime()-openCloseInterval)
                break;
            if(targetRequest.getRequestType()==OUTER_REQUEST){ //FR
                if(myElevator.getMoveDire() == STATUS_UP && targetRequest.getMoveType() == STATUS_UP){
                    if(myElevator.isAblePick(targetRequest.getRequestTime(), targetRequest.getTargetFloor(),true)
                            && myElevator.getCompleteRequest().getTargetFloor() >= targetRequest.getTargetFloor()){
                        if(targetRequest.getTargetFloor()<bestPickFloor){
                            bestPickFloor = targetRequest.getTargetFloor();
                            tempStack.clear();
                            tempStack.add(new Pick(loopStart,targetRequest));
                        }else if(targetRequest.getTargetFloor()==bestPickFloor)
                            addOrAbandon(tempStack,targetRequest,loopStart);
                    }
                }else if(myElevator.getMoveDire() == STATUS_DOWN && targetRequest.getMoveType() == STATUS_DOWN){
                    if(myElevator.isAblePick(targetRequest.getRequestTime(), targetRequest.getTargetFloor(),false)
                            && myElevator.getCompleteRequest().getTargetFloor() <= targetRequest.getTargetFloor()){
                        if(targetRequest.getTargetFloor()>bestPickFloor){
                            bestPickFloor = targetRequest.getTargetFloor();
                            tempStack.clear();
                            tempStack.add(new Pick(loopStart,targetRequest));
                        }else if(targetRequest.getTargetFloor()==bestPickFloor)
                            addOrAbandon(tempStack,targetRequest,loopStart);
                    }
                }
            }else if(targetRequest.getRequestType()==INNER_REQUEST) {//ER
                if(myElevator.getMoveDire() == STATUS_UP){
                    if(myElevator.isAblePick(targetRequest.getRequestTime(), targetRequest.getTargetFloor(),true)
                            && myElevator.getCompleteRequest().getTargetFloor() >= targetRequest.getTargetFloor()){
                        if(targetRequest.getTargetFloor()<bestPickFloor){
                            bestPickFloor = targetRequest.getTargetFloor();
                            tempStack.clear();
                            tempStack.add(new Pick(loopStart,targetRequest));
                        }else if(targetRequest.getTargetFloor()==bestPickFloor)
                            addOrAbandon(tempStack,targetRequest,loopStart);
                    }
                }else if(myElevator.getMoveDire() == STATUS_DOWN){
                    if(myElevator.isAblePick(targetRequest.getRequestTime(), targetRequest.getTargetFloor(),false)
                            && myElevator.getCompleteRequest().getTargetFloor() <= targetRequest.getTargetFloor()){
                        if(targetRequest.getTargetFloor()>bestPickFloor){
                            bestPickFloor = targetRequest.getTargetFloor();
                            tempStack.clear();
                            tempStack.add(new Pick(loopStart,targetRequest));
                        }else if(targetRequest.getTargetFloor()==bestPickFloor)
                            addOrAbandon(tempStack,targetRequest,loopStart);
                    }
                }
            }
        }
        if(tempStack.size()==0)
            return false;
        else{
            for(int i=tempStack.size() - 1;i >= 0;i--){
                SingleRequest objRequest = tempStack.get(i).getPickAbleRequest();
                checkSameRequest(tempStack.get(i).getIndexOfFetch()+1,myElevator.getArriveTime(objRequest.getTargetFloor())+openCloseInterval,
                        objRequest);
                requestQueue.delRequestAt(tempStack.get(i).getIndexOfFetch());
            }
            for (Pick aTempStack : tempStack) {
                SingleRequest objRequest = aTempStack.getPickAbleRequest();
                myElevator.accomplishPickedRequest(objRequest);
            }
            if(tempStack.get(0).getPickAbleRequest().getTargetFloor()!=myElevator.getCompleteRequest().getTargetFloor())
                myElevator.resetMemberVars(tempStack.get(0).getPickAbleRequest().getTargetFloor());
            return true;
        }
    }
    private void addOrAbandon(List<Pick> tempStack,SingleRequest targetRequest,int indexOfFetch){
        boolean addFlag = true;
        for (Pick aTempStack : tempStack) {
            SingleRequest objRequest = aTempStack.getPickAbleRequest();
            if (objRequest.getRequestType()==targetRequest.getRequestType()) {
                addFlag = false;
                break;
            }
        }
        if(addFlag)
            tempStack.add(new Pick(indexOfFetch,targetRequest));
    }
    private boolean findPickedPower(){
        SingleRequest objRequest = myElevator.getCompleteRequest();
        int elevatorStatus = myElevator.getMoveDire();
        if(objRequest==null || elevatorStatus==STATUS_STILL) //exit judge
            return false;
        for(int loopStart = requestQueue.getIndexOfFetch();loopStart<requestQueue.getSizeOfQueue();loopStart++) {
            SingleRequest targetRequest = requestQueue.getRequestAt(loopStart);
            if(targetRequest.getRequestTime()>myElevator.getCompleteTime()-openCloseInterval)
                break;
            if(targetRequest.getRequestType()==INNER_REQUEST && (elevatorStatus==STATUS_UP &&
                    targetRequest.getTargetFloor()>objRequest.getTargetFloor() || elevatorStatus==STATUS_DOWN &&
                targetRequest.getTargetFloor()<objRequest.getTargetFloor())){
                int index = requestQueue.getIndexOfFetch();
                requestQueue.setRequestAt(index-1,targetRequest);
                requestQueue.delRequestAt(loopStart);
                return true;
            }
        }
        return false;
    }
}
