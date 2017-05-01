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
class DealSearch extends Thread implements GlobalConstant{
    private RequestQueue<SearchRequest> queue;
    private BufferedWriter bufferedWriter;
    DealSearch(RequestQueue<SearchRequest> queue){
        this.queue = queue;
        Path outFile = Paths.get(SEARCH_OUT);
        try {
            bufferedWriter = Files.newBufferedWriter(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
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