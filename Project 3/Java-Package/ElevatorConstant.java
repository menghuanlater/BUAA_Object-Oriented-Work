package core;

/**
 * Created on 2017-03-13.
 */
interface ElevatorConstant {
    String ELEVATOR_INNER = "ER"; //instruction of ER
    String ELEVATOR_OUTER = "FR"; //instruction of FR
    String ELEVATOR_UP    = "UP"; //go up
    String ELEVATOR_DOWN  = "DOWN"; //go down
    int MAX_FLOOR = 10;
    int MIN_FLOOR = 1;
    int ER_ARGS = 3;
    int FR_ARGS = 4;
    double openCloseInterval = 1.0;//interval time
    double eachFloorResume = 0.5;
    int STATUS_UP = 1;
    int STATUS_DOWN = 2;
    int STATUS_STILL = 0;
    int INNER_REQUEST = 1;
    int OUTER_REQUEST = 2;
    long MAX_REQUEST_TIME = (long)(Integer.MAX_VALUE)*2+1;
}
