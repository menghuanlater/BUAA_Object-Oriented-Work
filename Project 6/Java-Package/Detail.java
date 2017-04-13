package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-04-10.
 */
class Detail {
    private BufferedWriter bufferedWriter;
    private List<String> renameTrigger;
    private List<String> modifyTrigger;
    private List<String> pathChangeTrigger;
    private List<String> sizeChangeTrigger;
    Detail(){
        renameTrigger = new ArrayList<>();
        modifyTrigger = new ArrayList<>();
        pathChangeTrigger = new ArrayList<>();
        sizeChangeTrigger = new ArrayList<>();
        try{
            Path outFile = Paths.get("detail.txt");
            bufferedWriter = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    synchronized void addRenameTrigger(String info){
        renameTrigger.add(info);
    }
    synchronized void addRenameTrigger(List<String> info){
        renameTrigger.addAll(info);
    }
    synchronized void addModifyTrigger(String info){
        modifyTrigger.add(info);
    }
    synchronized void addModifyTrigger(List<String> info){
        modifyTrigger.addAll(info);
    }
    synchronized void addPathChangeTrigger(String info){
        pathChangeTrigger.add(info);
    }
    synchronized void addPathChangeTrigger(List<String> info){
        pathChangeTrigger.addAll(info);
    }
    synchronized void addSizeChangeTrigger(String info){
        sizeChangeTrigger.add(info);
    }
    synchronized void addSizeChangeTrigger(List<String> info){
        sizeChangeTrigger.addAll(info);
    }
    synchronized void writeToOutFile(){
        try {
            bufferedWriter.write("\n------------------------------\n");
            bufferedWriter.write("write back time:"+Main.simpleDateFormat.format(System.currentTimeMillis())+"\n");

            bufferedWriter.write("renamed trigger触发信息:\n");
            for (String aRenameTrigger : renameTrigger) bufferedWriter.write(aRenameTrigger);

            bufferedWriter.write("Modified trigger触发信息:\n");
            for (String aModifyTrigger : modifyTrigger) bufferedWriter.write(aModifyTrigger);

            bufferedWriter.write("path-changed trigger触发信息:\n");
            for (String aPathChangeTrigger : pathChangeTrigger) bufferedWriter.write(aPathChangeTrigger);

            bufferedWriter.write("size-changed trigger触发信息:\n");
            for (String aSizeChangeTrigger : sizeChangeTrigger) bufferedWriter.write(aSizeChangeTrigger);

            bufferedWriter.flush();
            renameTrigger.clear();
            modifyTrigger.clear();
            pathChangeTrigger.clear();
            sizeChangeTrigger.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
