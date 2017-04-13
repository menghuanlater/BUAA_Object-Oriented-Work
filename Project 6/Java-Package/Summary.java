package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created on 2017-04-10.
 */
class Summary {
    private long renameTrigger;//重命名触发器触发次数
    private long modifyTrigger;//修改时间触发器
    private long pathChangeTrigger;//路径改变触发器
    private long sizeChangeTrigger;//大小改变触发器
    private BufferedWriter bufferedWriter;
    Summary(){
        renameTrigger = 0;
        modifyTrigger = 0;
        pathChangeTrigger = 0;
        sizeChangeTrigger = 0;
        Path outFile = Paths.get("summary.txt");
        try {
            bufferedWriter = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized void addRenameTrigger(){
        renameTrigger++;
    }
    synchronized void addRenameTrigger(int n){
        renameTrigger+=n;
    }
    synchronized void addModifyTrigger(){
        modifyTrigger++;
    }
    synchronized void addModifyTrigger(int n){
        modifyTrigger+=n;
    }
    synchronized void addPathChangeTrigger(){
        pathChangeTrigger++;
    }
    synchronized void addPathChangeTrigger(int n){
        pathChangeTrigger+=n;
    }
    synchronized void addSizeChangeTrigger(){
        sizeChangeTrigger++;
    }
    synchronized void addSizeChangeTrigger(int n){
        sizeChangeTrigger+=n;
    }
    synchronized void writeToOutFile(){
        try {
            bufferedWriter.write("\n------------------------------\n");
            bufferedWriter.write("write back time:"+Main.simpleDateFormat.format(System.currentTimeMillis())+"\n");
            bufferedWriter.write("renamed Trigger触发次数:"+renameTrigger+"\n");
            bufferedWriter.write("Modified Trigger触发次数:"+modifyTrigger+"\n");
            bufferedWriter.write("path-changed Trigger触发次数:"+pathChangeTrigger+"\n");
            bufferedWriter.write("size-changed Trigger触发次数"+sizeChangeTrigger+"\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
