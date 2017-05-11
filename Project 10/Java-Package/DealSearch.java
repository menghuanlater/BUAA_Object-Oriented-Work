package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created on 17-4-16.
 * info write to stdout and file is search.txt
 */
public class DealSearch extends Thread implements GlobalConstant{
    /*
    Overview:从搜索查询出租车的请求队列中获取请求,根据指令要求实现相关查询,输出信息到文件以及终端
     */
    private RequestQueue<SearchRequest> queue;
    private BufferedWriter bufferedWriter;
    DealSearch(RequestQueue<SearchRequest> queue){
        /*@REQUIRES:queue!=null
        @MODIFIES:this.queue,this.bufferedWriter
        @EFFECTS:normal_behavior:给this.queue赋值,正常打开文件输出流
                 文件输出流打开失败==>exceptional_behavior:(IOException)打印异常处理栈
        */
        this.queue = queue;
        Path outFile = Paths.get(SEARCH_OUT);
        try {
            bufferedWriter = Files.newBufferedWriter(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*@repOk.
    check:queue!=null && queue is instanceof RequestQueue && bufferedWriter!=null
          && bufferedWriter is instanceof BufferedWriter
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result = invariant(this)
         */
        if(queue==null) return false;
        if(bufferedWriter==null) return false;
        Object x = queue;
        Object y = bufferedWriter;
        if(!(x instanceof RequestQueue)) return false;
        if(!(y instanceof BufferedWriter)) return false;
        return true;
    }
    public void run(){
        /*@REQUIRES:Main.taxiSets have been build(100辆出租车实例化)
        @MODIFIES:bufferedWriter
        @EFFECTS:不停扫描请求队列，没有则等待，有则取出处理搜索请求,调用相关类方法将处理信息输出到终端以及文件输出流
                文件输出流写入失败==>exceptional_behavior:(IOException)打印异常处理栈信息
        */
        while(true){
            SearchRequest target = queue.getFrontRequest();
            String info;
            if(target.isTaxiSearch()) {
                Taxi taxi = Main.taxiSets[target.getTaxiCode()].clone();
                info = target.toString() + "\t--->\t" + taxi.toString();
            }else{
                int status = target.getTaxiStatus();
                info = target.toString()+"\t处于";
                info += (status==STOP_SERVICE)? "停止服务状态的出租车编号:" :
                        (status==IN_SERVICE)? "正在服务状态(有乘客在车上)的出租车编号:":
                                (status==WAIT_SERVICE)?"等待服务状态的出租车编号:":
                                        (status==GRAB_SERVICE)?"正在前往接客状态的出租车编号:":"";
                boolean isHave = false;
                for(int i=0;i<SUM_CARS;i++){
                    if(Main.taxiSets[i].getCurrentStatus()==status) {
                        info += i + " ";
                        isHave = true;
                    }
                }
                if(!isHave) info+="Not Found.";
            }
            Main.outPutInfoToTerminal(info);
            try {
                bufferedWriter.write(info+"\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}