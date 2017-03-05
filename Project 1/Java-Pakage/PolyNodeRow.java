package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ****** on 2017-03-05.
 * 一个多项式
 */
public class PolyNodeRow {
    static final int MAX_COL = 51;
    private PolyNodeCol[] polySetsCol = new PolyNodeCol[MAX_COL];
    private int colCount;
    public PolyNodeRow(){
        colCount = 0;
    }
    public void addPolyNode(String target){
        int minus;
        if(target.charAt(0)=='-')
            minus = -1;
        else
            minus = 1;
        String pattern = "(?<=\\()(.*?)(?=\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(target);
        while(m.find()){
            polySetsCol[colCount++] = new PolyNodeCol(m.group(1),minus);
        }
    }
    public PolyNodeCol getNodeAtCol(int position){
        return polySetsCol[position];
    }
    public int getColCount(){
        return colCount;
    }
}
