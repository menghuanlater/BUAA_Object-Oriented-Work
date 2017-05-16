package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17-4-18.
 */
public class SafeFile implements GlobalConstant{
    /*
    Overview:文件写入安全类,防止同时请求太多导致输出乱序
     */
    private BufferedWriter bw;
    private final HashMap<String,String> allInfoSets = new HashMap<>();
    SafeFile(String filename){
        /*@REQUIRES:filename!=null && filename为符合系统文件命名规则的文件(需要读写权限)
        @MODIFIES:bw(文件输出流对象)
        @EFFECTS:normal_behavior:如果文件存在==>清空文件内容;如果文件不存在则创建文件
                 文件输出流打开失败==>exceptional_behavior:(IOException)打印异常栈信息
        */
        try {
            Path outFile = Paths.get(filename);
            bw = Files.newBufferedWriter(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*@repOk.
    check:bw!=null(由于HashMap allInfoSets是final修饰,无需检查)
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        return !(bw==null);
    }
    synchronized void writeToFile(String hashString,String info){
        /*@REQUIRES:hashString and info都不为null
        @MODIFIES:allInfoSets(哈希Map)
        @EFFECTS:if(hashString is one key of allInfoSets)==>哈希键值对应的String拼接info构成新的键值
                  else 新建一个键值对 hashString->info
        @THREAD_REQUIRES:\locked(allInfoSets)
        @THREAD_EFFECTS:\locked(allInfoSets)更新同步allInfoSets.
        */
        String temp = allInfoSets.get(hashString);
        if(temp!=null)
            allInfoSets.put(hashString,temp+info+"\n");
        else
            allInfoSets.put(hashString,info+"\n");
    }
    void closeFileOutStream(){
        /*@REQUIRES:NONE
        @MODIFIES:bw
        @EFFECTS:normal_behavior:关闭文件输出流成功
                 关闭输出流失败==>exceptional_behavior:打印异常处理栈
        */
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized void outPutToFile(String request){//为请求完成服务的函数
        /*@REQUIRES:request!=null && Contain(request).(allInfoSets.Keys())
        @MODIFIES:bw,allInfoSets
        @EFFECTS:normal_behavior:打印出租车处理整个请求的全过程数据到文件,刷新缓冲区
                 exceptional_behavior(IOException)打印异常处理栈
                 无论是否出现异常,删除哈希map对应的键值对
        @THREAD_REQUIRES:\locked(bw,allInfoSets)
        @THREAD_EFFECTS:\locked()
        */
        try {
            bw.write("请求********"+request+"***************\n");
            bw.write(allInfoSets.get(request));
            bw.write("\n");//分隔
            bw.flush();//刷新缓冲区
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            allInfoSets.remove(request);//清除处理完的请求
        }
    }
    synchronized void outPutToFile(){//为扫描请求发出时周围所有的出租车信息服务
        /*@REQUIRES:None
        @MODIFIES:bw,allInfoSets
        @EFFECTS:normal_behavior:打印请求发出时周围所有出租车信息数据到文件,刷新缓冲区
                 exceptional_behavior(IOException)打印异常处理栈
                 无论是否出现异常,清空整个哈希表
        @THREAD_REQUIRES:\locked(bw,allInfoSets)
        @THREAD_EFFECTS:\locked()
        */
        for (Object o : allInfoSets.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            try {
                bw.write("请求********" + entry.getKey() + "***************\n");
                bw.write((String) entry.getValue());
                bw.write("\n");//分隔
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        allInfoSets.clear();
    }
}
