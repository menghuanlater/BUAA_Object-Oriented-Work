/*多项式加减*/
//不处理输入错误的容错响应
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//结构体以及宏定义
#define MAX_ROW 21
#define MAX_COL 51
#define DICT 10
#define MAX_CHAR 30000//最大输入字符数
typedef struct
{
    int flag;//存在标志
    long coefficient;//系数
    long power;//指数
}PolyNode,*PolyNodePtr;
typedef struct Dict
{
    int flags[DICT];
    int exist;
    PolyNodePtr current;
    struct Dict *links[DICT];
}TrieNode,*TrieNodePtr;
//全局变量
PolyNode mySets[MAX_ROW][MAX_COL];
TrieNodePtr trieHead;//字典树的头
int rowCount,colCount,sum;
PolyNodePtr *myNodes;
int indexDict;
//函数声明区
void outInputInfo(); //打印输入要求
void initialMySets();
void getPoly();//获取输入,填充mySets数组
void buildDictTree(int,int);
void DFS(TrieNodePtr);//遍历字典树
void quickSort(int,int);//快排
int getPivot(int,int);
/***********/
int main(void)
{
    outInputInfo();//打印输入提示
    trieHead = (TrieNodePtr)malloc(sizeof(TrieNode));//字典树头结点申请数据堆空间
    initialMySets();//初始化
    getPoly();//得到所有表达式

    int i,j;
    for(i=0;i<rowCount;i++){
        for(j=0;mySets[i][j].flag;j++){
            buildDictTree(i,j);
        }
    }
    myNodes = (PolyNodePtr *)malloc(sizeof(PolyNodePtr)*sum);
    DFS(trieHead);//得到结果集

    quickSort(0,indexDict-1);
    putchar('{');
    for(i=0;i<indexDict;i++) {
        if(i!=0)
            putchar(',');
        long coefficient = myNodes[i]->coefficient;
        if(coefficient==0)
            continue;
        printf("(%ld,%ld)", myNodes[i]->coefficient, myNodes[i]->power);
    }
    printf("}\n");
    free(trieHead);free(myNodes);
    return 0;
}
//函数实现区
void outInputInfo()
{
    printf("**********************************************************\n");
    printf("输入仅由 “ 0-9 + - , ( ) { } ”与空格这几种字符组成\n");
    printf("下面是一个符合输入条件的例子!\n");
    printf("{(3,0), (2,2), (12,3)} + {(3,1), (-5,3)} - {(-199,2), (29,3),(10,7)}\n");
    printf("**********************************************************\n");
    printf("请输入表达式:");
}
void initialMySets()
{
    int i,j;
    for(i=0;i<MAX_ROW;i++){
        for(j=0;j<MAX_COL;j++){
            mySets[i][j].flag = 0;
        }
    }
    for(i=0;i<DICT;i++){
        trieHead->flags[i] = 0;
    }
    trieHead->exist = 0;
}
void getPoly()
{
    FILE *in = fopen("text.txt","r");
    char targetString1[MAX_CHAR] = {'\0'};
    char targetString[MAX_CHAR] = {'\0'};
    fgets(targetString1,MAX_CHAR*sizeof(char),in);
    fclose(in);
    *(targetString1+strlen(targetString1)-1) = '\0';//消除换行符
    int i,j=0;
    for(i=0;*(targetString1+i)!='\0';i++){//消除所有的空格
        if(*(targetString1+i)!=' ') {
            *(targetString + j) = *(targetString1 + i);
            j++;
        }
    }
    int minus;
    char * start;
    if(*targetString=='-') {
        minus = -1;start = targetString+1;
    }
    else if(*targetString=='+'){
        minus = 1;start = targetString+1;
    } else{
        minus = 1;start = targetString;
    }
    for(;start<targetString+strlen(targetString);start++){
        if(*start=='('){
            if(colCount>=50){
                printf("Sorry,单个多项式数据对数过多!");
                exit(EXIT_SUCCESS);
            }
            char *end;
            long coefficient = minus*strtol(start+1,&end,10);
            long power = strtol(end+1,&end,10);
            mySets[rowCount][colCount].coefficient = coefficient;
            mySets[rowCount][colCount].power = power;
            mySets[rowCount][colCount].flag = 1;
            start = end;
            colCount++;
        }else if(*start=='}'){
            if(rowCount>=20){
                printf("Sorry,多项式数目过多!");
                exit(EXIT_SUCCESS);
            }
            rowCount++;
            colCount = 0;
            if(*(start+1)=='+')
                minus = 1;
            else if(*(start+1)=='-')
                minus = -1;
            else
                continue;
        }
    }
    //读取实现，验证成功
}
void buildDictTree(int row,int col)
{
    PolyNode target = mySets[row][col];
    TrieNodePtr headCopy = trieHead;
    char str[10] = {'\0'};
    sprintf(str,"%ld",target.power);
    int i;
    for(i=0;str[i]!='\0';i++){
        if(!headCopy->flags[str[i]-'0']){
            headCopy->flags[str[i]-'0'] = 1;
            headCopy->links[str[i]-'0'] = (TrieNodePtr)malloc(sizeof(TrieNode));
            int j;
            for(j=0;j<DICT;j++)
                headCopy->links[str[i]-'0']->flags[j] = 0;
            headCopy->links[str[i]-'0']->exist = 0;
        }
        headCopy = headCopy->links[str[i]-'0'];
    }
    if(headCopy->exist)
        headCopy->current->coefficient += target.coefficient;
    else{
        headCopy->exist = 1;
        sum++;
        headCopy->current = &(mySets[row][col]);
    }
    //字典树构建成功
}
void DFS(TrieNodePtr trieHead){
    int i;
    if(trieHead->exist)
        myNodes[indexDict++] = trieHead->current;
    for(i=0;i<DICT;i++){
        if(trieHead->flags[i])
            DFS(trieHead->links[i]);
    }
}
void quickSort(int low,int high)
{
    if(low < high){
        int pivotKey = getPivot(low,high);
        quickSort(low,pivotKey-1);
        quickSort(pivotKey+1,high);
    }
}
int getPivot(int low,int high)
{
    PolyNodePtr pivotKey = myNodes[low];
    while(low < high){
        while(low < high && myNodes[high]->power >= pivotKey->power)
            high--;
        myNodes[low] = myNodes[high];
        while(low < high && myNodes[low]->power <= pivotKey->power)
            low++;
        myNodes[high] = myNodes[low];
    }
    myNodes[low] = pivotKey;
    return low;
}
