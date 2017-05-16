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
        /*@REQUIRES:request!=null src!=null dst!=null requestTime>=0.0
        @MODIFIES:\all member vars
        @EFFECTS:normal_behavior:提取字符串信息,构造出一个完整的乘客请求.
                 非法字符串==>exceptional_behavior:(Exception) this.legacy = false;
        */
        this.request = request;
        this.legacy = true;
        this.requestTime = requestTime;
        this.grabTaxis = new ArrayList<>();
        this.ctrlArea = new ArrayList<>();
        if(src.length()<5 || dst.length()<5)
            this.legacy = false;
        else{
            try {
            if(src.charAt(0)!='('||src.charAt(src.length()-1)!=')' || dst.charAt(0)!='(' || dst.charAt(dst.length()-1)!=')'){
                this.legacy = false;
                return;
            }    
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
    boolean isLegacy(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回legacy的值
        */
        return this.legacy;
    }

    private boolean checkRowCol(int row, int col){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回(row,col)是否合法
        */
        return (row>=0) && (row<ROW_NUMBER) && (col>=0) && (col<COL_NUMBER);
    }
    int getStartCode() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回startCode的值
        */
        return startCode;
    }
    int getEndCode() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回endCode的值
        */
        return endCode;
    }
    String getRequest() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回原生请求字符串
        */
        return request;
    }
    /*double getRequestTime() {
        return requestTime;
    }*/
    synchronized void addGrabTaxi(int taxiCode){
        /*@REQUIRES:0<=taxiCode<=99
        @MODIFIES:grabTaxis
        @EFFECTS:将taxiCode加入grabTaxis
        @THREAD_REQUIRES:\locked(grabTaxis)
        @THREAD_EFFECTS:\locked();方法同步
        */
        grabTaxis.add(taxiCode);
    }
    List<Integer> getGrabTaxis(){//由于返回时不需要修改删除,所以无需拷贝.而且调用此方法不会再有addGrabTaxi出现
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回grabTaxis
        */
        return grabTaxis;
    }
    List<Integer> getCtrlArea(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回ctrlArea
        */
        return ctrlArea;
    }
    private void findCtrlArea(){
        /*@REQUIRES:None
        @MODIFIES:ctrlArea
        @EFFECTS:计算ctrlArea
        */
        int x_s = getCtrlRowStart();
        int x_e = getCtrlRowEnd();
        int y_s = getCtrlColStart();
        int y_e = getCtrlColEnd();
        for(int i=x_s;i<=x_e;i++)
            for(int j=y_s;j<=y_e;j++)
                ctrlArea.add(Main.getCodeByRowCol(i,j));
    }
    private int getCtrlRowStart(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回控制区域最小行起始点
        */
        int start = srcRow - SCAN_VS/2;
        while(start<0)
            start++;
        return start;
    }
    private int getCtrlRowEnd(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回控制区域最大行起始点
        */
        int end = srcRow + SCAN_VS/2;
        while(end>=ROW_NUMBER)
            end--;
        return end;
    }
    private int getCtrlColStart(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回控制区域最小列起始点
        */
        int start = srcCol - SCAN_SP/2;
        while(start<0)
            start++;
        return start;
    }
    private int getCtrlColEnd(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回控制区域最大列起始点
        */
        int end = srcCol + SCAN_SP/2;
        while(end>=COL_NUMBER)
            end--;
        return end;
    }
    int getSrcRow() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回请求发出点行数
        */
        return srcRow;
    }
    int getSrcCol() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回请求发出点列数
        */
        return srcCol;
    }
    int getDstRow() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回目的地行数
        */
        return dstRow;
    }
    int getDstCol() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回目的地列数
        */
        return dstCol;
    }
    String toHashString(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回请求的HashString
        */
        return "send time:"+requestTime+"s:"+request;
    }
}
