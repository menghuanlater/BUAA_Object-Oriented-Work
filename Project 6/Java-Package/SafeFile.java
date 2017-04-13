package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-04-10.
 * this class is for adding thread safety to init java.io.File
 */
public class SafeFile implements GlobalConstant{
    //读取信息
    synchronized void readFileInfo(String pathname){
        File file = new File(pathname);
        if(file.exists()){
            System.out.println("Found the path,the information as follows.");
            if(file.isDirectory()){
                File[] fileSets = file.listFiles();
                long size = 0;
                for(File aFileSets : fileSets != null ? fileSets : new File[0]){
                    size += (aFileSets.isDirectory())? 0 : aFileSets.length();
                }
                System.out.print("文件夹名:"+file.getName()+"\t\t\t");
                System.out.print("大小:"+size+"字节"+"\t\t\t");
                System.out.println("修改时间:"+Main.simpleDateFormat.format(file.lastModified()));
            }else{
                System.out.print("文件名:"+file.getName()+"\t\t\t");
                System.out.print("大小:"+file.length()+"字节"+"\t\t\t");
                System.out.println("修改时间:"+Main.simpleDateFormat.format(file.lastModified()));
            }
        }else{
            System.out.println("Sorry,we found the path is illegal.");
        }
    }
    //创建新文件
    synchronized void createNewFile(String pathname,String filename){
        File file = new File(pathname);//得到目录
        if(!file.exists()){//path不存在
            System.out.println("path not exist.");
            return;
        }
        if(!file.isDirectory()){//非目录
            System.out.println("can't add file to a file.");
            return;
        }
        if(!file.canWrite()){
            System.out.println("have not write permission.");
        }
        File newFile = new File(file.getPath()+File.separator+filename);
        try{
            if(newFile.createNewFile()){
                System.out.println("create succeed.");
            }else{
                System.out.println("create failed.");
            }
        }catch (Exception e){
            System.out.println("create failed because new file include new directory.");
        }
    }
    //新建目录+
    synchronized void mkdirs(String pathname){
        File file = new File(pathname);
        if(file.exists()){
            System.out.println("path have exist.");
        }else{
            if(file.mkdirs()){
                System.out.println("create succeed.");
            }else{
                System.out.println("create failed.");
            }
        }
    }
    //删除文件
    synchronized void delete(String pathname){
        File file = new File(pathname);
        if(!file.exists()){
            System.out.println("path not exist.");
            return;
        }
        if(file.isDirectory()){
            System.out.println("can't delete a directory.");
        }
        if(file.delete()){
            System.out.println("delete succeed.");
        }else{
            System.out.println("delete failed.");
        }
    }
    //删除目录
    synchronized void deleteDirectory(String pathname){
        File file = new File(pathname);
        if(!file.exists()){
            System.out.println("path not exist.");
            return;
        }
        if(!file.isDirectory()){
            System.out.println("can't delete a file.");
            return;
        }
        if(!file.canWrite()){
            System.out.println("have not delete permission.");
            return;
        }
        recursiveDelete(file);
        if(file.delete()){
            System.out.println("delete succeed.");
        }else{
            System.out.println(file.getPath()+" don't have delete permission.");
        }
    }
    //递归删除
    private void recursiveDelete(File root){
        File [] lists = root.listFiles();
        if(lists==null){
            System.out.println(root.getPath()+" don't have delete permission.");
            return;
        }
        for (File list : lists) {
            if (list.isDirectory()) {
                recursiveDelete(list);
            }
            if (!list.delete()) {
                System.out.println(list.getPath() + " don't have delete permission.");
            }
        }
    }
    //重命名
    synchronized void renameTo(String pathname,String newFileName){
        File file = new File(pathname);
        if(!file.exists()){
            System.out.println("path not exist.");
            return;
        }
        if(file.isDirectory()){
            System.out.println("can't rename a directory.");
            return;
        }
        String parent = file.getParent();
        String newFilePath = parent+File.separator+newFileName;
        if(file.renameTo(new File(newFilePath))){
            System.out.println("rename succeed.");
        }else{
            System.out.println("rename failed.");
        }
    }
    //执行文件恢复的recover,支持一个文件
    synchronized void recoverFile(String newPathname,String oldPathname){
        File newFile = new File(newPathname);
        File oldFile = new File(oldPathname);
        if(newFile.exists()){
            newFile.renameTo(oldFile);
        }
    }
    //执行文件批处理恢复,针对目录监控
    synchronized void recoverFile(List<String> newPathnameList,List<String> oldPathnameList){
        for(int i=0;i<newPathnameList.size();i++){
            File newFile = new File(newPathnameList.get(i));
            File oldFile = new File(oldPathnameList.get(i));
            if(newFile.exists()){
                newFile.renameTo(oldFile);
            }
        }
    }
    //文件移动,支持一个文件
    synchronized void moveTo(String pathname,String newPath){
        File file = new File(pathname);
        File temp = new File(newPath);
        //如果不存在此文件或者移动到的目录不存在
        if(!file.exists() || !temp.exists()){
            System.out.println("path not exist.");
            return;
        }
        //如果移动的不是文件
        if(file.isDirectory()){
            System.out.println("can't move a directory.");
            return;
        }
        //如果移动到的不是目录
        if(!temp.isDirectory()){
            System.out.println("can't move to a file.");
            return;
        }
        if(file.renameTo(new File(temp.getPath()+File.separator+file.getName()))){
            System.out.println("move success.");
        }else{
            System.out.println("move failed.");
        }
    }
    //文件内容写入,If not exist,then create it.
    synchronized void write(String pathname,String content){
        File file = new File(pathname);
        if(file.isDirectory()){
            System.out.println("can't write a directory.");
            return;
        }
        if(!file.getParentFile().canWrite()){
            System.out.println("file have not write permission.");
            return;
        }
        try {
            FileWriter writer = new FileWriter(pathname,true);
            writer.write(content);
            writer.flush();
            writer.close();
            System.out.println("write succeed.");
        } catch (IOException e) {
            System.out.println("write failed.");
        }
    }
    //监控文件快照
    synchronized List<FileInfo> getFileInfo(boolean init,String workSpace,String pathname, int monitorType){
        File file = new File(pathname);
        List<FileInfo> temp = new ArrayList<>();
        switch (monitorType){
            case RENAME:
                File[] parentList = file.getParentFile().listFiles();
                for(File aParentList : parentList != null ? parentList : new File[0]){
                    if(!aParentList.isDirectory()){
                        FileInfo fileInfo;
                        fileInfo = new FileInfo();
                        fileInfo.setFilepath(aParentList.getPath());
                        fileInfo.setLastModified(aParentList.lastModified());
                        fileInfo.setSize(aParentList.length());
                        temp.add(fileInfo);
                    }
                }
                break;
            case MODIFY:
                if(!file.exists() && init) {//初始且文件不存在
                    return temp;
                }
                if(file.exists()){
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFilepath(file.getPath());
                    fileInfo.setLastModified(file.lastModified());
                    temp.add(fileInfo);
                }else{
                    return null;
                }
                break;
            case PATH_CHANGE:
                File parent = new File(workSpace);
                String name = file.getName();
                recursiveFileRegion(parent,temp,name);
                break;
            case SIZE_CHANGE:
                if(!file.exists() && init) {//初始且文件不存在
                    return temp;
                }
                if(file.exists()) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFilepath(file.getPath());
                    fileInfo.setSize(file.length());
                    temp.add(fileInfo);
                }else{
                    return null;
                }
                break;
            default:break;
        }
        return temp;
    }
    //监控文件路径变化的工作区深度递归函数
    private void recursiveFileRegion(File parent,List<FileInfo> temp,String filename){
        File[] lists = parent.listFiles();
        for(File aLists : lists != null ? lists : new File[0]){
            if(aLists.isDirectory()){
                recursiveFileRegion(aLists,temp,filename);
            }else if(aLists.getName().equals(filename)){
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFilepath(aLists.getPath());
                fileInfo.setLastModified(aLists.lastModified());
                fileInfo.setSize(aLists.length());
                temp.add(fileInfo);
            }
        }
    }
    //监控目录树快照
    synchronized HashMap<String,DicInfo> getDicInfo(String pathname,int monitorType){
        File file = new File(pathname);
        HashMap<String,DicInfo> temp = new HashMap<>(HASH);
        recursiveFileRegion(file,temp,monitorType);
        return temp;
    }
    //深度递归
    private void recursiveFileRegion(File parent,HashMap<String,DicInfo> temp,int monitorType){
        File[] lists = parent.listFiles();
        //first judge where parent is needed to add
        if(monitorType==MODIFY){
            DicInfo dicInfo = new DicInfo();
            dicInfo.setDirectory(true);
            dicInfo.setFilepath(parent.getPath());
            dicInfo.setLastModified(parent.lastModified());
            temp.put(parent.getPath(),dicInfo);
        }else if(monitorType==SIZE_CHANGE){
            DicInfo dicInfo = new DicInfo();
            dicInfo.setDirectory(true);
            dicInfo.setFilepath(parent.getPath());
            File[] iter = parent.listFiles();
            long size = 0;
            for (File anIteList : iter != null ? iter : new File[0]) {
                if (!anIteList.isDirectory())
                    size += anIteList.length();
            }
            dicInfo.setSize(size);
            temp.put(parent.getPath(),dicInfo);
        }
        //second judge son file system
        for(int i = 0; i< (lists != null ? lists.length : 0); i++){
            File aList = lists[i];
            switch (monitorType) {
                case RENAME:
                    if (aList.isDirectory()) {
                        recursiveFileRegion(aList, temp, monitorType);
                    } else {
                        DicInfo dicInfo = new DicInfo();
                        dicInfo.setFilepath(aList.getPath());//设置路径
                        dicInfo.setParent(aList.getParent());//设置父目录名
                        dicInfo.setSize(aList.length());//设置文件大小
                        dicInfo.setLastModified(aList.lastModified());//设置最后修改时间
                        temp.put(aList.getPath(), dicInfo);
                    }
                    break;
                case MODIFY:
                    DicInfo dicInfo = new DicInfo();
                    if(aList.isDirectory()){
                        recursiveFileRegion(aList,temp,monitorType);
                    }else {
                        dicInfo.setFilepath(aList.getPath());//设置路径
                        dicInfo.setLastModified(aList.lastModified());//设置修改时间
                        temp.put(aList.getPath(), dicInfo);
                    }
                    break;
                case PATH_CHANGE:
                    if(aList.isDirectory()){
                        recursiveFileRegion(aList,temp,monitorType);
                    }else{
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    dicInfo = new DicInfo();
                        dicInfo.setFilepath(aList.getPath());//设置路径
                        dicInfo.setFilename(aList.getName());//设置文件名
                        dicInfo.setSize(aList.length());//设置文件大小
                        dicInfo.setLastModified(aList.lastModified());//设置最后修改时间
                        temp.put(aList.getPath(),dicInfo);
                    }
                    break;
                case SIZE_CHANGE:
                    dicInfo = new DicInfo();
                    if(aList.isDirectory()){
                        recursiveFileRegion(aList,temp,monitorType);
                    }else{
                        dicInfo.setSize(aList.length());
                        dicInfo.setFilepath(aList.getPath());
                        temp.put(aList.getPath(),dicInfo);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
