package core;

/**
 * Created by ****** on 2017-03-05.
 * 检查输入是否合法规范(缓冲区溢出在读取阶段就实行检测)
 */
public class CheckInputExp {
    public boolean isLegal(String expression){
        if(checkIllegalChar(expression)){
            System.out.println("Sorry,检测到输入存在非法字符!");
            return false;
        }
        if(checkIllegalMatch(expression))
            return false;
        return true;
    }
    private boolean checkIllegalChar(String expression){//非法字符检测函数
        for(int i=0,loopLength = expression.length();i<loopLength;i++){
            char checkChar = expression.charAt(i);
            if(!(checkChar=='+' || checkChar=='-' || checkChar=='{' || checkChar=='}' || checkChar=='(' || checkChar==')'
                || checkChar==',' || (checkChar>='0' && checkChar<='9'))){
                return true;
            }
        }
        return false;
    }
    private boolean checkIllegalMatch(String expression) {//检测括号是否匹配，以及(x)形式的问题
        final int STACK = 5;//验证符号匹配的栈空间大小
        char stack[] = new char[STACK];
        int top = -1;
        boolean flag = false;//当遇到(时为true，正确匹配小括号内的逗号时为false
        for(int i=0,loopLength = expression.length();i<loopLength;i++){
            char target = expression.charAt(i);
            if(target=='{'){//检测到{
                if(top!=-1){
                    System.out.println("Sorry,检测到输入存在'{'不匹配的情况!");
                    return true;
                }else
                    stack[++top] = target;
            }else if(target=='('){//检测到(
                if(top==-1){
                    System.out.println("Sorry,检测到输入存在'(c,n)'数对不在'{}'里的情况!");
                    return true;
                }else if(stack[top]=='('){
                    System.out.println("Sorry,检测到输入存在'('不匹配的情况!");
                    return true;
                }else{
                    stack[++top] = target;
                    flag = true;
                }
            }else if(target=='}'){//检测到}
                top = -1;
            }else if(flag && target==','){//检测到小括号内的逗号
                flag = false;
            }else if(flag && target==')'){//检测到小括号内没有逗号的情况
                System.out.println("Sorry,检测到输入的数对'(c,n)'存在','分隔符缺失7的情况!");
                return true;
            }else if(!flag && target==')'){//小括号内有逗号并检测到右小括号
                if(stack[top]=='{'){
                    System.out.println("Sorry,检测到输入存在')'不匹配的情况!");
                    return true;
                }else
                    top--;
            }
        }
        if(top!=-1){
            System.out.println("Sorry,检测到输入存在'{'不匹配的情况!");
            return true;
        }
        return false;
    }
}
