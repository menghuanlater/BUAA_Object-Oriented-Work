#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <ctime>
using namespace std;
int main(void)
{
    FILE *out = fopen("out.txt","w");
    fprintf(out,"(ER,1,0)\n");
    srand((unsigned)time(NULL));
    for(int i=0;i<1000000;i++){
        int temp1 = rand()%2;
        fprintf(out,"(");
        if(temp1){
            fprintf(out,"FR,");
            int temp2 = rand()%2;
            int temp3 = rand()%10 + 1;
            if(temp2){
                fprintf(out,"%d,UP,%d",temp3,i+(rand()%2+1));
            }else{
                fprintf(out,"%d,DOWN,%d",temp3,i+(rand()%2+1));
            }
        }else{
            fprintf(out,"ER,");
            int temp3 = rand()%10 + 1;
            fprintf(out,"%d,%d",temp3,i+(rand()%2+1));
        }
        fprintf(out,")\n");
    }
    fprintf(out,"run");
    fclose(out);
}
