package core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-04-16.
 * 该类的目的在于将地图所有点设置为ArrayList,一旦请求信号辐射,insert
 */
class MapRequestSignal implements GlobalConstant{
    private List[] map = new List[NODE_NUM];
    MapRequestSignal(){
        for(int i=0;i<map.length;i++)
            map[i] = new ArrayList<PassengerRequest>();
    }
    synchronized void setMapSignal(List<PassengerRequest> prList){
        for (PassengerRequest aPrList : prList) {
            Main.gui.RequestTaxi(new Point(aPrList.getSrcRow(),aPrList.getSrcCol()),new Point(
                    aPrList.getDstRow(),aPrList.getDstCol()));
            List<Integer> temp = aPrList.getCtrlArea();
            for (Integer aTemp : temp) {
                map[aTemp].add(aPrList);
            }
        }
    }
    synchronized List getMapSignalAt(int position){
        return map[position];
    }
    //request have done,clear it.
    synchronized void clearMapSignal(List<PassengerRequest> prList){
        for (PassengerRequest aPrList : prList) {
            List<Integer> temp = aPrList.getCtrlArea();
            for (Integer aTemp : temp) {
                map[aTemp].remove(0);
            }
        }
    }
}
