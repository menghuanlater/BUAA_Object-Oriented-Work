package core;

/**
 * Created on 2017-03-13.
 * command schedule
 * this class is just for the project 2
 */
class Dispatcher implements ElevatorConstant {
    protected Elevator myElevator;
    protected RequestQueue requestQueue;
    Dispatcher(RequestQueue requestQueue){
        myElevator = new Elevator();
        this.requestQueue = requestQueue;
    }
    //this function is the most important task.
    void carryOutTheElevator(){
        while(requestQueue.haveNext()){
            checkSameRequest(requestQueue.getIndexOfFetch(),myElevator.getCompleteTime(),myElevator.getCompleteRequest());
            //check later,reset beforeRequestTime
            SingleRequest request;
            if(requestQueue.haveNext())
                request= requestQueue.getRequestNext();
            else{
                myElevator.outPut();
                return;
            }
            //deal the first not start at 0
            myElevator.outPut();
            myElevator.setCompleteRequest(request);
            myElevator.setCompleteTime();
            myElevator.setMoveDire();
        }
        myElevator.outPut();
    }
    void checkSameRequest(int loopStart,double completeTime,SingleRequest objRequest){
        if(objRequest==null)
            return;
        SingleRequest targetRequest;
        for(;loopStart<requestQueue.getSizeOfQueue();loopStart++){
            targetRequest = requestQueue.getRequestAt(loopStart);
            if(targetRequest.getRequestTime()>completeTime) //if true,not need loop again.exit the function
                break;
            if(objRequest.getRequestType()==OUTER_REQUEST && targetRequest.getRequestType()==OUTER_REQUEST){ //--> FR
                if(objRequest.getMoveType()==targetRequest.getMoveType()){//-->UP || DOWN
                    if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){//same floor
                        Main.illegalMessage.add("SAME "+targetRequest.toString());
                        requestQueue.delRequestAt(loopStart);
                        loopStart--;
                    }
                }
            }else if(objRequest.getRequestType()==INNER_REQUEST && targetRequest.getRequestType()==INNER_REQUEST){//--> ER
                if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){
                    Main.illegalMessage.add("SAME "+targetRequest.toString());
                    requestQueue.delRequestAt(loopStart);
                    loopStart--;
                }
            }
        }
    }
}
