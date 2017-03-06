package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ****** on 2017-03-05.
 * main函数所在类,负责获取终端输入数据
 */
public class ProjectBegin {
    static int sum = 0;//得到字典树有效结点的个数
    private static GetPoly myPoly = null;
    private static Functions quoteFunction = new Functions();
    private static TrieNode trieHead = new TrieNode();
    public static void main(String[] args) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("请输入所要计算的多项式:");

        try {
            String initExp = bufferedReader.readLine();//用户输入的多项式
            myPoly = new GetPoly(initExp);//原多项式处理实例对象
            /*只针对输入非法字符、括号符号等不匹配可重新选择输入，其余一律停止程序*/
            while (!myPoly.isLegal()) {
                System.out.print("是否重新输入(输入'y'表示重新输入，输入其他字符表示结束程序):");
                String button = bufferedReader.readLine();
                if (button.equals("y")) {
                    System.out.print("请输入所要计算的多项式:");
                    initExp = bufferedReader.readLine();
                    myPoly = new GetPoly(initExp);
                } else {
                    bufferedReader.close();
                    System.exit(0);
                }
            }
            myPoly.setPolySetsRow();
            //字典树建立
            for (int i = 0, rowLength = myPoly.getRowCount(); i < rowLength; i++) {
                for (int j = 0, colLength = myPoly.getNodeAtRow(i).getColCount(); j < colLength; j++) {
                    quoteFunction.buildDictTree(trieHead, myPoly, i, j);
                }
            }

            //遍历字典树
            ValidTrie validTrie = new ValidTrie();
            quoteFunction.depthFirstSearch(trieHead, validTrie);

            //快速排序
            quoteFunction.quickSort(validTrie, 0, sum - 1);

            //结果输出
            System.out.print("{");
            boolean comaFlag = true;
            for (int i = 0; i < sum; i++) {
                if (i != 0 && comaFlag)
                    System.out.print(",");
                PolyNodeCol temp = validTrie.getNodeAt(i);
                int coefficient = temp.getCoefficient();
                if (coefficient == 0) {
                    comaFlag = false;
                    continue;
                }
                comaFlag = true;
                System.out.print("(" + coefficient + "," + temp.getPower() + ")");
            }
            System.out.println("}");
        }catch (Exception e){
            System.out.println("输入数据过于长,程序退出!");
        }
        bufferedReader.close();
    }
}
