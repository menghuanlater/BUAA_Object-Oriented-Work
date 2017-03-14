package core;

/**
 * Created on 2017-03-13.
 * it seems we may not use this class in project 2.
 */
public class FloorNode {
    private boolean upFlag;
    private boolean downFlag;
    public FloorNode(){
        upFlag = false;
        downFlag = false;
    }
    public void setUpFlag(boolean value){
        upFlag = value;
    }
    public void setDownFlag(boolean value){
        downFlag = value;
    }
    public boolean isUpFlag(){
        return upFlag;
    }
    public boolean isDownFlag(){
        return downFlag;
    }
}
