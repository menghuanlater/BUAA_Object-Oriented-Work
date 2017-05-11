package core;

/**
 * Created on 2017-05-06.
 * this class is for monitor car flow
 */
public class MonitorCarFlow implements GlobalConstant{
    /*
    Overview:监控道路各条连接边的车流量变化
    */
    private final int carFlow[] = new int[EDGE_NUM];
    /*@repOk.
    check:\all carFlow[i]>=0,0<=i<EDGE_NUM
    */
    public boolean repOk(){
        /*
        @EFFECTS:\result = invariant(this)
        */
        for(int i=0;i<EDGE_NUM;i++){
            if(carFlow[i]<0)
                return false;
        }
        return true;
    }
    //@增加车流量,并且防止同时多辆车进入修改
    synchronized void addCarFlowAt(int x1,int y1,int x2,int y2){
        /*@REQUIRES:0<=x1,x2,y1,y2<=79 && (x1,y1) not equals (x2,y2)
        @MODIFIES:carFlow
        @EFFECTS:增加(x1,y1)-(x2,y2)对应边的流量
        @THREAD_REQUIRES:\locked(carFlow)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        int edge = getEdgeByPoint(x1, y1, x2, y2);
        carFlow[edge]++;
    }
    //@减少车流量,并且防止同时多辆车进入修改
    synchronized void subCarFlowAt(int x1,int y1,int x2,int y2){
        /*@REQUIRES:0<=x1,x2,y1,y2<=79 && (x1,y1) not equals (x2,y2) && carFlow At(x1,y1)-(x2,y2) must >0.
        @MODIFIES:carFlow
        @EFFECTS:减少(x1,y1)-(x2,y2)对应边的流量
        @THREAD_REQUIRES:\locked(carFlow)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        int edge = getEdgeByPoint(x1, y1, x2, y2);
        if(carFlow[edge]>0)
            carFlow[edge]--;
    }
    //查询车流量,查询与改变车流量互斥
    synchronized int getCarFlowAt(int x1,int y1,int x2,int y2){
        /*@REQUIRES:0<=x1,x2,y1,y2<=79 && (x1,y1) not equals (x2,y2)
        @MODIFIES:None
        @EFFECTS:返回(x1,y1)-(x2,y2)对应边的流量值
        @THREAD_REQUIRES:\locked(carFlow)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        int edge = getEdgeByPoint(x1, y1, x2, y2);
        return carFlow[edge];
    }
    //某个点流量清0--->对应道路修改
    synchronized void clearFlowAt(int x1,int y1,int x2,int y2){
        /*@REQUIRES:0<=x1,x2,y1,y2<=79 && (x1,y1) not equals (x2,y2)
        @MODIFIES:carFlow
        @EFFECTS:将(x1,y1)-(x2,y2)对应边的流量清0
        @THREAD_REQUIRES:\locked(carFlow)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        int edge = getEdgeByPoint(x1, y1, x2, y2);
        carFlow[edge] = 0;
    }
    //计算两个坐标对应的边编号
    private int getEdgeByPoint(int row1,int col1,int row2,int col2){//根据节点坐标找到边的编号
        /*@REQUIRES:0<=x1,x2,y1,y2<=79 && (x1,y1) not equals (x2,y2)
        @MODIFIES:None
        @EFFECTS:返回(x1,y1)-(x2,y2)对应边的编号
        */
        if(row1==row2){
            return row1*(COL_NUMBER-1) + ((col1<col2)?col1:col2);
        }else{
            return ROW_NUMBER*(COL_NUMBER-1)+COL_NUMBER*(row1<row2?row1:row2)+col1;
        }
    }
}
