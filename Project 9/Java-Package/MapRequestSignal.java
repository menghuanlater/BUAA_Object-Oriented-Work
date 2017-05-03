package core;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-04-16.
 * 该类的目的在于将地图所有点设置为ArrayList,一旦请求信号辐射,insert,并且
 * 扫描所有出租车,将所有在请求开始辐射区域内的所有出租车找到,输出他们的信息.
 */
class MapRequestSignal implements GlobalConstant{
    private List[] map = new List[NODE_NUM];
    MapRequestSignal(){
        /*@REQUIRES:None
        @MODIFIES:this.map
        @EFFECTS:构造,实例化数组
        */
        for(int i=0;i<map.length;i++)
            map[i] = new ArrayList<PassengerRequest>();
    }
    synchronized void setMapSignal(List<PassengerRequest> prList){
        /*@REQUIRES:prList.size()>0
        @MODIFIES:this.map,Main.safeFileRequest,Main.gui
        @EFFECTS:将请求辐射区域打上标记,并搜寻请求发出时周围所有的出租车,打印状态信息
        @THREAD_REQUIRES:\locked(map)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        //首先将所有出租车拷贝下来.
        HashMap<Integer,List<PassengerRequest>> hashMap = new HashMap<>(50);
        Taxi[] taxiSets = new Taxi[SUM_CARS];
        for(int i=0;i<SUM_CARS;i++)
            taxiSets[i] = Main.taxiSets[i].clone();
        //执行正常的辐射,其中附带标注了哪些点有请求.--->HashMap实现
        for (PassengerRequest aPrList : prList) {
            Main.gui.RequestTaxi(new Point(aPrList.getSrcRow(),aPrList.getSrcCol()),new Point(
                    aPrList.getDstRow(),aPrList.getDstCol()));
            List<Integer> temp = aPrList.getCtrlArea();
            for (Integer aTemp : temp) {
                map[aTemp].add(aPrList);
                if(hashMap.get(aTemp)==null){
                    List<PassengerRequest> list = new ArrayList<>();
                    list.add(aPrList);
                    hashMap.put(aTemp,list);
                }else{
                    hashMap.get(aTemp).add(aPrList);
                }
            }
        }
        //此信息不输出到终端
        List<PassengerRequest> list;
        for(int i=0;i<SUM_CARS;i++){
            int position = taxiSets[i].getCurrentPosition();
            if((list=hashMap.get(position))!=null){
                for(PassengerRequest aList : list)
                    Main.safeFileRequest.writeToFile(aList.toHashString(),"出租车编号:"+taxiSets[i].getTaxiCode()+
                    "\t"+taxiSets[i].toString());
            }
        }
        Main.safeFileRequest.outPutToFile();
    }
    synchronized List<PassengerRequest> getMapSignalAt(int position){
        /*@REQUIRES:0<=position<=6399
        @MODIFIES:this.map
        @EFFECTS:返回所有在编号为position节点上标记的请求
        @THREAD_REQUIRES:\locked(map)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        List<PassengerRequest> temp = new ArrayList<>();
        temp.addAll(map[position]);
        return temp;
    }
    //request have done,clear it.
    synchronized void clearMapSignal(List<PassengerRequest> prList){
        /*@REQUIRES:prList.size()>0
        @MODIFIES:this.map
        @EFFECTS:请求响应时间结束,清除标记信号
        @THREAD_REQUIRES:\locked(map)
        @THREAD_EFFECTS:\locked(),整个方法同步
        */
        for (PassengerRequest aPrList : prList) {
            List<Integer> temp = aPrList.getCtrlArea();
            for (Integer aTemp : temp) {
                map[aTemp].remove(0);
            }
        }
    }
}
