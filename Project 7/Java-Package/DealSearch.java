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
    private SearchRequestQueue queue;
    private Path outFile;
    private BufferedWriter bufferedWriter;
    DealSearch(SearchRequestQueue queue){
        this.queue = queue;
        outFile = Paths.get(SEARCH_OUT);
        try {
            bufferedWriter = Files.newBufferedWriter(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        while(true){
            SearchRequest target = queue.getFrontRequest();
            Taxi taxi = Main.taxiSets[target.getTaxiCode()].clone();
            String info = target.toString()+"\t--->\t"+taxi.toString();
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
