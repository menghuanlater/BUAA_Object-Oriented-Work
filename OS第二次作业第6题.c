#include <stdio.h>
#include <stdlib.h>
#define INFINITY 65535
typedef struct Node
{
	char type;
	int processID; //ID>0 If ID==0-->type=='H'
	int start;
	int length;
	struct Node * link; //link to next node
}MemoryListNode,*MemoryListNodePtr;
const int SUM = 8; //题目中链表长度

void initialList(MemoryListNodePtr myList) 
{
	char TYPE[SUM] = {'P','H','P','P','H','P','P','H'};
	int START[SUM] = {0,5,8,14,18,20,26,29};
	int LENGTH[SUM] = {5,3,6,4,2,6,3,3};
	int ID[SUM] = {21323,0,1289,9340,0,6675,3345,0};
	int i;
	MemoryListNodePtr ptr = myList;
	for (i = 0; i < SUM; i++) {
		MemoryListNodePtr temp = (MemoryListNodePtr)malloc(sizeof(MemoryListNode));
		temp->type = TYPE[i];
		temp->processID = ID[i];
		temp->start = START[i];
		temp->length = LENGTH[i];
		temp->link = NULL;
		ptr->link = temp;
		ptr = temp;
	}
};
void bestFit(MemoryListNodePtr myList,int ID, int size)
{
	MemoryListNodePtr Ptr = myList, bestPtr=NULL;
	int min = INFINITY;
	while (Ptr) {
		if (Ptr->type == 'H' && Ptr->length >= size) {
			if (Ptr->length == size) {
				Ptr->type = 'P';
				Ptr->processID = ID;
				printf("Allocate Process %d is Success! Remain:%d \n", ID,0);
				return;
			}
			else if (Ptr->length - size < min) {
				min = Ptr->length - size;
				bestPtr = Ptr;
			}
		}
		Ptr = Ptr->link;
	}
	if (min == INFINITY)
		printf("Allocate Process %d is Failed!\n", ID);
	else {
		MemoryListNodePtr temp = (MemoryListNodePtr)malloc(sizeof(MemoryListNode));
		temp->type = 'P'; 
		temp->processID = ID;
		temp->start = bestPtr->start + size;
		temp->length = size;
		bestPtr->length -= size;
		Ptr = bestPtr->link;
		bestPtr->link = temp;
		if (Ptr == NULL) {
			temp->link = NULL;
		}
		else {
			temp->link = Ptr;
		}
		printf("Allocate Process %d is Success! Remain:%d \n", ID,bestPtr->length);
	}
}

void worstFit(MemoryListNodePtr myList,int ID, int size)
{
	MemoryListNodePtr Ptr = myList, worstPtr=NULL;
	int max = -INFINITY;
	while (Ptr) {
		if (Ptr->type == 'H' && Ptr->length >= size) {
			if (Ptr->length - size > max) {
				max = Ptr->length - size;
				worstPtr = Ptr;
			}
		}
		Ptr = Ptr->link;
	}
	if (max == -INFINITY)
		printf("Allocate Process %d is Failed!\n", ID);
	else if(worstPtr->length==size){
		Ptr->type = 'P';
		Ptr->processID = ID;
		printf("Allocate Process %d is Success! Remain:%d \n", ID,0);
	}
	else {
		MemoryListNodePtr temp = (MemoryListNodePtr)malloc(sizeof(MemoryListNode));
		temp->type = 'P'; 
		temp->processID = ID;
		temp->start = worstPtr->start + size;
		temp->length = size;
		worstPtr->length -= size;
		Ptr = worstPtr->link;
		worstPtr->link = temp;
		if (Ptr == NULL) {
			temp->link = NULL;
		}
		else {
			temp->link = Ptr;
		}
		printf("Allocate Process %d is Success! Remain:%d \n", ID,worstPtr->length);
	}
}

void freeID(MemoryListNodePtr myList,int ID) 
{
	MemoryListNodePtr Ptr = myList;
	MemoryListNodePtr before = NULL;
	int flag = 0;
	while (Ptr) {
		if (Ptr->processID == ID) {
			flag = 1;
			Ptr->type = 'H';
			Ptr->processID = 0;
			if(Ptr->link && Ptr->link->type=='H'){
				Ptr->length += Ptr->link->length;
				MemoryListNodePtr temp = Ptr->link;
				Ptr->link = temp->link;
				free(temp);
			}
			if(before && before->type=='H'){
				before->length += Ptr->length;
				before->link = Ptr->link;
				free(Ptr);
			}
			break;
		}
		before = Ptr;
		Ptr = Ptr->link;
	}
	if(flag)
		printf("Process %d Free Success!\n",ID);
	else
		printf("Process %d Not Exist!\n",ID);
}

int main(void)
{
	MemoryListNodePtr myList = (MemoryListNodePtr)malloc(sizeof(MemoryListNode));
	//首先按照题目中所给的单向链表，初始化链表
	initialList(myList);
	//首先采用Best Fit算法分配(分配进程ID：66123，需要大小：1),之后free ID：1289
	bestFit(myList,66123,1);
	freeID(myList,1289);
	//采用Worst Fit算法分配(分配进程ID：81283，需要大小：2)
	worstFit(myList,81283,2);
	//free the whole list
	while(myList){
		MemoryListNodePtr temp = myList->link;
		free(myList);
		myList = temp;
	}
	return 0;
}
