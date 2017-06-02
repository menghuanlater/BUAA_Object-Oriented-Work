package test.core; 

import core.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** 
* Dispatcher Tester. 
*
* @version 1.0 
*/ 
public class DispatcherTest implements ElevatorConstant{

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
}
/**
 *
 * Method: getMyElevator()
 *
 */
@Test
public void testGetMyElevator() throws Exception {
//TODO: Test goes here...
    RequestQueue x = new RequestQueue();
    Dispatcher dispatcher = new Dispatcher(x);
    Elevator t = dispatcher.getMyElevator();
    Assert.assertEquals(Math.abs(t.getCompleteTime()-0.0)<0.0001,true);
    Assert.assertEquals(Math.abs(t.getStartTime()-0.0)<0.0001,true);
    Assert.assertEquals(t.getStartFloor(),1);
    Assert.assertEquals(t.getMoveDire(),STATUS_STILL);
    Assert.assertEquals(t.getCompleteRequest(),null);
}

/** 
* 
* Method: checkSameRequest(int loopStart, double completeTime, SingleRequest objRequest) 
* 
*/ 
@Test
public void testCheckSameRequest() throws Exception { 
//TODO: Test goes here...
    RequestQueue testQueue = new RequestQueue();
    //加入一系列合法的请求(整体全覆盖性测试)
    SingleRequest a1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest a15 = new SingleRequest("(ER,5,1)",0);
    SingleRequest a16 = new SingleRequest("(FR,5,UP,1)",1);
    SingleRequest a2 = new SingleRequest("(ER,8,1)",0);
    SingleRequest a3 = new SingleRequest("(ER,8,1)",1);
    SingleRequest a4 = new SingleRequest("(ER,8,2)",1);
    SingleRequest a5 = new SingleRequest("(FR,9,UP,3)",2);
    SingleRequest a6 = new SingleRequest("(FR,9,DOWN,3)",3);
    SingleRequest a7 = new SingleRequest("(ER,8,3)",3);
    SingleRequest a8 = new SingleRequest("(FR,9,UP,7)",3);
    SingleRequest a9 = new SingleRequest("(FR,9,DOWN,8)",7);
    SingleRequest a10 = new SingleRequest("(ER,8,30)",8);
    SingleRequest a11 = new SingleRequest("(FR,9,UP,30)",30);
    SingleRequest a12 = new SingleRequest("(FR,9,DOWN,30)",30);
    SingleRequest a13 = new SingleRequest("(FR,9,UP,31)",30);
    SingleRequest a14 = new SingleRequest("(FR,9,DOWN,31)",31);
    //应当被清除的请求:a3,a4,a7,a8,a9,a13,a14
    testQueue.addRequest(a1);
    testQueue.addRequest(a15);
    testQueue.addRequest(a16);
    testQueue.addRequest(a2);
    testQueue.addRequest(a3);
    testQueue.addRequest(a4);
    testQueue.addRequest(a5);
    testQueue.addRequest(a6);
    testQueue.addRequest(a7);
    testQueue.addRequest(a8);
    testQueue.addRequest(a9);
    testQueue.addRequest(a10);
    testQueue.addRequest(a11);
    testQueue.addRequest(a12);
    testQueue.addRequest(a13);
    testQueue.addRequest(a14);

    Dispatcher dispatcher = new Dispatcher(testQueue);
    dispatcher.checkSameRequest(0,1,null);
    dispatcher.checkSameRequest(1,1,a1);//不应该删除任何一个请求
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),true);
    Assert.assertEquals(testQueue.isContain(a4),true);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),true);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(2,4,a15);//不应该删除任何一个请求
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),true);
    Assert.assertEquals(testQueue.isContain(a4),true);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),true);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(3,4,a16);//不应该删除任何一个请求
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),true);
    Assert.assertEquals(testQueue.isContain(a4),true);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),true);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(4,6.5,a2);
    //a3,a4,a7被删除
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(5,8.0,a5);
    //a8被删
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),false);
    Assert.assertEquals(testQueue.isContain(a9),true);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(6,9,a6);
    //a9被删
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),false);
    Assert.assertEquals(testQueue.isContain(a9),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(7,31.5,a10);
    //没有被删
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),false);
    Assert.assertEquals(testQueue.isContain(a9),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),true);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(8,33.0,a11);
    //a13被删
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),false);
    Assert.assertEquals(testQueue.isContain(a9),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),false);
    Assert.assertEquals(testQueue.isContain(a14),true);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    dispatcher.checkSameRequest(9,34.0,a12);
    //a14被删
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),false);
    Assert.assertEquals(testQueue.isContain(a4),false);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),false);
    Assert.assertEquals(testQueue.isContain(a8),false);
    Assert.assertEquals(testQueue.isContain(a9),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    Assert.assertEquals(testQueue.isContain(a12),true);
    Assert.assertEquals(testQueue.isContain(a13),false);
    Assert.assertEquals(testQueue.isContain(a14),false);
    Assert.assertEquals(testQueue.isContain(a15),true);
    Assert.assertEquals(testQueue.isContain(a16),true);

    Assert.assertEquals(testQueue.getSizeOfQueue(),9);
} 


} 
