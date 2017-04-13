package core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-04-10.
 */
class Monitor extends Thread implements GlobalConstant{
    private int monitorType;//触发器类型
    private int taskType;//任务类型
    private String monitorPath;//监控路径
    private boolean isDirectory;//监控的是文件还是目录
    private String workSpace;
    Monitor(int monitorType,int taskType,String monitorPath){
        this.monitorPath = monitorPath;
        this.monitorType = monitorType;
        this.taskType = taskType;
        File file = new File(monitorPath);
        this.isDirectory = file.isDirectory();
        this.workSpace = file.getParent();
    }
    public void run(){
        if(!isDirectory){//针对文件
            boolean stopMonitor = false;
            //just pick the first one.
            List<FileInfo> beforeFileInfoList = Main.safeFile.getFileInfo(true,workSpace,monitorPath,monitorType);
            FileInfo beforeFileInfo = null;
            //定位到监控的文件
            for (FileInfo aBeforeFileInfoList : beforeFileInfoList) {
                if(aBeforeFileInfoList==null)
                    break;
                if (aBeforeFileInfoList.getFilepath().equals(monitorPath)) {
                    beforeFileInfo = aBeforeFileInfoList;
                    break;
                }
            }
            if(beforeFileInfo==null) stopMonitor = true;
            while(true) {
                if (stopMonitor)
                    break;
                List<FileInfo> currentSnap = Main.safeFile.getFileInfo(false,workSpace,beforeFileInfo.getFilepath(), monitorType);
                compareCheckIsNewAdd(beforeFileInfoList,currentSnap);//对比工作区,将不是新增的文件全部标记为非新增
                switch (monitorType) {
                    case RENAME:
                        if(isExist(beforeFileInfo,currentSnap))//存在退出
                            break;
                        stopMonitor = true;
                        for (FileInfo watch : currentSnap) {
                            if(!watch.isNewAdd())
                                continue;
                            if (watch.getLastModified() == beforeFileInfo.getLastModified() &&
                                    watch.getSize() == beforeFileInfo.getSize()) { //found the rename file
                                //found the rename file.carry out the task
                                switch (taskType){
                                    case SUMMARY:
                                        Main.summary.addRenameTrigger();
                                        beforeFileInfo.setFilepath(watch.getFilepath());
                                        break;
                                    case DETAIL:
                                        String info = beforeFileInfo.getFilepath()+" rename to " +watch.getFilepath()+"\n";
                                        Main.detail.addRenameTrigger(info);
                                        beforeFileInfo.setFilepath(watch.getFilepath());
                                        break;
                                    case RECOVER:
                                        Main.safeFile.recoverFile(watch.getFilepath(),beforeFileInfo.getFilepath());
                                        watch.setFilepath(beforeFileInfo.getFilepath());
                                        break;
                                    default:break;
                                }
                                stopMonitor = false;
                                break;
                            }
                        }
                        break;
                    case MODIFY:
                        if(currentSnap==null){ //文件不存在,即将退出监控
                            stopMonitor = true;
                        }else if(currentSnap.get(0).getLastModified() != beforeFileInfo.getLastModified()){
                            switch(taskType){
                                case SUMMARY:
                                    Main.summary.addModifyTrigger();
                                    break;
                                case DETAIL:
                                    String info = beforeFileInfo.getFilepath() + " lastModified from " +
                                            Main.simpleDateFormat.format(beforeFileInfo.getLastModified())+
                                            " to "+Main.simpleDateFormat.format(currentSnap.get(0).getLastModified())+"\n";
                                    Main.detail.addModifyTrigger(info);
                                    break;
                                default:break;
                            }
                            beforeFileInfo.setLastModified(currentSnap.get(0).getLastModified());
                        }
                        break;
                    case PATH_CHANGE:
                        if(isExist(beforeFileInfo,currentSnap))//存在退出
                            break;
                        if(currentSnap.size()==0){
                            stopMonitor = true; //当文件不存在且其他目录没有找到的时候,监控对象丢失.
                        }else{//其他目录存在同名文件,进入判断.
                            stopMonitor = true;
                            for (FileInfo target : currentSnap) {
                                if(!target.isNewAdd())
                                    continue;
                                if(target.getLastModified()==beforeFileInfo.getLastModified() &&
                                        target.getSize() == beforeFileInfo.getSize()) {//满足修改时间,大小一样才可以
                                    switch (taskType){
                                        case SUMMARY:
                                            Main.summary.addPathChangeTrigger();
                                            beforeFileInfo.setFilepath(target.getFilepath());
                                            break;
                                        case DETAIL:
                                            String info = beforeFileInfo.getFilepath()+" move to "+target.getFilepath()+"\n";
                                            Main.detail.addPathChangeTrigger(info);
                                            beforeFileInfo.setFilepath(target.getFilepath());
                                            break;
                                        case RECOVER:
                                            Main.safeFile.recoverFile(target.getFilepath(),beforeFileInfo.getFilepath());
                                            target.setFilepath(beforeFileInfo.getFilepath());
                                            break;
                                        default:break;
                                    }
                                    stopMonitor = false;
                                    break;
                                }
                            }
                        }
                        break;
                    case SIZE_CHANGE://这个不会触发停止,因为需要支持新增和删除qaq.
                        if(currentSnap==null && beforeFileInfo.getSize()>=0){//文件刚丢失,触发
                            switch (taskType){
                                case SUMMARY:
                                    Main.summary.addSizeChangeTrigger();
                                    break;
                                case DETAIL:
                                    String info = beforeFileInfo.getFilepath() + " size from "+beforeFileInfo.getSize()+"B to"+
                                            " none\n";
                                    Main.detail.addSizeChangeTrigger(info);
                                    break;
                                default:break;
                            }
                            beforeFileInfo.setSize(-1);//设置小于0,表示文件不存在
                        }else if(currentSnap!=null && beforeFileInfo.getSize()<0) {//文件新增恢复
                            switch (taskType){
                                case SUMMARY:
                                    Main.summary.addSizeChangeTrigger();
                                    break;
                                case DETAIL:
                                    String info = beforeFileInfo.getFilepath() + " size from none to "+
                                            currentSnap.get(0).getSize()+"B\n";
                                    Main.detail.addSizeChangeTrigger(info);
                                    break;
                                default:break;
                            }
                            beforeFileInfo.setSize(currentSnap.get(0).getSize());
                        }else if((currentSnap != null ? currentSnap.get(0).getSize() : 0) !=beforeFileInfo.getSize()){
                            switch (taskType){
                                case SUMMARY:
                                    Main.summary.addSizeChangeTrigger();
                                    break;
                                case DETAIL:
                                    String info = beforeFileInfo.getFilepath() + " size from "+beforeFileInfo.getSize()+"B to "+
                                            (currentSnap != null ? currentSnap.get(0).getSize() : 0) +"B\n";
                                    Main.detail.addSizeChangeTrigger(info);
                                    break;
                                default:break;
                            }
                            beforeFileInfo.setSize(currentSnap != null ? currentSnap.get(0).getSize() : 0);
                        }
                        break;
                    default:
                        break;
                }
                beforeFileInfoList = currentSnap;//工作区切换.
            }
        }else{//针对目录
            HashMap<String,DicInfo> beforeDicInfo = Main.safeFile.getDicInfo(monitorPath,monitorType);
            while(true){
                HashMap<String,DicInfo> currentSnap = Main.safeFile.getDicInfo(monitorPath,monitorType);
                //first check.
                compareCheckIsNewAdd(beforeDicInfo,currentSnap);

                List<String> beforeKeys = new ArrayList<>(beforeDicInfo.keySet());
                List<String> currentKeys = new ArrayList<>(currentSnap.keySet());
                int triggerCount = 0;//计数,统计触发次数,if summary
                List<String> info = new ArrayList<>();//if detailed
                List<String> newPathnameList = new ArrayList<>();//if recover
                List<String> oldPathnameList = new ArrayList<>();//if recover
                switch (monitorType){
                    case RENAME:
                        for (String beforeKey : beforeKeys) {
                            DicInfo before = beforeDicInfo.get(beforeKey);
                            if(currentSnap.get(beforeKey)==null) {//发现不存在,进行重命名寻找查看
                                for(String currentKey : currentKeys) {
                                    DicInfo current = currentSnap.get(currentKey);
                                    if(current.isNewAdd() && current.getParent().equals(before.getParent()) &&
                                            current.getLastModified()==before.getLastModified() &&
                                            current.getSize()==before.getSize()){
                                        switch (taskType){
                                            case SUMMARY:
                                                triggerCount++;
                                                break;
                                            case DETAIL:
                                                info.add(before.getFilepath()+" rename to " +current.getFilepath()+"\n");
                                                break;
                                            case RECOVER:
                                                newPathnameList.add(current.getFilepath());
                                                oldPathnameList.add(before.getFilepath());
                                                current.setFilepath(before.getFilepath());//re back
                                                current.setNewAdd(false);
                                                break;
                                            default:break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        //carry out task.
                        switch (taskType){
                            case SUMMARY:
                                if(triggerCount>0)
                                    Main.summary.addRenameTrigger(triggerCount);
                                break;
                            case DETAIL:
                                if(info.size()>0)
                                    Main.detail.addRenameTrigger(info);
                                break;
                            case RECOVER:
                                if(newPathnameList.size()>0)
                                    Main.safeFile.recoverFile(newPathnameList,oldPathnameList);
                                break;
                            default:break;
                        }
                        break;
                    case MODIFY:
                        for (String beforeKey : beforeKeys) {
                            DicInfo before = beforeDicInfo.get(beforeKey);
                            DicInfo current = currentSnap.get(beforeKey);
                            if(current!=null && current.getLastModified()!=before.getLastModified()){
                                switch (taskType){
                                    case SUMMARY:
                                        triggerCount++;
                                        break;
                                    case DETAIL:
                                        info.add(before.getFilepath() + " lastModified from " +
                                                Main.simpleDateFormat.format(before.getLastModified())+
                                                " to "+Main.simpleDateFormat.format(current.getLastModified())+"\n");
                                        break;
                                    default:break;
                                }
                            }
                        }
                        switch (taskType){
                            case SUMMARY:
                                if(triggerCount>0)
                                    Main.summary.addModifyTrigger(triggerCount);
                                break;
                            case DETAIL:
                                if(info.size()>0)
                                    Main.detail.addModifyTrigger(info);
                                break;
                            default:break;
                        }
                        break;
                    case PATH_CHANGE:
                        for (String beforeKey : beforeKeys) {
                            DicInfo before = beforeDicInfo.get(beforeKey);
                            if (currentSnap.get(beforeKey) == null) {//发现不存在,进行其他路径查询寻找查看
                                for (String currentKey : currentKeys) {
                                    DicInfo current = currentSnap.get(currentKey);
                                    if(current.isNewAdd() && current.getFilename().equals(before.getFilename()) &&
                                            current.getLastModified()==before.getLastModified() &&
                                            current.getSize()==before.getSize()){
                                        switch (taskType){
                                            case SUMMARY:
                                                triggerCount++;
                                                break;
                                            case DETAIL:
                                                info.add(before.getFilepath()+" move to "+current.getFilepath()+"\n");
                                                break;
                                            case RECOVER:
                                                newPathnameList.add(current.getFilepath());
                                                oldPathnameList.add(before.getFilepath());
                                                current.setFilepath(before.getFilepath());//re back
                                                current.setNewAdd(false);
                                                break;
                                            default:break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        //carry out task
                        switch (taskType){
                            case SUMMARY:
                                if (triggerCount>0)
                                    Main.summary.addPathChangeTrigger(triggerCount);
                                break;
                            case DETAIL:
                                if(info.size()>0)
                                    Main.detail.addPathChangeTrigger(info);
                                break;
                            case RECOVER:
                                if(newPathnameList.size()>0)
                                    Main.safeFile.recoverFile(newPathnameList,oldPathnameList);
                                break;
                            default:break;
                        }
                        break;
                    case SIZE_CHANGE:
                        //首先查看哪些被删除了或者大小被修改了-->遍历先前工作区查看当前工作区快照
                        for(String beforeKey : beforeKeys){
                            DicInfo before = beforeDicInfo.get(beforeKey);
                            DicInfo current = currentSnap.get(beforeKey);
                            if(current==null || (current.getSize()!=before.getSize())){//文件/目录被删或者大小发生改变
                                switch (taskType){
                                    case SUMMARY:
                                        triggerCount++;
                                        break;
                                    case DETAIL:
                                        if(current==null)
                                            info.add(before.getFilepath()+" size from "+before.getSize()+"B to none.\n");
                                        else
                                            info.add(before.getFilepath()+" size from "+before.getSize()+"B to "+
                                            current.getSize()+"B\n");
                                        break;
                                    default:break;
                                }
                            }
                        }
                        //其次查看新工作区快照添加了哪些文件或者文件夹
                        for(String currentKey : currentKeys){
                            DicInfo current = currentSnap.get(currentKey);
                            DicInfo before = beforeDicInfo.get(currentKey);
                            if(before==null){
                                switch (taskType){
                                    case SUMMARY:
                                        triggerCount++;
                                        break;
                                    case DETAIL:
                                        info.add(current.getFilepath()+" size from none to "+current.getSize()+"B\n");
                                        break;
                                    default:break;
                                }
                            }
                        }
                        switch (taskType){
                            case SUMMARY:
                                if (triggerCount>0)
                                    Main.summary.addSizeChangeTrigger(triggerCount);
                                break;
                            case DETAIL:
                                if(info.size()>0)
                                    Main.detail.addSizeChangeTrigger(info);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
                beforeDicInfo = currentSnap;//工作区交换.
            }
        }
    }

    String getMonitorPath(){return monitorPath;}
    int getMonitorType(){return monitorType;}
    int getTaskType(){return taskType;}
    //文件工作区对比,将不是新增的FileInfo设置为非新增
    private void compareCheckIsNewAdd(List<FileInfo> beforeList,List<FileInfo> currentList){//just for some situation
        if(currentList==null || beforeList==null){
            return;
        }
        for (FileInfo aCurrentList : currentList) {
            for (FileInfo aBeforeList : beforeList) {
                if (aCurrentList.getFilepath().equals(aBeforeList.getFilepath()))
                    aCurrentList.setNewAdd(false);
            }
        }
    }
    //目录工作区对比,将不是新增的DicInfo设置为非新增,减少判断
    private void compareCheckIsNewAdd(HashMap<String,DicInfo> beforeList,HashMap<String,DicInfo> currentList){
        List<String> current = new ArrayList<>(currentList.keySet());
        for (String aCurrent : current) {
            if (beforeList.get(aCurrent) != null) {
                currentList.get(aCurrent).setNewAdd(false);
            }
        }
    }
    //文件监控检查文件是否还存在(rename/path-change)
    private boolean isExist(FileInfo target,List<FileInfo> currentList){
        boolean exist = false;
        for (FileInfo aCurrentSnap : currentList) {
            if (aCurrentSnap.getFilepath().equals(target.getFilepath())) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
