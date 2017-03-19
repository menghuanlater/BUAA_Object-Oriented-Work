package core;

/**
 * Created on 2017-03-19.
 * we use extend to reuse Dispatcher class
 */
class ALSDispatcher extends Dispatcher {

    ALSDispatcher(RequestQueue requestQueue){ //with parameter
        super(requestQueue);
    }
    /**rewrite the carryOutTheElevator()
    *  achieve the elevator main task.
    * */
    void carryOutTheElevator(){
        int beforeRequestTime = 0;
        boolean isFirst = true;
        while(requestQueue.haveNext()){
            checkSameRequest(beforeRequestTime);
            SingleRequest request;
            if(requestQueue.haveNext())
                request = new SingleRequest(requestQueue.getRequestNext(),beforeRequestTime);
            else{
                myElevator.outPut();
                return;
            }
            if(!request.isLegalRequest()) //find the request is illegal
                continue;
            //deal the first not start at 0
            if(isFirst){
                if(!(request.getRequestTime()==0 && request.getTargetFloor()==1 && request.getRequestType()==OUTER_REQUEST
                        && request.getMoveType()==STATUS_UP)){
                    Main.illegalMessage.add("The first Legal request is not (FR,1,UP,0),the program will exit.");
                    Main.outIllegalMessage();
                }else
                    isFirst = false;
            }
            pickOtherRequests();//Find all can piggyback request
            myElevator.outPut();
            myElevator.setCompleteRequest(request);
            myElevator.setCompleteTime();
            myElevator.setMoveDire();
            beforeRequestTime = request.getRequestTime();
        }
        myElevator.outPut();
    }
    //resolve problem of pick request
    private void pickOtherRequests(){

    }
}
