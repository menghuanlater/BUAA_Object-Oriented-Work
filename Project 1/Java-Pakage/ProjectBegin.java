package core;

import java.util.Scanner;

/**
 * Created by ****** on 2017-03-05.
 * main函数所在类,负责获取终端输入数据
 */
public class ProjectBegin {
    static int sum = 0;//得到字典树有效结点的个数
    private static GetPoly myPoly = null;
    private static Functions quoteFunction = new Functions();
    private static TrieNode trieHead = new TrieNode();
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);//获得系统输入的对象
        System.out.print("请输入所要计算的多项式:");

        String initExp = input.nextLine();//用户输入的多项式
        myPoly = new GetPoly(initExp);//原多项式处理实例对象
        /*如果输入的表达式合法，则可以进行正则表达式匹配,将所有的最小化多项式写入数组
        * 否则将提示用户是否重新输入*/
        while(!myPoly.isLegal()){
            System.out.print("是否重新输入(输入'y'表示重新输入，输入其他字符表示结束程序):");
            String button = input.nextLine();
            if(button.equals("y")) {
                System.out.print("请输入所要计算的多项式:");
                initExp = input.nextLine();
                myPoly = new GetPoly(initExp);
            }else{
                input.close();
                System.exit(0);
            }
        }

        myPoly.setPolySetsRow();
        //字典树建立
        for(int i=0,rowLength = myPoly.getRowCount();i<rowLength;i++){
            for(int j=0,colLength = myPoly.getNodeAtRow(i).getColCount();j<colLength;j++){
                quoteFunction.buildDictTree(trieHead,myPoly,i,j);
            }
        }

        //遍历字典树
        ValidTrie validTrie = new ValidTrie();
        quoteFunction.depthFirstSearch(trieHead,validTrie);

        //快速排序
        quoteFunction.quickSort(validTrie,0,sum-1);

        //结果输出
        System.out.print("{");
        for(int i=0;i<sum;i++){
            if(i!=0)
                System.out.print(",");
            PolyNodeCol temp = validTrie.getNodeAt(i);
            System.out.print("("+temp.getCoefficient()+","+temp.getPower()+")");
        }
        System.out.println("}");

        input.close();//关闭系统输入对象
    }
}
