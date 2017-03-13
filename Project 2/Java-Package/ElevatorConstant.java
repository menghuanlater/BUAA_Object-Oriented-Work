package core;

/**
 * Created on 2017-03-13.
 */
public interface ElevatorConstant {
    final String ELEVATOR_INNER = "ER"; //instruction of ER
    final String ELEVATOR_OUTER = "FR"; //instruction of FR
    final String ELEVATOR_UP    = "UP"; //go up
    final String ELEVATOR_DOWN  = "DOWN"; //go down
    final int MAX_FLOOR = 10;
    final int MIN_FLOOR = 1;
    final int ER_ARGS = 3;
    final int FR_ARGS = 4;
    final double openCloseInterval = 1.0;//interval time
}
