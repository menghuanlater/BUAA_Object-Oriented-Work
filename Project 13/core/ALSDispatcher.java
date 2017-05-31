package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-03-19.
 * we use extend to reuse Dispatcher class
 * */
public class ALSDispatcher extends Dispatcher implements ElevatorConstant {
    public ALSDispatcher(RequestQueue requestQueue){ //with parameter
        /*
        @REQUIRES: requestQueue!= null && requestQueue.size>0 && requestQueue.get(0) is [FR,1,UP,0] &&
         (\all temp = requestQueue.get(i), temp.legacy == true && (if j=i-1 >=0) ==> requestQueue.get(j).requestTime<= temp.requestTime
           ,0<=i<requestQueue.size())
        @MODIFIES:super
        @EFFECTS:对象实例化
         */
        super(requestQueue);
    }
    /**rewrite the carryOutTheElevator()
    *  achieve the elevator main task.
    * */
    public void carryOutTheElevator() throws NoNextRequestException {
        /*
        @REQUIRES:类正确构造
        @MODIFIES:super
        @EFFECTS:遍历取出请求队列中所有的请求,每次设置一个主请求,首先扫描之后的请求队列中是否存在与主请求同质的请求,找到就删除
          ,之后扫描队列中是否存在可直接稍待完成的请求,如果存在,选择最先可捎带完成的请求(可以是两个并列的最优捎带),扫描将请求队列中
          与目前即将先捎带完成的请求同质的请求删除,再通知电梯先执行捎带请求,之后主请求再次发起队列扫描,扫描与自身的同质请求并
          删除直到主请求已经没有可以直接捎带完成的请求为止,然后主请求扫描队列,搜索是否存在可捎带但是不能直接完成的请求,有则选择最优的
          请求作为下一个主请求,将其调到请求队列的前端.否则下一个主请求就是队列的下一个请求.完成当前的主请求,进行主请求的交替更换,
          重复上述操作,直至请求全部执行完毕.
          代码存在问题对队列的非法操作会引起抛出异常.
         */
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
    public boolean pickDirectRequest() throws NoNextRequestException {
        /*
        @REQUIRES:类正确构造
        @MODIFIES:super
        @EFFECTS:根据当前电梯执行的主请求,从请求队列中找到可直接捎带完成的请求(选择最先可被捎带的),完成捎带请求,重设电梯的
        相关状态.
        代码对请求队列非法操作会引起异常(如果代码存在问题)
         */
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
    public void addOrAbandon(List<Pick> tempStack,SingleRequest targetRequest,int indexOfFetch){
        /*
        @REQUIRES:tempStack!=null && targetRequest!=null && targetRequest.legacy == true && indexOfFetch >= 0
        @MODIFIES:tempStack
        @EFFECTS:if(\all temp = tempStack.get(i).request,temp.Type!= targetRequest.type,0<=i<tempStack.size)
                    ==> tempStack == \old(tempStack) + new Pick(indexOfFetch,targetRequest);
                 else ==>  do nothing.
         */
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
    public boolean findPickedPower() throws NoNextRequestException {
        /*
        @REQUIRES:类正确的构造
        @MODIFIES:super
        @EFFECTS:查询主请求接下来的请求队列,查询是否存在可捎带但是不能直接完成的请求,有则选择第一个(FCFS)
                 设置为下一个主请求(通过调整其在请求队列中的位置实现),返回true,否则返回false.
                 非法操作请求队列触发异常
         */
        SingleRequest objRequest = myElevator.getCompleteRequest();
        int elevatorStatus = myElevator.getMoveDire();
        if(objRequest==null || elevatorStatus==STATUS_STILL) //exit judge
            return false;
        for(int loopStart = requestQueue.getIndexOfFetch();loopStart<requestQueue.getSizeOfQueue();loopStart++) {
            SingleRequest targetRequest = requestQueue.getRequestAt(loopStart);
            if(targetRequest.getRequestTime()>=myElevator.getCompleteTime()-openCloseInterval)
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
