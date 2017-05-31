package core;

/**
 * Created on 2017-03-13.
 * command schedule
 * this class is just for the project 2
 */
public class Dispatcher implements ElevatorConstant {
    Elevator myElevator;
    RequestQueue requestQueue;
    public Dispatcher(RequestQueue requestQueue){
        /*
        @REQUIRES: requestQueue!= null && requestQueue.size>0 && requestQueue.get(0) is [FR,1,UP,0] &&
         (\all temp = requestQueue.get(i), temp.legacy == true && (if j=i-1 >=0) ==> requestQueue.get(j).requestTime<= temp.requestTime
           ,0<=i<requestQueue.size())
        @MODIFIES:this
        @EFFECTS: 类对象实例化
         */
        myElevator = new Elevator();
        this.requestQueue = requestQueue;
    }
    //检查同质请求
    public void checkSameRequest(int loopStart,double completeTime,SingleRequest objRequest) throws NoNextRequestException {
        /*
        @REQUIRES:loopStart>=0 && completeTime>=0.0 && (if objRequest!=null ==> onjRequest.legacy == true)
        @MODIFIES:this.requestQueue
        @EFFECTS:当objRequest!=null时从请求队列的下标loopStart开始,之后队列中所有的与objRequest同质的请求全部删除
                如果程序写的不对,会触发NoNextRequestException异常
         */
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
    //为了junit测试而加的get
    public Elevator getMyElevator(){
        /*
        @EFFECTS:\result == this.myElevator
         */
        return myElevator;
    }
}
