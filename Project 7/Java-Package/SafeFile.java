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
 * 一次只能由一个出租车输出信息到文件
 */
class SafeFile implements GlobalConstant{
    private BufferedWriter bw;
    private HashMap<String,String> allInfoSets = new HashMap<>();
    SafeFile(String filename){
        try {
            Path outFile = Paths.get(filename);
            bw = Files.newBufferedWriter(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized void writeToFile(String hashString,String info){
        String temp = allInfoSets.get(hashString);
        if(temp!=null)
            allInfoSets.put(hashString,temp+info+"\n");
        else
            allInfoSets.put(hashString,info+"\n");
    }
    void closeFileOutStream(){
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized void outPutToFile(String request){//为请求完成服务的函数
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
