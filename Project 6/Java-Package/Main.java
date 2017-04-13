package core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-04-08.
 */
public class Main implements GlobalConstant{
    //输出标准格式时间
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //线程安全类
    static SafeFile safeFile = new SafeFile();
    //summary记录对象
    static Summary summary = new Summary();
    //detail记录对象
    static Detail detail = new Detail();
    public static void main(String[] args) {
        System.out.println("----------Welcome to use IFTTT----------");
        System.out.println("First,please enter all the IFTTT separated by new line and end by input end.");
        //input IFTTT
        List<Monitor> monitorsList = new ArrayList<>();//所有监控.
        MonitorInputHandle monitorInput = new MonitorInputHandle(monitorsList);
        try {
            monitorInput.getAllMonitors();//得到所有的监控输入
            //启动所有的IFTTT线程.
            for (Monitor aMonitorsList : monitorsList) {
                System.out.println(aMonitorsList.getMonitorPath() + "\t" + aMonitorsList.getMonitorType() + "\t" +
                        aMonitorsList.getTaskType());
                aMonitorsList.start();//启动线程
            }
            //主线程进入等待用户输入指令处理阶段,另一个线程.
            Command command = new Command();
            command.start();
            long startTime = System.currentTimeMillis();
            while (command.getState() != Thread.State.TERMINATED) {
                if (System.currentTimeMillis() - startTime >= UPDATE_INTERVAL) {
                    startTime = System.currentTimeMillis();
                    summary.writeToOutFile();
                    detail.writeToOutFile();
                }
            }
            //command over
            System.out.println("command thread end.");
            //进入等待,3s后终止所有的线程.
            try {
                Thread.sleep(3000);//等所有其他线程该干的事情干完,程序结束
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Monitor aMonitorsList : monitorsList) {
                if (aMonitorsList.getState() != Thread.State.TERMINATED)
                    aMonitorsList.stop();//强制关闭
            }
        }catch (IOException | NullPointerException e){
            System.out.println("Illegal input.");
        }
    }
}
