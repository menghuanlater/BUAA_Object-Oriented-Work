package core;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created on 17-4-16.
 * 主控函数
 * 兼容了迪杰斯特拉算法求解最短路径(具体信息)
 * 获得两个节点间的最短距离使用单源节点广度优先搜索算法.
 * 出租车与出租车线程相分离,更好的做到数据安全\线程安全
 */
public class Main implements GlobalConstant{
    /*
    Overview:程序入口函数,实现各线程各全局变量的初始化工作
     */
    //整个出租车系统需要的全局变量
    static final boolean[][] matrix = new boolean[NODE_NUM][NODE_NUM];//邻接矩阵,采用boolean
    static final boolean[][] matrixInit = new boolean[NODE_NUM][NODE_NUM];//初始备份,用于道路修改判断是否合法
    static final int [][] matrixGui = new int[ROW_NUMBER][COL_NUMBER];
    static final RedGreenLight redGreenLight = new RedGreenLight();//红绿灯
    static final MonitorCarFlow carFlow = new MonitorCarFlow();//全局车流量统计
    static final TaxiGUI gui=new TaxiGUI();//GUI可视化对象
    static final DecimalFormat decimalFormat = new DecimalFormat("0.0");//数值四舍五入
    static long startTime = System.currentTimeMillis();
    static final Taxi[] taxiSets = new Taxi[SUM_CARS];//100个出租车
    static final CommandTaxi[] commandTaxis = new CommandTaxi[SUM_CARS];//100个出租车调度线程(执行与信息存储分离)
    static final MapRequestSignal mapSignal = new MapRequestSignal();//地图请求信号
    static final SafeFile safeFilePassenger = new SafeFile(PASSENGER_OUT);//出租车服务处理相关信息输出到文件的安全类,程序最后输出.
    static final SafeFile safeFileRequest = new SafeFile(REQUEST_SCAN);//请求发出时周边所有的出租车状态信息.
    //用于迪杰斯特拉算法所需的变量(避免每次都重新分配)
    private static boolean find[] = new boolean[NODE_NUM];
    private static int pathArc[] = new int[NODE_NUM];
    private static int shortPathTable[] = new int[NODE_NUM];
    private static int costTable[] = new int[NODE_NUM];//花费矩阵
    //专门为输出做的锁
    private final static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        //gui的修改均在gui内部,外部不修改
        /*@REQUIRES:None
        @MODIFIES:System.out,taxiSets,commandTaxis
        @EFFECTS:normal_behavior:初始化各相关的变量,通过各类的方法调用:读取加载地图,启动gui、出租车、请求输入、请求抓取处理等线程
                 if(input end)--> goto end the program.
                 函数抛出的文件输入输出流打开异常|Thread.sleep()异常==>exceptional_behavior:
                 (IOException|InterruptedException)打印异常处理栈
        */
        try {
            System.out.println("欢迎使用出租车调度管理系统,请输入请求.");
            MapLightHandler mapLightHandler = new MapLightHandler();
            mapLightHandler.readTheMapFile();
            //List<Integer> s = getShortestPath(223,1599);
            //System.out.println(s.size());
            //System.exit(0);
            gui.LoadMap(matrixGui, ROW_NUMBER);
            //地图录入结束,下面进行线程启动
            //first,申明队列
            RequestQueue<PassengerRequest> prQueue = new RequestQueue<>();//乘客请求队列
            RequestQueue<SearchRequest> srQueue = new RequestQueue<>();//搜索申请队列
            RequestQueue<RoadRequest> rrQueue = new RequestQueue<>();//道路修改队列
            //next 创建启动相关线程
            RequestInputHandler requestInput = new RequestInputHandler(srQueue, prQueue, rrQueue);
            requestInput.start();
            //启动搜索线程
            DealSearch dealSearch = new DealSearch(srQueue);
            dealSearch.start();
            //启动道路设置线程
            DealRoad dealRoad = new DealRoad(rrQueue);
            dealRoad.start();
            //启动乘客请求处理线程
            DealPassenger dealPassenger = new DealPassenger(prQueue);
            dealPassenger.start();
            //启动100个taxi
            for (int i = 0; i < SUM_CARS; i++) {
                taxiSets[i] = new Taxi(i);
                commandTaxis[i] = new CommandTaxi(taxiSets[i], i);
                commandTaxis[i].start();
            }
            //启动红绿灯线程
            redGreenLight.start();
            //循环判断输入何时结束
            while (requestInput.getState() != Thread.State.TERMINATED) {//输入结束,下面去判断所有出租车的状态
                Thread.sleep(5000);//不需要一直去扫描,尽量不占用太多的CPU资源
            }
            //首先终止非出租车线程
            dealSearch.stop();
            dealRoad.stop();
            dealPassenger.stop();
            //当判断请求输入截止的时候,每5s遍历一次所有的taxi状态,当没有任何一个处于接客服务状态,全部停止,加一个队列
            //每次判断只判断队列里的.
            List<Taxi> noAccomplishTaxis = new ArrayList<>();
            for (int i = 0; i < SUM_CARS; i++) {
                noAccomplishTaxis.add(taxiSets[i].clone());
            }
            //每5s扫描一次
            while (noAccomplishTaxis.size() > 0) {
                for (int i = 0; i < noAccomplishTaxis.size(); i++) {
                    int status = noAccomplishTaxis.get(i).getCurrentStatus();
                    if (status != IN_SERVICE && status != GRAB_SERVICE) {
                        noAccomplishTaxis.remove(i);
                        i--;
                    }
                }
                Thread.sleep(5000);
                //重新扫描
                for (int i = 0; i < noAccomplishTaxis.size(); i++) {
                    int code = noAccomplishTaxis.get(i).getTaxiCode();
                    noAccomplishTaxis.remove(i);
                    noAccomplishTaxis.add(i, taxiSets[code].clone());
                }
            }
            //终止红绿灯
            redGreenLight.stop();
            //over all thread
            Thread.sleep(10);//再休眠10ms,终止所有的出租车线程.
            //关闭输出流
            safeFilePassenger.closeFileOutStream();
            safeFileRequest.closeFileOutStream();
            for (int i = 0; i < SUM_CARS; i++)
                commandTaxis[i].stop();
            System.out.println("All thread have exist successfully!");
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    //节点编号与节点所在的行列转换
    static double getCurrentTime(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回当前程序时间(非系统时间)
        */
        return Double.valueOf(Main.decimalFormat.format((System.currentTimeMillis()-startTime)/1000.0));
    }
    static int getRowByCode(int code){
        /*@REQUIRES:0<=code<=6399;
        @MODIFIES:None
        @EFFECTS:返回code所代表的节点的行数
        */
        return code/COL_NUMBER;
    }
    static int getColByCode(int code){
        /*@REQUIRES:0<=code<=6399;
        @MODIFIES:None
        @EFFECTS:返回code所代表的节点的列数
        */
        return code - COL_NUMBER*getRowByCode(code);
    }
    static int getCodeByRowCol(int row,int col){
        /*@REQUIRES:0<=row<=79 && 0<=col<=79
        @MODIFIES:None
        @EFFECTS:返回(row,col)所代表的节点编号
        */
        return row*ROW_NUMBER+col;
    }
    //非法地图输出并over
    static void illegalMapLight(){
        /*@REQUIRES:Node
        @MODIFIES:System.out
        @EFFECTS:输出地图非法提示,终止程序
        */
        System.out.println("抱歉,您提供的地图或者红绿灯文件非法.\n");
        System.exit(0);//地图非法,程序终止
    }
    //最短路径Dijkstra算法优化模式.getConnectList是辅助优化,避免找到一个结点的最短路径去循环遍历所有
    //同时也辅助广度优先搜索,并且加入判断点是最短路径是否已经找到
    private static List<Integer> getConnectList(int targetCode){
        /*@REQUIRES:0<=targetCode<=6399
        @MODIFIES:None
        @EFFECTS:返回与targetCode所代表的点相连的点集(当前均不在最短路径中)
        */
        List<Integer> temp = new ArrayList<>();
        int targetRow = getRowByCode(targetCode);
        int targetCol = getColByCode(targetCode);
        //UP
        if(targetRow>0 && matrix[targetCode-COL_NUMBER][targetCode] && !find[targetCode-COL_NUMBER])
            temp.add(targetCode-COL_NUMBER);
        //DOWN
        if(targetRow<ROW_NUMBER-1 && matrix[targetCode+COL_NUMBER][targetCode] && !find[targetCode+COL_NUMBER])
            temp.add(targetCode+COL_NUMBER);
        //LEFT
        if(targetCol>0 && matrix[targetCode-1][targetCode] && !find[targetCode-1])
            temp.add(targetCode-1);
        //RIGHT
        if(targetCol<COL_NUMBER-1 && matrix[targetCode+1][targetCode] && !find[targetCode+1])
            temp.add(targetCode+1);
        return temp;
    }
    //由于存在全局变量,同时多个线程请求计算会出问题
    static synchronized List<Integer> getShortestPath(int startCode, int endCode){
        /*@REQUIRES:0<=startCode,endCode<=6399
        @MODIFIES:find[],pathArc[],shortPathTable[],costTable[]
        @EFFECTS:寻找从startCode到endCode的最短路径并且车流量最小,返回给调用者
        @THREAD_REQUIRES:\locked(find[],pathArc[],shortPathTable[],costTable[],matrix[][])
        @THREAD_EFFECTS:\locked();整个方法同步
        */
        long algorithmStart = System.currentTimeMillis();//计算运行时间,用于后面出租车时间的修正补偿
        List<Integer> temp = new ArrayList<>();
        //首先针对全局辅助静态变量初始化.
        for(int v=0;v<NODE_NUM;v++){
            find[v] = false;
            pathArc[v] = startCode;
            shortPathTable[v] = (matrix[startCode][v]) ? 1 : INF;
            costTable[v] = (matrix[startCode][v])? carFlow.getCarFlowAt(getRowByCode(startCode),getColByCode(startCode),
                    getRowByCode(v),getColByCode(v)) : INF;
        }
        pathArc[startCode] = startCode;
        find[startCode] = true;
        costTable[startCode] = 0;
        int min,k = 0;
        for(int v=0;v<NODE_NUM;v++){
            if(v==startCode)
                continue;
            min = INF;
            for(int w=0;w<NODE_NUM;w++){
                if(!find[w] && shortPathTable[w]<min){
                    k = w;
                    min = shortPathTable[w];
                }
            }
            if(k==endCode)
                break;
            find[k] = true;
            List<Integer> relateList = getConnectList(k);
            for (Integer aRelateList : relateList) {
                int cost = costTable[k] + carFlow.getCarFlowAt(getRowByCode(k),
                        getColByCode(k),getRowByCode(aRelateList),getColByCode(aRelateList));
                if ((min + 1) < shortPathTable[aRelateList] ||
                        ((min + 1) == shortPathTable[aRelateList] && costTable[aRelateList] > cost)) {
                    shortPathTable[aRelateList] = min + 1;
                    pathArc[aRelateList] = k;
                    costTable[aRelateList] = cost;
                }
            }
        }
        temp.add((int) (System.currentTimeMillis()-algorithmStart));
        temp.add(endCode);
        k = pathArc[endCode];
        while(k!=startCode){
            temp.add(k);
            k = pathArc[k];
        }
        temp.add(startCode);
        return temp;
    }
    //最短路径广度优先算法,算法的目的在于选择距离请求出发点最近的出租车,找到一个直接返回
    static synchronized int getNearestTaxi(int srcPos, HashMap<Integer,Integer> availableTaxis){
        /*@REQUIRES:0<=srcPos<=6399 && availableTaxis.Keys().size>=2// <=1没有任何需要判断的必要
        @MODIFIES:find[]
        @EFFECTS:以srcPos为起点,做广度优先搜索,找到距离srcPos最近的出租车(在availableTaxis中寻找),返回车编号
        @THREAD_REQUIRES:\locked(find[],matrix[][])
        @THREAD_EFFECTS:\locked();整个方法同步
        */
        for(int i=0;i<NODE_NUM;i++)
            find[i] = false;
        List<Integer> queue = new ArrayList<>();
        queue.add(srcPos);
        while(queue.size()>0){
            if(availableTaxis.get(queue.get(0))!=null){
                return availableTaxis.get(queue.get(0));
            }
            find[queue.get(0)] = true;
            List<Integer> con = getConnectList(queue.get(0));
            for(Integer a:con)
                queue.add(a);
            queue.remove(0);//移去队列的头部
        }
        return 0;
    }
    //修改车流量数组以及路径邻接矩阵(关路与开路)
    static synchronized void modifyRoad(List<RoadRequest> list){
        /*@REQUIRES:list.size()>0;
        @MODIFIES:matrix[][]
        @EFFECTS:根据道路修改指令,关闭或打开某些路,通知相关出租车重新规划路径,调用相关类方法修改流量、决断是否关闭红绿灯
        @THREAD_REQUIRES:\locked(matrix[][])
        @THREAD_EFFECTS:\locked();方法同步
        */
        RoadRequest r; boolean status;
        int firstRow,nextRow,firstCol,nextCol;
        for (RoadRequest aList : list) {
            r = aList;
            firstRow = r.getFirstRow();
            firstCol = r.getFirstCol();
            nextRow = r.getNextRow();
            nextCol = r.getNextCol();
            status = (r.getType() == openRoad);
            int firstCode = getCodeByRowCol(firstRow,firstCol);
            int nextCode = getCodeByRowCol(nextRow,nextCol);
            if(!matrixInit[firstCode][nextCode]){
                outPutInfoToTerminal("对边("+firstRow+","+firstCol+")--("+nextRow+","+nextCol+")的修改无效");
                continue;
            }
            matrix[firstCode][nextCode] = matrix[nextCode][firstCode] = status;
            carFlow.clearFlowAt(firstRow,firstCol,nextRow,nextCol);//新打开或者关闭的边流量-->0
            redGreenLight.shutLight(firstCode,nextCode);//不需要关闭则不关
            //设置gui状态
            int _status = (status)? 1:0;
            gui.SetRoadStatus(new Point(getRowByCode(firstCode),getColByCode(firstCode)),new Point(getRowByCode(nextCode),
            getColByCode(nextCode)),_status);

        }
        for(int i=0;i<SUM_CARS;i++)
            commandTaxis[i].setCheckRoadChange();
    }
    //输出信息到终端,但是为了避免多个线程输出信息的紊乱杂糅,一次只能输出一个
    static void outPutInfoToTerminal(String info){
        /*@REQUIRES:info!=null
        @MODIFIES:System.out
        @EFFECTS:调用Lock的方法加锁,输出信息到终端,调用Lock的方法释放锁
        @THREAD_REQUIRES:None
        @THREAD_EFFECTS:None.   (锁机制由自定义锁完成互斥)
        */
        lock.lock();
        System.out.println(info);
        lock.unlock();
    }
    /*@repOk.
    //检查需要在Main函数启动后检查,否则检查没有任何意义.
    check:1.\all matrix[i][j] == matrix[j][i] && matrixInit[i][j] == matrixInit[j][i] &&
        (if matrixInit[i][j]==false ==> matrix[i][j]==false) ,0<=i,j<=6399
    2.\all matrixGui[i][j] is in {0,1,2,3}, 0<=i,j<=6399
    3.startTime>0 && \all taxiSets[i]!=null && taxiSets[i].taxiCode = i && commandTaxis[i]!=null
        && commandTaxis[i]!=null
    4.其他成员变量无需检查(final的存在等原因)
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result = invariant(this)
         */
        //check 1
        for(int i=0;i<NODE_NUM;i++){
            for(int j=0;j<NODE_NUM;j++){
                if(matrix[i][j]!=matrix[j][i])
                    return false;
                if(matrixInit[i][j]!=matrixInit[j][i])
                    return false;
                if(!matrixInit[i][j] && matrix[i][j])
                    return false;
            }
        }
        //check 2
        for(int i=0;i<ROW_NUMBER;i++){
            for(int j=0;j<COL_NUMBER;j++){
                if(matrixGui[i][j]<0 || matrixGui[i][j]>3)
                    return false;
            }
        }
        //check 3
        if(startTime<=0) return false;
        for(int i=0;i<SUM_CARS;i++)
            if(taxiSets[i]==null || commandTaxis[i]==null || taxiSets[i].getTaxiCode()!=i)
                return false;
        return true;
    }
}
