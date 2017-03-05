package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ****** on 2017-03-05.
 * 多项式一个子项结点
 */
public class PolyNodeCol {
    private int coefficient;//系数
    private int power;//指数
    public PolyNodeCol(String target,int minus){
        String pattern = "(.*),(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(target);
        if(m.find()) {
            this.coefficient = minus * Integer.parseInt(m.group(1));
            this.power = Integer.parseInt(m.group(2));
        }
    }
    public int getCoefficient(){
        return coefficient;
    }
    public int getPower(){
        return power;
    }
    public void addCoefficient(int add){
        this.coefficient += add;
    }
}
