package core;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-04-02.
 * this class add thread at the base of ALSDispatcher
 */
public class ALSThreadDispatcher extends ALSDispatcher implements Runnable,ElevatorConstant{
    static final Floors floors = new Floors();
    private ThreadElevator[] elevators = new ThreadElevator[ELE_NUM];
    private final List<SingleRequest> bufferRequest = new ArrayList<>();
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");//standard
    ALSThreadDispatcher(RequestQueue requestQueue) {
        super(requestQueue);
        for(int i=0;i<ELE_NUM;i++)
            elevators[i] = new ThreadElevator(i+1);
    }
    public void run(){
        startElevators();
        while(true) {
            //first we scan the buffer queue, see whether exist elev can react to.
            //and we will re use chooseElevatorExec
            if(Main.inputHandle.getState()== Thread.State.TERMINATED && bufferRequest.size()==0 && isAllStill()){
                stopElevators();
                break;
            }
            while(bufferRequest.size()>0){
                SingleRequest objRequest = bufferRequest.get(0);
                int ele = chooseElevatorExec(objRequest,false);
                if(ele>0){
                    if(objRequest.getMoveType()==STATUS_UP){
                        floors.setExecUpElevatorAt(objRequest.getTargetFloor()-1,ele);
                    }else if(objRequest.getMoveType()==STATUS_DOWN){
                        floors.setExecDownElevatorAt(objRequest.getTargetFloor()-1,ele);
                    }
                    if(elevators[ele-1].getMoveDire()==STATUS_STILL)
                        elevators[ele-1].dispatcherSetMoveDire(objRequest.getTargetFloor());
                }else
                    bufferRequest.add(objRequest);
                bufferRequest.remove(0);//remove the front buffered request.
                //if not pick,will add to the end of the queue.
            }
            //next scan the new queue. but maybe is empty.
            SingleRequest objRequest;
            while ((objRequest = requestQueue.getFrontRequest()) != null) {
                if (isSameRequest(objRequest)) {
                    try {
                        Main.bufferedWriter.write(Main.getStandardOSTime() + ":SAME [" + objRequest.getRequestStr() + " ," +
                                decimalFormat.format(Math.floor(objRequest.getRequestTime())) + "]\n");
                        Main.bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (objRequest.getRequestType() == INNER_REQUEST) {//ER
                        int ele = objRequest.getTargetEle();
                        elevators[ele - 1].lightButtonAt(objRequest.getTargetFloor() - 1, objRequest.getRequestTime());
                        if (elevators[ele - 1].getMoveDire() == STATUS_STILL)
                            elevators[ele - 1].dispatcherSetMoveDire(objRequest.getTargetFloor());
                        if (elevators[ele - 1].getMoveDire() == STATUS_OPEN_CLOSE && elevators[ele - 1].getStillToMove() == STATUS_STILL) {
                            elevators[ele - 1].setStillToMove(objRequest.getTargetFloor());
                        }
                    } else if (objRequest.getRequestType() == OUTER_REQUEST) {//FR
                        //need to choose which elevator exec this request.
                        int targetFloor = objRequest.getTargetFloor();
                        int ele = chooseElevatorExec(objRequest, true);
                        if (objRequest.getMoveType() == STATUS_UP) {
                            floors.setFloorUpLightStatusAt(targetFloor - 1, true);
                            floors.setLightUpTimeAt(targetFloor - 1, objRequest.getRequestTime());
                            floors.setExecUpElevatorAt(targetFloor - 1, ele);
                        } else if (objRequest.getMoveType() == STATUS_DOWN) {
                            floors.setFloorDownLightStatusAt(targetFloor - 1, true);
                            floors.setLightDownTimeAt(targetFloor - 1, objRequest.getRequestTime());
                            floors.setExecDownElevatorAt(targetFloor - 1, ele);
                        }
                        if (ele == 0) bufferRequest.add(objRequest);
                        else if (elevators[ele - 1].getMoveDire() == STATUS_STILL)
                            elevators[ele - 1].dispatcherSetMoveDire(objRequest.getTargetFloor());
                    }
                }
                requestQueue.delRequestAt(0);
            }
        }
    }
    //start three elevators
    private void startElevators(){
        for(int i=0;i<ELE_NUM;i++)
            elevators[i].start();
    }
    //stop all the elevators
    private void stopElevators(){
        for(int i=0;i<ELE_NUM;i++)
            elevators[i].stop();
    }
    //check whether all the elevators' status is still
    private boolean isAllStill(){
        boolean flag = true;
        for(int i=0;i<ELE_NUM;i++){
            if(elevators[i].getMoveDire()!=STATUS_STILL){
                flag = false;
                break;
            }
        }
        return flag;
    }
    //check for same request.
    private boolean isSameRequest(SingleRequest targetRequest){
        if(targetRequest.getRequestType()==INNER_REQUEST){
            int ele = targetRequest.getTargetEle();
            return elevators[ele-1].getButtonAt(targetRequest.getTargetFloor()-1);
        }else if(targetRequest.getRequestType()==OUTER_REQUEST){
            int dire = targetRequest.getMoveType();
            if(dire == STATUS_UP){
                return floors.getFloorUpLightStatusAt(targetRequest.getTargetFloor()-1);
            }else if(dire == STATUS_DOWN){
                return floors.getFloorDownLightStatusAt(targetRequest.getTargetFloor()-1);
            }
        }
        return false;
    }
    //choose the exec elevator for each request.when mode is true,serve for regular/normal request
    //when mode is false,serve for buffer request.
    private int chooseElevatorExec(SingleRequest targetRequest,boolean mode) {
        int targetFloor = targetRequest.getTargetFloor();
        int dire = targetRequest.getMoveType();
        List<ThreadElevator> pickAbleEle = new ArrayList<>();
        for (int i = 0; mode && i < ELE_NUM; i++) {
            if (elevators[i].getMoveDire() == STATUS_UP && dire == STATUS_UP && elevators[i].getCurrentFloor() < targetFloor &&
                    elevators[i].limitFloor(floors) >= targetFloor ||
                    elevators[i].getMoveDire() == STATUS_DOWN && dire == STATUS_DOWN && elevators[i].getCurrentFloor() > targetFloor &&
                            elevators[i].limitFloor(floors) <= targetFloor)
                pickAbleEle.add(elevators[i]);
        }
        if (pickAbleEle.size() > 0) {
            int min = pickAbleEle.get(0).getMoveCount();
            int code = pickAbleEle.get(0).getCode();
            for (int i = 1; i < pickAbleEle.size(); i++) {
                if (pickAbleEle.get(i).getMoveCount() < min) {min = pickAbleEle.get(i).getMoveCount();
                    code = pickAbleEle.get(i).getCode();
                }
            }
            return code;
        } else {
            for (int i = 0; i < ELE_NUM; i++) {
                if (elevators[i].getMoveDire() == STATUS_STILL)
                    pickAbleEle.add(elevators[i]);
            }
            if (pickAbleEle.size() > 0) {
                int min = pickAbleEle.get(0).getMoveCount();
                int code = pickAbleEle.get(0).getCode();
                for (int i = 1; i < pickAbleEle.size(); i++) {
                    if (pickAbleEle.get(i).getMoveCount() < min) {
                        min = pickAbleEle.get(i).getMoveCount();
                        code = pickAbleEle.get(i).getCode();
                    }
                }
                return code;
            } else
                return 0;
        }
    }
}
