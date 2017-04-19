package core;

/**
 * Created on 17-4-16.
 * 存储所有的全局常量
 */
interface GlobalConstant {
    //关于出租车的常量值
    int SUM_CARS = 100;//出租车一共100辆
    int STOP_SERVICE  = 0;//停止服务状态
    int WAIT_SERVICE = 2;//等待服务状态
    int IN_SERVICE = 1;//正在接客服务状态
    int GRAB_SERVICE = 3;//抢单成功并每分派状态
    int STOP_GRAB = 4;//新增的状态:出租车去接客,到达src时停止1s
    int STOP_ACHIEVE = 5;//新增状态:出租车运送乘客达到目的地,停止1s
    int SCAN_VS = 4;//4x4扫描垂直范围
    int SCAN_SP = 4;//4x4扫描水平范围
    //有关地图的常量值
    int NO_CONNECT = 0;//两边都不连接
    int CONNECT_RIGHT = 1;//与右边连接
    int CONNECT_DOWN = 2;//与下方连接
    int ALL_CONNECT = 3;//全部连接
    int ROW_NUMBER = 80;//行数
    int COL_NUMBER = 80;//列数
    int NODE_NUM = ROW_NUMBER*COL_NUMBER;
    //有关时间的常量值
    double gridConsume = 0.2;//一个格子行驶耗时200ms
    double singleWaitMax = 20.0;//处于连续等待最大时间
    double stopInterVal = 1.0;//停止运行的时间间隔
    double reactTime = 3.0;//乘客发出请求的响应时间
    double percific = 0.0001;//浮点数相等的最大误差
    //有关出租车信用
    int CREDIT_INIT = 0;//信用初始化
    int ADD_PER_GRAB = 1;//每抢单一次信用+
    int ADD_PER_SERVICE = 3;//每服务一次+;

    int INF = 65535;
    //关于请求的常量值
    String PASSENGER = "CR";
    String SEARCH = "SR";
    int ARGUMENT_P = 5;//包含多余分割
    int ARGUMENT_S = 2;
    //输入输出文件名
    String MAP_NAME = "map.txt";//定义地图文件名
    String SEARCH_OUT = "search.txt";//输出搜索信息
    String PASSENGER_OUT = "passenger.txt";//输出接客信息
    String REQUEST_SCAN = "requestScan.txt";//输出请求开始时,周边所有的出租车状态信息
}
