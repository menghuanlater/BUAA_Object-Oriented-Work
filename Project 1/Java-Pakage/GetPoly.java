package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ****** on 2017-03-05.
 * 正则匹配获取多项式的所有子项
 */
public class GetPoly {
    static final int MAX_ROW = 21;
    private String expression;//规整表达式
    private PolyNodeRow[] polySetsRow = new PolyNodeRow[MAX_ROW];
    private int rowCount;
    private CheckInputExp myCheck = new CheckInputExp();
    private boolean isLegal;

    public GetPoly(String initExp){
        this.expression = initExp.replaceAll(" ","");//去除所有的空格
        this.isLegal = myCheck.isLegal(this.expression);
        this.rowCount = 0;
    }
    public boolean isLegal(){
        return this.isLegal;
    }
    public void setPolySetsRow(){
        String pattern = "[+-]*\\{(.*?)\\}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(expression);
        while(m.find()){
            polySetsRow[rowCount] = new PolyNodeRow();
            polySetsRow[rowCount].addPolyNode(m.group(0));
            rowCount++;
        }
    }
    public PolyNodeRow getNodeAtRow(int position){
        return polySetsRow[position];
    }
    public int getRowCount(){
        return rowCount;
    }
}
