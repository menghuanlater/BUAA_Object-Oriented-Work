package core;

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
 * 获得两个节点间的最短距离使用gui包内置的单源节点广度优先搜索算法.
 * 出租车与出租车线程相分离,更好的做到数据安全\线程安全
 */
public class Main implements GlobalConstant{
    //整个出租车系统需要的全局变量
    static boolean[][] matrix = new boolean[NODE_NUM][NODE_NUM];//邻接矩阵,采用boolean
    static int [][] matrixGui = new int[ROW_NUMBER][COL_NUMBER];
    static TaxiGUI gui=new TaxiGUI();//GUI可视化对象
    static DecimalFormat decimalFormat = new DecimalFormat("0.0");//数值四舍五入
    static long startTime = System.currentTimeMillis();
    static Taxi[] taxiSets = new Taxi[SUM_CARS];//100个出租车
    static CommandTaxi[] commandTaxis = new CommandTaxi[SUM_CARS];//100个出租车调度线程(执行与信息存储分离)
    static MapRequestSignal mapSignal = new MapRequestSignal();//地图请求信号
    static SafeFile safeFilePassenger = new SafeFile(PASSENGER_OUT);//出租车服务处理相关信息输出到文件的安全类,程序最后输出.
    static SafeFile safeFileRequest = new SafeFile(REQUEST_SCAN);//请求发出时周边所有的出租车状态信息.
    //用于迪杰斯特拉算法所需的变量(避免每次都重新分配)
    private static boolean find[] = new boolean[NODE_NUM];
    private static int pathArc[] = new int[NODE_NUM];
    private static int shortPathTable[] = new int[NODE_NUM];
    //专门为输出做的锁
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("欢迎使用出租车调度管理系统,请输入请求.");
        MapInputHandler mapInputHandler = new MapInputHandler();
        mapInputHandler.readTheMapFile();
        //List<Integer> s = getShortestPath(223,1599);
        //System.out.println(s.size());
        //System.exit(0);
        gui.LoadMap(matrixGui,ROW_NUMBER);
        //地图录入结束,下面进行线程启动
        //first,申明队列
        PassengerRequestQueue prQueue = new PassengerRequestQueue();//乘客请求队列
        SearchRequestQueue srQueue = new SearchRequestQueue();//搜索申请队列
        //next 创建启动相关线程
        RequestInputHandler requestInput = new RequestInputHandler(srQueue,prQueue);
        requestInput.start();
        //启动搜索线程
        DealSearch dealSearch = new DealSearch(srQueue);
        dealSearch.start();
        //启动乘客请求处理线程
        DealPassenger dealPassenger = new DealPassenger(prQueue);
        dealPassenger.start();
        //启动100个taxi
        for(int i=0;i<SUM_CARS;i++){
            taxiSets[i] = new Taxi(i);
            commandTaxis[i] = new CommandTaxi(taxiSets[i],i);
            commandTaxis[i].start();
        }
        //循环判断输入何时结束
        while(requestInput.getState()!= Thread.State.TERMINATED){//输入结束,下面去判断所有出租车的状态
            Thread.sleep(5000);//不需要一直去扫描,尽量不占用太多的CPU资源
        }
        //首先终止非出租车线程
        dealSearch.stop();
        dealPassenger.stop();
        //当判断请求输入截止的时候,每5s遍历一次所有的taxi状态,当没有任何一个处于接客服务状态,全部停止,加一个队列
        //每次判断只判断队列里的.
        List<Taxi> noAccomplishTaxis = new ArrayList<>();
        for(int i=0;i<SUM_CARS;i++){
            noAccomplishTaxis.add(taxiSets[i].clone());
        }
        //每5s扫描一次
        while(noAccomplishTaxis.size()>0){
            for(int i=0;i<noAccomplishTaxis.size();i++){
                int status = noAccomplishTaxis.get(i).getCurrentStatus();
                if(status!=IN_SERVICE && status!=GRAB_SERVICE){
                    noAccomplishTaxis.remove(i);
                    i--;
                }
            }
            Thread.sleep(5000);
            //重新扫描
            for(int i=0;i<noAccomplishTaxis.size();i++){
                int code = noAccomplishTaxis.get(i).getTaxiCode();
                noAccomplishTaxis.remove(i);
                noAccomplishTaxis.add(i,taxiSets[code].clone());
            }
        }
        //over all thread
        Thread.sleep(10);//再休眠10ms,终止所有的出租车线程.
        //关闭输出流
        safeFilePassenger.closeFileOutStream();
        safeFileRequest.closeFileOutStream();
        for(int i=0;i<SUM_CARS;i++)
            commandTaxis[i].stop();
        System.out.println("All thread have exist successfully!");
    }
    //节点编号与节点所在的行列转换
    static double getCurrentTime(){
        return Double.valueOf(Main.decimalFormat.format((System.currentTimeMillis()-startTime)/1000.0));
    }
    static int getRowByCode(int code){
        return code/COL_NUMBER;
    }
    static int getColByCode(int code){
        return code - COL_NUMBER*getRowByCode(code);
    }
    static int getCodeByRowCol(int row,int col){
        return row*ROW_NUMBER+col;
    }
    //非法地图输出并over
    static void illegalMap(){
        System.out.println("抱歉,您提供的地图非法.\n");
        System.exit(0);//地图非法,程序终止
    }
    //最短路径Dijkstra算法优化模式.getConnectList是辅助优化,避免找到一个结点的最短路径去循环遍历所有
    //同时也辅助广度优先搜索,并且加入判断点是最短路径是否已经找到
    private static List<Integer> getConnectList(int targetCode){
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
    static synchronized List<Integer> getShortestPath(int startCode,int endCode){
        long algorithmStart = System.currentTimeMillis();//计算运行时间,用于后面出租车时间的修正补偿
        List<Integer> temp = new ArrayList<>();
        //首先针对全局辅助静态变量初始化.
        for(int v=0;v<NODE_NUM;v++){
            find[v] = false;
            pathArc[v] = startCode;
            shortPathTable[v] = (matrix[startCode][v]) ? 1 : INF;
        }
        pathArc[startCode] = startCode;
        find[startCode] = true;
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
                if ((min + 1) < shortPathTable[aRelateList]) {
                    shortPathTable[aRelateList] = min + 1;
                    pathArc[aRelateList] = k;
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
    //输出信息到终端,但是为了避免多个线程输出信息的紊乱杂糅,一次只能输出一个
    static void outPutInfoToTerminal(String info){
        lock.lock();
        System.out.println(info);
        lock.unlock();
    }
}
