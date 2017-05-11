package core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-05-09.
 */
public class RedGreenLight extends Thread implements GlobalConstant{
    /*
    Overview:红绿灯线程类,按照红绿灯变化间隔模拟南北东西红绿灯的交替
     */
    private int interVal;//灯变化间隔
    private long lastChangeTime;//上一次灯变化的时间
    final int globalLight[] = new int[NODE_NUM];
    final List<Integer> lightSets = new ArrayList<>();
    RedGreenLight(){
        /*@REQUIRES:None
        @MODIFIES:this.interval,this.lastChangeTime
        @EFFECTS:构造一个类对象
         */
        interVal = 50+(int)(Math.random()*51);//50~100(包含端点)
        lastChangeTime = 0;//init
    }
    /*repOk 对于globalLight以及lightSets无需检查是否为null的情况(final)
    check:1.interval>=50 && interval<=100
    2.lastChangeTime>=0
    3.\all globalLight[i] is in {0,1,2},0<=i<NODE_NUM;
    4.\all lightSets.get(i)!=null && lightSets.get(i) is instanceof Integer &&
    0<=lightSets.get(i)<=6399 && (if i!=j ==> lightSets.get(i).equals(lightSets.get(j))==false),
    0<=i,j<lightSets.size;
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result = invariant(this)
         */
        if(interVal<50 || interVal>100)
            return false;
        if(lastChangeTime<0)
            return false;
        for(int i=0;i<NODE_NUM;i++){
            if(globalLight[i]<0 || globalLight[i]>2)
                return false;
        }
        for (Integer lightSet : lightSets) {
            Object x = lightSet;
            if (x == null || !(x instanceof Integer))
                return false;
            if (lightSet < 0 || lightSet >= NODE_NUM)
                return false;
        }
        for(int i=0;i<lightSets.size();i++){
            Integer y = lightSets.get(i);
            for(int j=i+1;j<lightSets.size();j++){
                if(lightSets.get(j).equals(y))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        /*@REQUIRES:None
        @MODIFIES:lastChangeTime
        @EFFECTS:根据红绿灯变化间隔interval,交替变化各路口红绿灯的颜色
                 sleep()出现错误==>exceptional_behavior:(InterruptedException)打印异常处理栈
         */
        lastChangeTime = System.currentTimeMillis();
        loadInitLight();//初始化设置各路口红绿灯
        while(true){
            try {
                sleep(interVal);
                changeLight();
                lastChangeTime = System.currentTimeMillis();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    //初始化函数
    private void loadInitLight(){
        /*@REQUIRES:Main.gui有效
        @MODIFIES:None
        @EFFECTS:调用Main.gui中的方法初始化道路红绿灯(方法内部修改)
         */
        for(int i=0;i<NODE_NUM;i++){
            Main.gui.SetLightStatus(new Point(Main.getRowByCode(i),Main.getColByCode(i)),globalLight[i]);
        }
    }
    private synchronized void changeLight(){
        /*@REQUIRES:Main.gui有效
        @MODIFIES:globalLight
        @EFFECTS:统一改变所有有红绿灯路口红绿灯的颜色
        @THREAD_REQUIRES:\locked(globalLight)
        @THREAD_EFFECTS:\locked(),整个方法同步
         */
        for(Integer a:lightSets){
            int lightStatus = (globalLight[a]==LIGHT_GREEN)? LIGHT_RED:LIGHT_GREEN;
            globalLight[a] = lightStatus;
            Main.gui.SetLightStatus(new Point(Main.getRowByCode(a),Main.getColByCode(a)),lightStatus);
        }
    }
    //判断是否需要等待,不需要返回0
    synchronized long getWaitTime(int whichLight,int nodeCode){
        /*@REQUIRES:(whichLight==1 || whichLight==2)&& 0<=nodeCode<=6399
        @MODIFIES:None
        @EFFECTS:返回需要等待的时间数
        @THREAD_REQUIRES:\locked(globalLight,lastChangeTime)
        @THREAD_EFFECTS:\locked(),整个方法同步
         */
        if(globalLight[nodeCode]==0 || (whichLight==EW && globalLight[nodeCode]==1) || (whichLight==SN && globalLight[nodeCode]==2))
            return 0;//绿灯或者没有红绿灯
        else{
            long time = interVal-(System.currentTimeMillis()-lastChangeTime);
            return (time>0 ? time:0);
        }
    }
    //@当道路关闭导致相关路口不是交叉路口,红绿灯失效,直接从此点挖除
    synchronized void shutLight(int firstCode,int secondCode){
        /*@REQUIRES:0<=firstCode,secondCode<=6399 && Main.gui有效
        @MODIFIES:globalLight
        @EFFECTS:根据修改后道路是否成为非交叉路口选择是否关闭此道路的红绿灯并调用gui方法
        @THREAD_REQUIRES:\locked(globalLight)
        @THREAD_EFFECTS:\locked(),整个方法同步
         */
        if(globalLight[firstCode]!=0 && getConnectNum(firstCode)<=2) {
            globalLight[firstCode] = 0;
            Main.gui.SetLightStatus(new Point(Main.getRowByCode(firstCode),Main.getColByCode(firstCode)),0);
        }
        if(globalLight[secondCode]!=0 && getConnectNum(secondCode)<=2) {
            globalLight[secondCode] = 0;
            Main.gui.SetLightStatus(new Point(Main.getRowByCode(secondCode),Main.getColByCode(secondCode)),0);
        }
    }
    private int getConnectNum(int targetCode){
        /*@REQUIRES:0<=targetCode<=6399 && Main.matrix有效
        @MODIFIES:None
        @EFFECTS:返回与targetCode所代表的结点相连的节点数
         */
        int count = 0;
        int targetRow = Main.getRowByCode(targetCode);
        int targetCol = Main.getColByCode(targetCode);
        //UP
        if(targetRow>0 && Main.matrix[targetCode-COL_NUMBER][targetCode])
            count++;
        //DOWN
        if(targetRow<ROW_NUMBER-1 && Main.matrix[targetCode+COL_NUMBER][targetCode])
            count++;
        //LEFT
        if(targetCol>0 && Main.matrix[targetCode-1][targetCode])
            count++;
        //RIGHT
        if(targetCol<COL_NUMBER-1 && Main.matrix[targetCode+1][targetCode])
            count++;
        return count;
    }
}
