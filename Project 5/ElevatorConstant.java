package core;

/**
 * Created on 2017-03-13.
 */
interface ElevatorConstant {
    String ELEVATOR_INNER = "ER"; //instruction of ER
    String ELEVATOR_OUTER = "FR"; //instruction of FR
    String ELEVATOR_UP    = "UP"; //go up
    String ELEVATOR_DOWN  = "DOWN"; //go down
    int MAX_FLOOR = 20;
    int MIN_FLOOR = 1;
    int ARGS = 3;
    double openCloseInterval = 6.0;//interval time
    double eachFloorResume = 3.0;
    int STATUS_UP= 1;
    int STATUS_DOWN = 2;
    int STATUS_OPEN_CLOSE = 3;
    int STATUS_UP_WAIT = 4;
    int STATUS_DOWN_WAIT = 5;
    int STATUS_STILL = 0;
    int INNER_REQUEST = 1;
    int OUTER_REQUEST = 2;
    int ELE_NUM = 3;
    int LINE_REQUEST_MAX = 10;
    //int TIME_RESUME = 0; //set the time resume as 30ms,make up for program resume.
}
