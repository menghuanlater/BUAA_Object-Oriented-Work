package test.core; 

import core.NoNextRequestException;
import core.RequestQueue;
import core.SingleRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** 
* RequestQueue Tester. 
*
*/ 
public class RequestQueueTest {
    private RequestQueue testQueue;//测试
@Before
public void before() throws Exception {
    testQueue = new RequestQueue();
} 

@After
public void after() throws Exception {

} 

/** 
* 
* Method: addRequest(SingleRequest request) 
* 
*/ 
@Test
public void testAddRequest() throws Exception { 
//TODO: Test goes here...
    //1.add null
    Assert.assertEquals(testQueue.addRequest(null),false);
    //2.add request.legacy == false
    SingleRequest temp1 = new SingleRequest("(ER,12,9)",0);
    Assert.assertEquals(temp1.isLegalRequest(),false);//we must prove it's illegal request
    Assert.assertEquals(testQueue.addRequest(temp1),false);
    //3.add a normal request
    SingleRequest temp2 = new SingleRequest("(FR,1,UP,0)",0);
    Assert.assertEquals(testQueue.addRequest(temp2),true);
    Assert.assertEquals(testQueue.isContain(temp2),true);//判断是否真的add进去
    //4.add same request
    Assert.assertEquals(testQueue.addRequest(temp2),false);
    Assert.assertEquals(testQueue.getSizeOfQueue(),1);
} 

/** 
* 
* Method: getRequestNext() 
* 
*/ 
@Test
public void testGetRequestNext() throws Exception { 
//TODO: Test goes here...
    //1.when haveNext is false
    try{
        testQueue.getRequestNext();//触发异常
        Assert.assertEquals(false,true);//如果没有触发异常,检测未通过
    }catch (NoNextRequestException e){
        Assert.assertEquals(true,true);//触发异常,检测通过
    }
    //2.正常检测
    try {
        SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
        SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
        testQueue.addRequest(t1);testQueue.addRequest(t2);
        int index1 = testQueue.getIndexOfFetch();
        SingleRequest g1 = testQueue.getRequestNext();
        Assert.assertEquals(g1,t1);
        int index2 = testQueue.getIndexOfFetch();
        Assert.assertEquals(index1+1,index2);
        SingleRequest g2 = testQueue.getRequestNext();
        Assert.assertEquals(g2,t2);
        int index3 = testQueue.getIndexOfFetch();
        Assert.assertEquals(index2+1,index3);
    }catch (NoNextRequestException e){
        Assert.assertEquals(false,true);//触发异常,检测不通过
    }
} 

/** 
* 
* Method: haveNext() 
* 
*/ 
@Test
public void testHaveNext() throws Exception { 
//TODO: Test goes here...
    //1.when queue is empty
    Assert.assertEquals(testQueue.haveNext(),false);
    //2.when queue is not empty and index<size
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    Assert.assertEquals(testQueue.haveNext(),true);
    testQueue.getRequestNext();
    Assert.assertEquals(testQueue.haveNext(),true);
    testQueue.getRequestNext();
    //3.when queue is not empty and index>=size
    Assert.assertEquals(testQueue.haveNext(),false);
} 

/** 
* 
* Method: getIndexOfFetch() 
* 
*/ 
@Test
public void testGetIndexOfFetch() throws Exception { 
//TODO: Test goes here...
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    int index1 = testQueue.getIndexOfFetch();
    testQueue.getRequestNext();
    int index2 = testQueue.getIndexOfFetch();
    testQueue.getRequestNext();
    int index3 = testQueue.getIndexOfFetch();
    Assert.assertEquals(index1+1,index2);
    Assert.assertEquals(index2+1,index3);
} 

/** 
* 
* Method: getSizeOfQueue() 
* 
*/ 
@Test
public void testGetSizeOfQueue() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(testQueue.getSizeOfQueue(),0);
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    testQueue.addRequest(t1);
    Assert.assertEquals(testQueue.getSizeOfQueue(),1);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t2);
    Assert.assertEquals(testQueue.getSizeOfQueue(),2);
    SingleRequest t3 = new SingleRequest("(FR,1,DOWN,2)",1);//illegal request
    testQueue.addRequest(t3);
    Assert.assertEquals(testQueue.getSizeOfQueue(),2);
} 

/** 
* 
* Method: getRequestAt(int position) 
* 
*/ 
@Test
public void testGetRequestAt() throws Exception { 
//TODO: Test goes here...
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    //1.position is minus
    try {
        testQueue.getRequestAt(-9);//触发异常
        Assert.assertEquals(false,true);
    }catch (NoNextRequestException e){
        Assert.assertEquals(true,true);
    }
    //2.position==size
    try {
        testQueue.getRequestAt(2);//触发异常
        Assert.assertEquals(false,true);
    }catch (NoNextRequestException e){
        Assert.assertEquals(true,true);
    }
    //3.position>size
    try {
        testQueue.getRequestAt(6);//触发异常
        Assert.assertEquals(false,true);
    }catch (NoNextRequestException e){
        Assert.assertEquals(true,true);
    }
    //4.正常测试
    try {
        SingleRequest g1 = testQueue.getRequestAt(0);
        SingleRequest g2 = testQueue.getRequestAt(1);
        Assert.assertEquals(g1,t1);
        Assert.assertEquals(g2,t2);
    }catch (NoNextRequestException e){
        Assert.assertEquals(false,true);
    }
} 

/** 
* 
* Method: delRequestAt(int position) 
* 
*/ 
@Test
public void testDelRequestAt() throws Exception { 
//TODO: Test goes here...
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    //1.position<0
    Assert.assertEquals(testQueue.delRequestAt(-9),false);
    //2.position==size
    Assert.assertEquals(testQueue.delRequestAt(2),false);
    //3.position>size
    Assert.assertEquals(testQueue.delRequestAt(9),false);
    //4.正常测试
    Assert.assertEquals(testQueue.delRequestAt(1),true);
    Assert.assertEquals(testQueue.getSizeOfQueue(),1);//size -1
    Assert.assertEquals(testQueue.isContain(t2),false);
    Assert.assertEquals(testQueue.delRequestAt(0),true);
    Assert.assertEquals(testQueue.getSizeOfQueue(),0);
    Assert.assertEquals(testQueue.isContain(t1),false);
} 

/** 
* 
* Method: isContain(SingleRequest request) 
* 
*/ 
@Test
public void testIsContain() throws Exception { 
//TODO: Test goes here...
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    //1.request == null
    Assert.assertEquals(testQueue.isContain(null),false);
    //2.request.legacy == false
    Assert.assertEquals(testQueue.isContain(new SingleRequest("[cd,cd]",0)),false);
    //3.request is in
    Assert.assertEquals(testQueue.isContain(t1),true);
    //4.request is not in
    Assert.assertEquals(testQueue.isContain(new SingleRequest("(ER,7,3)",0)), false);
} 

/** 
* 
* Method: subIndexOfFetch() 
* 
*/ 
@Test
public void testSubIndexOfFetch() throws Exception { 
//TODO: Test goes here...
    //1.when indexOfFetch==0
    Assert.assertEquals(testQueue.subIndexOfFetch(),false);
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    testQueue.addRequest(t1);
    testQueue.getRequestNext();
    Assert.assertEquals(testQueue.getIndexOfFetch(),1);
    //2.正常测试
    Assert.assertEquals(testQueue.subIndexOfFetch(),true);
    Assert.assertEquals(testQueue.getIndexOfFetch(),0);
} 

/** 
* 
* Method: setRequestAt(int position, SingleRequest request) 
* 
*/ 
@Test
public void testSetRequestAt() throws Exception { 
//TODO: Test goes here...
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,1)",0);
    SingleRequest t3 = new SingleRequest("(ER,10,2)",1);
    SingleRequest t4 = new SingleRequest("(FR,8,DOWN,6)",9);//非法请求
    //1.position<0
    testQueue.addRequest(t1);
    Assert.assertEquals(testQueue.getSizeOfQueue(),1);
    Assert.assertEquals(testQueue.setRequestAt(-7,t3),false);
    //2.position==size
    Assert.assertEquals(testQueue.setRequestAt(1,t3),false);
    //3.position>size
    Assert.assertEquals(testQueue.setRequestAt(2,t3),false);
    testQueue.addRequest(t2);
    Assert.assertEquals(testQueue.getSizeOfQueue(),2);
    //4.request == null
    Assert.assertEquals(testQueue.setRequestAt(0,null),false);
    //5.request.legacy == false
    Assert.assertEquals(t4.isLegalRequest(),false);
    Assert.assertEquals(testQueue.setRequestAt(1,t4),false);
    //6.正常测试
    Assert.assertEquals(testQueue.setRequestAt(1,t3),true);
    Assert.assertEquals(testQueue.getRequestAt(1),t3);
    Assert.assertEquals(testQueue.isContain(t2),false);
} 


} 
