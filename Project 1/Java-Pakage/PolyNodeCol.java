package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ****** on 2017-03-05.
 * 多项式一个子项结点
 */
public class PolyNodeCol {
    private final int MAX_VALUE = 100000;
    private final int MIN_VALUE = -100000;
    private int coefficient;//系数
    private int power;//指数
    public PolyNodeCol(String target,int minus){
        String pattern = "(.*),(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(target);
        if(m.find()) {
            try {
                if(Integer.parseInt(m.group(1)) >= MAX_VALUE || Integer.parseInt(m.group(1)) <= MIN_VALUE){
                    System.out.println("Sorry,检测到数对中存在系数不在规定范围的数据!");
                    System.exit(0);
                }else {
                    this.coefficient = minus*Integer.parseInt(m.group(1));
                }
                if(Integer.parseInt(m.group(2)) >= MAX_VALUE || Integer.parseInt(m.group(2)) < 0){
                    System.out.println("Sorry,检测到数对中存在指数不在规定范围的数据!");
                    System.exit(0);
                }else {
                    this.power = Integer.parseInt(m.group(2));
                }
            }catch(Exception e){
                System.out.println("Sorry,检测到输入的数对中有不是纯数据存在!");
                System.exit(0);
            }
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
