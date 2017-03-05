package core;

import java.util.Scanner;

/**
 * Created by ****** on 2017-03-05.
 * main函数所在类,负责获取终端输入数据
 */
public class ProjectBegin {
    GetPoly myPoly = null;
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);//获得系统输入的对象
        ProjectBegin myCore = new ProjectBegin();//实例化核心类对象，为了不使用static修饰符
        System.out.print("请输入所要计算的多项式:");

        String initExp = input.nextLine();//用户输入的多项式
        myCore.myPoly = new GetPoly(initExp);//原多项式处理实例对象
        /*如果输入的表达式合法，则可以进行正则表达式匹配,将所有的最小化多项式写入数组
        * 否则将提示用户是否重新输入*/
        while(!myCore.myPoly.isLegal()){
            System.out.print("是否重新输入(输入'y'表示重新输入，输入其他字符表示结束程序):");
            String button = input.nextLine();
            if(button.equals("y")) {
                System.out.print("请输入所要计算的多项式:");
                initExp = input.nextLine();
                myCore.myPoly = new GetPoly(initExp);
            }else{
                input.close();
                System.exit(0);
            }
        }
        myCore.myPoly.setPolySetsRow();
        //字典树建立

        input.close();//关闭系统输入对象
    }
}
