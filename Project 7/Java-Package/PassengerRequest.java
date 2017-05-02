package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 */
class PassengerRequest implements GlobalConstant{
    private int startCode;
    private int srcRow;
    private int srcCol;
    private int dstRow;
    private int dstCol;
    private int endCode;
    private String request;
    private double requestTime;
    private boolean legacy;
    private List<Integer> grabTaxis;
    private List<Integer> ctrlArea;
    PassengerRequest(String request,String src,String dst,double requestTime){
        this.request = request;
        this.legacy = true;
        this.requestTime = requestTime;
        this.grabTaxis = new ArrayList<>();
        this.ctrlArea = new ArrayList<>();
        if(src.length()<5 || dst.length()<5)
            this.legacy = false;
        else{
            try {
            String srcArg[] = src.substring(1,src.length()-1).split(",");
            String dstArg[] = dst.substring(1,dst.length()-1).split(",");
            if(srcArg.length!=2 || dstArg.length!=2)
                this.legacy = false;
            else{
                srcRow = Integer.parseInt(srcArg[0]);
                srcCol = Integer.parseInt(srcArg[1]);
                dstRow = Integer.parseInt(dstArg[0]);
                dstCol = Integer.parseInt(dstArg[1]);
                if(!(srcRow==dstRow && srcCol==dstCol) &&checkRowCol(srcRow,srcCol) && checkRowCol(dstRow,dstCol)){
                    startCode = Main.getCodeByRowCol(srcRow,srcCol);
                    endCode = Main.getCodeByRowCol(dstRow,dstCol);
                    findCtrlArea();
                }else
                    this.legacy = false;
                }
            }catch (Exception e){
                this.legacy = false;
            }
        }
    }
    boolean isLegacy(){return this.legacy;}
    private boolean checkRowCol(int row, int col){
        return (row>=0) && (row<ROW_NUMBER) && (col>=0) && (col<COL_NUMBER);
    }
    int getStartCode() {
        return startCode;
    }
    int getEndCode() {
        return endCode;
    }
    String getRequest() {
        return request;
    }
    /*double getRequestTime() {
        return requestTime;
    }*/
    synchnozied void addGrabTaxi(int taxiCode){
        grabTaxis.add(taxiCode);
    }
    List<Integer> getGrabTaxis(){//由于返回时不需要修改删除,所以无需拷贝.
        return grabTaxis;
    }
    List<Integer> getCtrlArea(){return ctrlArea;}
    private void findCtrlArea(){
        int x_s = getCtrlRowStart();
        int x_e = getCtrlRowEnd();
        int y_s = getCtrlColStart();
        int y_e = getCtrlColEnd();
        for(int i=x_s;i<=x_e;i++)
            for(int j=y_s;j<=y_e;j++)
                ctrlArea.add(Main.getCodeByRowCol(i,j));
    }
    private int getCtrlRowStart(){
        int start = srcRow - SCAN_VS/2;
        while(start<0)
            start++;
        return start;
    }
    private int getCtrlRowEnd(){
        int end = srcRow + SCAN_VS/2;
        while(end>=ROW_NUMBER)
            end--;
        return end;
    }
    private int getCtrlColStart(){
        int start = srcCol - SCAN_SP/2;
        while(start<0)
            start++;
        return start;
    }
    private int getCtrlColEnd(){
        int end = srcCol + SCAN_SP/2;
        while(end>=COL_NUMBER)
            end--;
        return end;
    }
    int getSrcRow() {
        return srcRow;
    }
    int getSrcCol() {
        return srcCol;
    }
    int getDstRow() {
        return dstRow;
    }
    int getDstCol() {
        return dstCol;
    }
    String toHashString(){
        return "send time:"+requestTime+"s:"+request;
    }
}
