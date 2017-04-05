package core;

/**
 * Created on 2017-04-04.
 */
class Floors implements ElevatorConstant{
    private final boolean[] floorUpLightStatus = new boolean[MAX_FLOOR];
    private final boolean[] floorDownLightStatus = new boolean[MAX_FLOOR];
    private final int[] execUpElevator = new int[MAX_FLOOR];
    private final int[] execDownElevator = new int[MAX_FLOOR];
    private final double[] lightUpTime = new double[MAX_FLOOR];
    private final double[] lightDownTime = new double[MAX_FLOOR];
    Floors(){
        for(int i=0;i<MAX_FLOOR;i++) {
            floorUpLightStatus[i] = floorDownLightStatus[i] = false;
            execUpElevator[i] = execDownElevator[i] = 0;
            lightDownTime[i] = lightUpTime[i] = 0.000;
        }
    }
    void setFloorUpLightStatusAt(int position,boolean status){
        synchronized (floorUpLightStatus){
            floorUpLightStatus[position] = status;
        }
    }
    void setFloorDownLightStatusAt(int position,boolean status){
        synchronized (floorDownLightStatus) {
            floorDownLightStatus[position] = status;
        }
    }
    boolean getFloorUpLightStatusAt(int position){
        synchronized (floorUpLightStatus) {
            return floorUpLightStatus[position];
        }
    }
    boolean getFloorDownLightStatusAt(int position){
        synchronized (floorDownLightStatus) {
            return floorDownLightStatus[position];
        }
    }
    void setExecUpElevatorAt(int position,int code){
        synchronized (execUpElevator) {
            execUpElevator[position] = code;
        }
    }
    void setExecDownElevatorAt(int position,int code){
        synchronized (execDownElevator) {
            execDownElevator[position] = code;
        }
    }
    int getExecUpElevatorAt(int position){
        synchronized (execUpElevator) {
            return execUpElevator[position];
        }
    }
    int getExecDownElevatorAt(int position){
        synchronized (execDownElevator) {
            return execDownElevator[position];
        }
    }
    void setLightUpTimeAt(int position,double time){
        synchronized (lightUpTime) {
            lightUpTime[position] = time;
        }
    }
    void setLightDownTimeAt(int position,double time){
        synchronized (lightDownTime) {
            lightDownTime[position] = time;
        }
    }
    double getLightUpTimeAt(int position){
        synchronized (lightUpTime){
            return lightUpTime[position];
        }
    }
    double getLightDownTimeAt(int position){
        synchronized (lightDownTime){
            return lightDownTime[position];
        }
    }
}
