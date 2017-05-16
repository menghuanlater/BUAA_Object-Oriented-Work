package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17-4-16.
 */
public class PassengerRequest implements GlobalConstant{
    /*
    Overview:乘客请求,保存请求的相关信息
     */
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
    /*@repOk. 规定:如果请求通过构造函数发现自身是不合法请求,那么该请求是无效的
    check:legacy==true && 0<=srcRow,srcCol,dstRow,dstCol<=79 && (stcRow,srcCol) not equals (dstRow,dstCol)
    && 0<=startCode,endCode<=6399 && startCode!=endCode && Main.getCodeByRowCol(srcRow,srcCol)==startCode
    && Main.getCodeByRowCol(dstRow,dstCol)==endCode && request!=null && request is instance of String && requestTime>=0.0
    && grabTaxis!=null && ctrlArea!=null && (\all grabTaxis[i]!=null && grabTaxis[i] is instanceof Integer && 0<=grabTaxis[i]<=99
    && i!=j==>grabTaxis[i]!=grabTaxis[j] ,0<=i,j<grabTaxis.size) && (\all ctrlArea[i]!=null && ctrlArea[i] is instanceof Integer &&
    0<=ctrlArea[i]<=6399 && i!=j==>ctrlArea[i]!=ctrlArea[j],0<=i,j<=ctrlArea.size)
    */
    public synchronized boolean repOk(){
        /*@EFFECTS:\result == invariant(this)
        @THREAD_REQUIRES:\locked(grabTaxis) //存在修改grabTaxis的函数被其他线程调用
         */
        if(!legacy) return false; //不合法请求不需要继续检测其他成员
        if(srcRow<0 || srcRow>=ROW_NUMBER || srcCol<0 || srcCol>=COL_NUMBER || dstRow<0 || dstRow>=ROW_NUMBER ||
                dstCol<0 || dstCol>=COL_NUMBER)
            return false;
        if(srcRow==dstRow && srcCol==dstCol) return false;
        if(startCode<0 || startCode>=NODE_NUM || endCode<0 || endCode>=NODE_NUM || startCode==endCode)
            return false;
        if(Main.getCodeByRowCol(srcRow,srcCol)!=startCode || Main.getCodeByRowCol(dstRow,dstCol)!=endCode)
            return false;
        Object x = request;
        if(x==null || !(x instanceof String)) return false;
        if(requestTime<0.0) return false;
        if(grabTaxis==null || ctrlArea==null) return false;
        for (Integer grabTaxi : grabTaxis) {
            x = grabTaxi;
            if (x == null || !(x instanceof Integer)) return false;
        }
        for(int i=0;i<grabTaxis.size();i++){
            Integer y = grabTaxis.get(i);
            if(y<0 || y>=SUM_CARS)
                return false;
            for(int j=i+1;j<grabTaxis.size();j++)
                if(y.equals(grabTaxis.get(j)))
                    return false;
        }
        for (Integer aCtrlArea : ctrlArea) {
            x = aCtrlArea;
            if (x == null || !(x instanceof Integer)) return false;
        }
        for(int i=0;i<ctrlArea.size();i++){
            Integer y = ctrlArea.get(i);
            if(y<0 || y>=SUM_CARS)
                return false;
            for(int j=i+1;j<ctrlArea.size();j++){
                if(y.equals(ctrlArea.get(j)))
                    return false;
            }
        }
        return true;
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
    @Override
    public String toString(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回请求的String形式
        */
        return request+"@send time:"+requestTime;
    }
}
