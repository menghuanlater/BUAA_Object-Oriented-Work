#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <ctime>
#define MOD 100000
using namespace std;
int main(void)
{
    FILE *out = fopen("out.txt","w");
    srand((unsigned)time(NULL));
    for(int i=0;i<20;i++){
        if(rand()%2)
            fprintf(out,"%c{",'+');
        else
            fprintf(out,"%c{",'-');
        for(int j=0;j<50;j++){
            if(j!=0)
                fprintf(out,"%c",',');
            int minus = (rand()%2)?(-1):(1);
            int coefficient = minus*rand()%MOD;
            int power = rand()%MOD;
            fprintf(out,"(%d , %d)",coefficient,power);
        }
        fprintf(out,"%c",'}');
    }
    fclose(out);
}