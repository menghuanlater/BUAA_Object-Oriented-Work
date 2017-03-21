package core;

/**
 * Created on 2017-03-20.
 * Interface of Elevator class
 */
interface ElevatorInterface{
    void setCompleteRequest(SingleRequest completeRequest);
    void setMoveDire();
    void setCompleteTime();
    void outPut();
    double getCompleteTime();
    SingleRequest getCompleteRequest();
    int getMoveDire();
    double getArriveTime(int targetFloor);
    //int getCurrentStatus(int requestTime);
    //int getCurrentFloor(int requestTime);
    boolean isAblePick(int requestTime, int targetFloor,boolean mode);
    void resetMemberVars(int pickFloor);
    void accomplishPickedRequest(SingleRequest objRequest);
}