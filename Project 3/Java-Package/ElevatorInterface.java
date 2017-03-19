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
}