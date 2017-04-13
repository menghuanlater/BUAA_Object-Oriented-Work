package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created on 2017-04-10.
 * this class is to deal with monitor input
 */
class MonitorInputHandle implements GlobalConstant{
    private BufferedReader bufferedReader;
    private List<Monitor> monitorList;
    MonitorInputHandle(List<Monitor> monitorList){
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.monitorList = monitorList;
    }
    void getAllMonitors() throws IOException,NullPointerException {
        int count = 0; //记录不同路径数
        while(true){
            String str = bufferedReader.readLine();
            int monitorType,taskType;
            String monitorPath;
            boolean sameFlag;
            /*if(str.equalsIgnoreCase("end") && count<MIN_PATH){//监控数不足5个
                System.out.println("End failed because the different monitor path not enough.");
                continue;
            }else if(str.equalsIgnoreCase("end") && count>=MIN_PATH){
                break;
            }*/
            if(str.equalsIgnoreCase("end"))
                break;
            String[] arguments = str.split(",");
            if(!(arguments.length==MONITOR_ARGUMENTS && arguments[0].equals("IF") && arguments[3].equals("THEN"))){
                System.out.println("INVALID Input");
                continue;
            }
            if(!new File(arguments[1]).exists()){
                System.out.println("Path not exist.");
                continue;
            }else{
                monitorPath = new File(arguments[1]).getPath();
                sameFlag = isDifferentPath(monitorPath);
            }
            switch (arguments[2]){
                case "renamed":
                    monitorType = RENAME;
                    break;
                case "Modified":
                    monitorType = MODIFY;
                    break;
                case "path-changed":
                    monitorType = PATH_CHANGE;
                    break;
                case "size-changed":
                    monitorType = SIZE_CHANGE;
                    break;
                default:monitorType = 0;break;
            }
            if(monitorType == 0){
                System.out.println("trigger not exist.");
                continue;
            }
            switch (arguments[4]){
                case "record-summary":
                    taskType = SUMMARY;
                    break;
                case "record-detail":
                    taskType = DETAIL;
                    break;
                case "recover":
                    taskType = RECOVER;
                    break;
                default:taskType = 0;break;
            }
            if(taskType == 0){
                System.out.println("task not exist.");
                continue;
            }
            Monitor temp = new Monitor(monitorType,taskType,monitorPath);
            insertOrAbandon(temp);
            if(sameFlag){
                if(count>=MAX_PATH){
                    System.out.println("Differ path has go to the limit.");
                    monitorList.remove(monitorList.size()-1);
                }else
                    count++;
            }
        }
    }
    private boolean isDifferentPath(String pathname){
        boolean flag = true;
        for (Monitor aMonitorList : monitorList) {
            if (aMonitorList.getMonitorPath().equals(pathname)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    private void insertOrAbandon(Monitor temp){
        for(Monitor aMonitorList : monitorList){
            if(aMonitorList.getMonitorPath().equals(temp.getMonitorPath()) && aMonitorList.getMonitorType()==temp.getMonitorType()
                    && aMonitorList.getTaskType()==temp.getTaskType()){
                return;
            }
        }
        monitorList.add(temp);
    }
}
