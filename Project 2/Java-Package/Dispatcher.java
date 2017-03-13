package core;

/**
 * Created on 2017-03-13.
 * command schedule
 */
public class Dispatcher {
    private Elevator myElevator;
    private RequestQueue requestQueue;
    public Dispatcher(RequestQueue requestQueue){
        myElevator = new Elevator();
        this.requestQueue = requestQueue;
    }
    //this function is the most important task.
    public void carryOutTheElevator(){
        int beforeRequestTime = 0;
        boolean isFirst = true;
        while(requestQueue.haveNext()){
            checkSameRequest(beforeRequestTime);//check later,reset beforeRequestTime
            SingleRequest request;
            if(requestQueue.haveNext())
                request= new SingleRequest(requestQueue.getRequestNext(),beforeRequestTime);
            else{
                myElevator.outPut();
                return;
            }
            if(!request.isLegalRequest()) //find the request is illegal
                continue;
            //deal the first not start at 0
            if(isFirst){
                if(request.getRequestTime()!=0){
                    System.out.println("The first Legal request is not start at 0,the program exit.");
                    System.exit(0);
                }else
                    isFirst = false;
            }
            myElevator.outPut();
            myElevator.setCompleteRequest(request);
            myElevator.setCompleteTime();
            myElevator.setMoveDire();
            beforeRequestTime = request.getRequestTime();
        }
        myElevator.outPut();
    }
    private void checkSameRequest(int time){
        int loopStart = requestQueue.getIndexOfFetch();
        SingleRequest objRequest = myElevator.getCompleteRequest();
        SingleRequest targetRequest;
        double completeTime = myElevator.getCompleteTime();
        int beforeRequestTime = time;
        String request;

        if(objRequest==null)
            return;
        for(;loopStart<requestQueue.getSizeOfQueue();loopStart++){
            request = requestQueue.getRequestAt(loopStart);
            targetRequest = new SingleRequest(request,beforeRequestTime);
            if(targetRequest.isLegalRequest()){
                if(targetRequest.getRequestTime()>completeTime) //if true,not need loop again.exit the function
                    break;
                beforeRequestTime = targetRequest.getRequestTime();//for next judge.
            }else{
                requestQueue.delRequestAt(loopStart);
                loopStart--;
                continue;
            }
            if(objRequest.getRequestType()==2 && targetRequest.getRequestType()==2){ //--> FR
                if((objRequest.getMoveType()==1 && targetRequest.getMoveType()==1) ||
                        (objRequest.getMoveType()==2 && targetRequest.getMoveType()==2)){//-->UP || DOWN
                    if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){//same floor
                        System.out.println(request+" is redundant,the program will ignore this request.");
                        requestQueue.delRequestAt(loopStart);
                        loopStart--;
                    }
                }
            }else if(objRequest.getRequestType()==1 && targetRequest.getRequestType()==1){//--> ER
                if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){
                    System.out.println(request+" is redundant,the program will ignore this request.");
                    requestQueue.delRequestAt(loopStart);
                    loopStart--;
                }
            }
        }
    }
}
