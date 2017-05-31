package test.core;

import core.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/** 
* ALSDispatcher Tester. 
*
*/ 
public class ALSDispatcherTest implements ElevatorConstant{
    private RequestQueue testQueue;
    private ALSDispatcher testALSD;
    private ByteArrayOutputStream bao = new ByteArrayOutputStream(4096);
    private PrintStream cacheStream = new PrintStream(bao);
    private PrintStream oldStream = System.out;
    private String lineSeparator = System.lineSeparator();
@Before
public void before() throws Exception {
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    System.setOut(cacheStream);
} 

@After
public void after() throws Exception {
    System.setOut(oldStream);
    bao.reset();//清空
} 

/** 
* 
* Method: carryOutTheElevator() 
* 
*/ 
@Test
public void testCarryOutTheElevator() throws Exception { 
//TODO: Test goes here...
    //整体化测试-->测试电梯正确行驶信息
    //由于处理机制不同,不合法或者同质请求的输出在Main类中执行,此处检验正常运行流程
    //考虑到简便性,所有beforeRequestTime全部设置为0,请求队列中的请求都是符合时间顺序的
    //*****//
    SingleRequest t1 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,1,0)",0);
    SingleRequest t3 = new SingleRequest("(ER,3,1)",0);
    SingleRequest t4 = new SingleRequest("(FR,2,DOWN,1)",0);
    SingleRequest t5 = new SingleRequest("(ER,5,6)",0);
    SingleRequest t6 = new SingleRequest("(ER,5,6)",0);
    SingleRequest t7 = new SingleRequest("(FR,7,UP,6)",0);
    SingleRequest t8 = new SingleRequest("(FR,7,DOWN,6)",0);
    SingleRequest t9 = new SingleRequest("(FR,7,UP,6)",0);
    SingleRequest t10 = new SingleRequest("(FR,7,DOWN,6)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);testQueue.addRequest(t6);
    testQueue.addRequest(t7);testQueue.addRequest(t8);testQueue.addRequest(t9);
    testQueue.addRequest(t10);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" +lineSeparator+
                    "[ER,1,0] / (1,STILL,2.0)" + lineSeparator +
                    "[ER,3,1] / (3,UP,3.0)" + lineSeparator+
                    "[FR,2,DOWN,1] / (2,DOWN,4.5)" +lineSeparator+
                    "[ER,5,6] / (5,UP,7.5)" + lineSeparator +
                    "[FR,7,UP,6] / (7,UP,9.5)" + lineSeparator+
                    "[FR,7,DOWN,6] / (7,STILL,11.5)" + lineSeparator
    ),true);
    bao.reset();
    //*****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(ER,8,1)",0);
    t3 = new SingleRequest("(FR,4,UP,2)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,4,UP,2] / (4,UP,2.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator
    ),true);
    bao.reset();
    //****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t4 = new SingleRequest("(ER,6,4)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,4,UP,2] / (4,UP,2.5)" + lineSeparator +
                    "[ER,6,4] / (6,UP,4.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,6.5)" + lineSeparator
    ),true);
    bao.reset();
    //****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t4 = new SingleRequest("(ER,5,4)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,4,UP,2] / (4,UP,2.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator +
                    "[ER,5,4] / (5,DOWN,8.0)" + lineSeparator
    ),true);
    bao.reset();
    //****/
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t4 = new SingleRequest("(FR,5,DOWN,3)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,4,UP,2] / (4,UP,2.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator +
                    "[FR,5,DOWN,3] / (5,DOWN,8.0)" + lineSeparator
    ),true);
    bao.reset();
    //****/
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t4 = new SingleRequest("(FR,9,UP,3)",0);
    t5 = new SingleRequest("(ER,10,3)",0);
    t6 = new SingleRequest("(ER,9,3)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);testQueue.addRequest(t6);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,4,UP,2] / (4,UP,2.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator +
                    "[FR,9,UP,3] / (9,UP,7.0)" + lineSeparator +
                    "[ER,9,3] / (9,UP,7.0)" + lineSeparator +
                    "[ER,10,3] / (10,UP,8.5)" + lineSeparator
    ),true);
    bao.reset();
    //****/
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(ER,10,0)",0);
    t3 = new SingleRequest("(ER,4,2)",0);
    t4 = new SingleRequest("(FR,4,DOWN,2)",0);
    t5 = new SingleRequest("(FR,4,UP,3)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
          "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                  "[ER,4,2] / (4,UP,2.5)" + lineSeparator +
                  "[ER,10,0] / (10,UP,6.5)" + lineSeparator +
                  "[FR,4,DOWN,2] / (4,DOWN,10.5)" + lineSeparator +
                  "[FR,4,UP,3] / (4,STILL,12.5)" + lineSeparator
    ),true);
    bao.reset();
    //****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(FR,9,UP,1)",0);
    t3 = new SingleRequest("(FR,9,UP,3)",0);
    t4 = new SingleRequest("(FR,9,UP,6)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,9,UP,1] / (9,UP,5.0)" + lineSeparator
    ),true);
    bao.reset();
    //****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(FR,8,DOWN,0)",0);
    t3 = new SingleRequest("(ER,4,1)",0);
    t4 = new SingleRequest("(FR,8,UP,1)",0);
    t5 = new SingleRequest("(ER,8,1)",0);
    t6 = new SingleRequest("(FR,5,DOWN,6)",0);
    t7 = new SingleRequest("(ER,10,6)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);testQueue.addRequest(t6);
    testQueue.addRequest(t7);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[ER,4,1] / (4,UP,2.5)" + lineSeparator +
                    "[FR,8,UP,1] / (8,UP,5.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator +
                    "[FR,8,DOWN,0] / (8,UP,5.5)" + lineSeparator +
                    "[FR,5,DOWN,6] / (5,DOWN,8.0)" + lineSeparator +
                    "[ER,10,6] / (10,UP,11.5)" + lineSeparator
    ),true);
    bao.reset();
    //****//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(ER,8,1)",0);
    t3 = new SingleRequest("(FR,6,UP,2)",0);
    t4 = new SingleRequest("(FR,4,DOWN,2)",0);
    t5 = new SingleRequest("(ER,5,3)",0);
    t6 = new SingleRequest("(FR,9,UP,3)",0);
    t7 = new SingleRequest("(ER,10,3)",0);
    t8 = new SingleRequest("(ER,9,3)",0);
    t9 = new SingleRequest("(ER,6,4)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);testQueue.addRequest(t6);
    testQueue.addRequest(t7);testQueue.addRequest(t8);testQueue.addRequest(t9);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[FR,6,UP,2] / (6,UP,3.5)" + lineSeparator +
                    "[ER,8,1] / (8,UP,5.5)" + lineSeparator +
                    "[FR,9,UP,3] / (9,UP,7.0)" + lineSeparator +
                    "[ER,9,3] / (9,UP,7.0)" + lineSeparator +
                    "[ER,10,3] / (10,UP,8.5)" + lineSeparator +
                    "[ER,6,4] / (6,DOWN,11.5)" + lineSeparator +
                    "[ER,5,3] / (5,DOWN,13.0)" + lineSeparator +
                    "[FR,4,DOWN,2] / (4,DOWN,14.5)" + lineSeparator
    ),true);
    bao.reset();
    //**强测试点,比较复杂**//
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    t2 = new SingleRequest("(ER,1,0)",0);
    t3 = new SingleRequest("(ER,1,1)",0);
    t4 = new SingleRequest("(ER,1,2)",0);
    t5 = new SingleRequest("(ER,2,3)",0);
    t6 = new SingleRequest("(ER,2,4)",0);
    t7 = new SingleRequest("(ER,2,5)",0);
    t8 = new SingleRequest("(ER,8,100)",0);
    t9 = new SingleRequest("(FR,4,UP,101)",0);
    t10 = new SingleRequest("(FR,5,DOWN,101)",0);
    SingleRequest t11 = new SingleRequest("(FR,5,UP,101)",0);
    SingleRequest t12 = new SingleRequest("(ER,4,101)",0);
    SingleRequest t13 = new SingleRequest("(ER,5,101)",0);
    SingleRequest t14 = new SingleRequest("(ER,9,101)",0);
    SingleRequest t15 = new SingleRequest("(ER,5,102)",0);
    SingleRequest t16 = new SingleRequest("(FR,5,UP,102)",0);
    SingleRequest t17 = new SingleRequest("(FR,4,DOWN,102)",0);
    SingleRequest t18 = new SingleRequest("(FR,9,UP,103)",0);
    SingleRequest t19 = new SingleRequest("(ER,10,103)",0);
    SingleRequest t20 = new SingleRequest("(ER,10,104)",0);
    SingleRequest t21 = new SingleRequest("(ER,8,104)",0);
    SingleRequest t22 = new SingleRequest("(FR,9,UP,105)",0);
    SingleRequest t23 = new SingleRequest("(ER,8,105)",0);
    SingleRequest t24 = new SingleRequest("(FR,9,UP,105)",0);
    SingleRequest t25 = new SingleRequest("(ER,9,106)",0);
    SingleRequest t26 = new SingleRequest("(ER,10,107)",0);
    SingleRequest t27 = new SingleRequest("(ER,10,108)",0);
    SingleRequest t28 = new SingleRequest("(ER,7,109)",0);
    SingleRequest t29 = new SingleRequest("(ER,4,113)",0);
    SingleRequest t30 = new SingleRequest("(FR,4,UP,114)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.addRequest(t4);testQueue.addRequest(t5);testQueue.addRequest(t6);
    testQueue.addRequest(t7);testQueue.addRequest(t8);testQueue.addRequest(t9);
    testQueue.addRequest(t10);testQueue.addRequest(t11);testQueue.addRequest(t12);
    testQueue.addRequest(t13);testQueue.addRequest(t14);testQueue.addRequest(t15);
    testQueue.addRequest(t16);testQueue.addRequest(t17);testQueue.addRequest(t18);
    testQueue.addRequest(t19);testQueue.addRequest(t20);testQueue.addRequest(t21);
    testQueue.addRequest(t22);testQueue.addRequest(t23);testQueue.addRequest(t24);
    testQueue.addRequest(t25);testQueue.addRequest(t26);testQueue.addRequest(t27);
    testQueue.addRequest(t28);testQueue.addRequest(t29);testQueue.addRequest(t30);
    testALSD.carryOutTheElevator();
    Assert.assertEquals(bao.toString().equals(
            "[FR,1,UP,0] / (1,STILL,1.0)" + lineSeparator +
                    "[ER,1,0] / (1,STILL,2.0)" + lineSeparator +
                    "[ER,2,3] / (2,UP,3.5)" + lineSeparator +
                    "[ER,2,5] / (2,STILL,6.0)" + lineSeparator +
                    "[FR,5,UP,101] / (5,UP,101.5)" + lineSeparator +
                    "[ER,5,101] / (5,UP,101.5)" + lineSeparator +
                    "[ER,8,100] / (8,UP,104.0)" + lineSeparator +
                    "[FR,9,UP,103] / (9,UP,105.5)" + lineSeparator +
                    "[ER,9,101] / (9,UP,105.5)" + lineSeparator +
                    "[ER,10,103] / (10,UP,107.0)" + lineSeparator +
                    "[ER,7,109] / (7,DOWN,109.5)" + lineSeparator +
                    "[FR,5,DOWN,101] / (5,DOWN,111.5)" + lineSeparator +
                    "[ER,4,101] / (4,DOWN,113.0)" + lineSeparator +
                    "[FR,4,DOWN,102] / (4,DOWN,113.0)" + lineSeparator +
                    "[FR,4,UP,101] / (4,DOWN,113.0)" + lineSeparator
    ),true);
    bao.reset();
}

/** 
* 
* Method: pickDirectRequest() 
* 
*/ 
@Test
public void testPickDirectRequest() throws Exception { 
//TODO: Test goes here...测试直接可捎带
    Elevator test = testALSD.getMyElevator();
    //电梯状态为STILL
    test.setCompleteRequest(new SingleRequest("(FR,1,UP,0)",0));
    test.setMoveDire();test.setCompleteTime();

    SingleRequest t1 = new SingleRequest("(ER,1,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,2,0)",0);
    testQueue.addRequest(t1);testQueue.addRequest(t2);
    Assert.assertEquals(testALSD.pickDirectRequest(),false);
    Assert.assertEquals(testQueue.isContain(t1),true);
    Assert.assertEquals(testQueue.isContain(t2),true);

    //电梯状态为UP,预设电梯去往7层
    //1.覆盖性测试无可直接捎带
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    SingleRequest a1 = new SingleRequest("(ER,1,0)",0);
    SingleRequest a2 = new SingleRequest("(FR,1,UP,0)",0);
    SingleRequest a3 = new SingleRequest("(FR,2,DOWN,0)",0);
    SingleRequest a4 = new SingleRequest("(ER,2,1)",0);
    SingleRequest a5 = new SingleRequest("(FR,2,UP,1)",1);
    SingleRequest a6 = new SingleRequest("(FR,2,DOWN,1)",1);
    SingleRequest a7 = new SingleRequest("(ER,3,1)",1);
    SingleRequest a8 = new SingleRequest("(FR,3,UP,1)",1);
    SingleRequest a9 = new SingleRequest("(FR,3,DOWN,1)",1);
    testQueue.addRequest(a1);testQueue.addRequest(a2);testQueue.addRequest(a3);
    testQueue.addRequest(a4);testQueue.addRequest(a5);testQueue.addRequest(a6);
    testQueue.addRequest(a7);testQueue.addRequest(a8);testQueue.addRequest(a9);
    Assert.assertEquals(testALSD.pickDirectRequest(),false);
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),true);
    Assert.assertEquals(testQueue.isContain(a4),true);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),true);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);

    //2.存在可直接捎带,且只有一个为ER
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    SingleRequest a10 = new SingleRequest("(ER,5,1)",0);
    testQueue.addRequest(a10);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a10),false);
    Assert.assertEquals(bao.toString().equals("[ER,5,1] / (5,UP,2.0)"+lineSeparator),true);
    bao.reset();
    Assert.assertEquals(test.getStartFloor(),5);
    Assert.assertEquals(test.getMoveDire(),STATUS_UP);
    Assert.assertEquals(Math.abs(test.getStartTime()-3.0)<0.0001,true);
    Assert.assertEquals(Math.abs(test.getCompleteTime()-5.0)<0.0001,true);

    //3.存在可直接捎带,且只有一个为FR
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    SingleRequest a11 = new SingleRequest("(FR,5,UP,1)",0);
    testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a11),false);
    Assert.assertEquals(bao.toString().equals("[FR,5,UP,1] / (5,UP,2.0)"+lineSeparator),true);
    bao.reset();
    Assert.assertEquals(test.getStartFloor(),5);
    Assert.assertEquals(test.getMoveDire(),STATUS_UP);
    Assert.assertEquals(Math.abs(test.getStartTime()-3.0)<0.0001,true);
    Assert.assertEquals(Math.abs(test.getCompleteTime()-5.0)<0.0001,true);
    //4.存在且>1个可直接捎带,只有一个最优
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    SingleRequest a12 = new SingleRequest("(ER,3,0)",0);
    testQueue.addRequest(a12);testQueue.addRequest(a10);testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a12),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    bao.reset();
    //5.存在且>1个可直接捎带,有两个并列的最优
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    testQueue.addRequest(a10);testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(bao.toString().equals("[ER,5,1] / (5,UP,2.0)"+lineSeparator+
            "[FR,5,UP,1] / (5,UP,2.0)"+lineSeparator),true);
    bao.reset();

    //电梯状态为DOWN,预设电梯从10层去往4层
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setCompleteTime();test.setMoveDire();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setCompleteTime();test.setMoveDire();
    //1.覆盖性测试无可捎带
    a1 = new SingleRequest("(ER,10,20)",20);
    a2 = new SingleRequest("(FR,10,DOWN,20)",20);
    a3 = new SingleRequest("(FR,9,UP,20)",20);
    a4 = new SingleRequest("(ER,9,21)",20);
    a5 = new SingleRequest("(FR,9,UP,21)",21);
    a6 = new SingleRequest("(FR,9,UP,21)",21);
    a7 = new SingleRequest("(ER,8,21)",21);
    a8 = new SingleRequest("(FR,8,DOWN,21)",21);
    a9 = new SingleRequest("(FR,8,UP,21)",21);
    testQueue.addRequest(a1);testQueue.addRequest(a2);testQueue.addRequest(a3);
    testQueue.addRequest(a4);testQueue.addRequest(a5);testQueue.addRequest(a6);
    testQueue.addRequest(a7);testQueue.addRequest(a8);testQueue.addRequest(a9);
    Assert.assertEquals(testALSD.pickDirectRequest(),false);
    Assert.assertEquals(testQueue.isContain(a1),true);
    Assert.assertEquals(testQueue.isContain(a2),true);
    Assert.assertEquals(testQueue.isContain(a3),true);
    Assert.assertEquals(testQueue.isContain(a4),true);
    Assert.assertEquals(testQueue.isContain(a5),true);
    Assert.assertEquals(testQueue.isContain(a6),true);
    Assert.assertEquals(testQueue.isContain(a7),true);
    Assert.assertEquals(testQueue.isContain(a8),true);
    Assert.assertEquals(testQueue.isContain(a9),true);
    //2.存在可直接捎带,且只有一个为ER
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setCompleteTime();test.setMoveDire();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setCompleteTime();test.setMoveDire();
    a10 = new SingleRequest("(ER,6,21)",20);
    testQueue.addRequest(a10);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a10),false);
    Assert.assertEquals(bao.toString().equals("[ER,6,21] / (6,DOWN,22.0)"+lineSeparator),true);
    bao.reset();
    Assert.assertEquals(test.getStartFloor(),6);
    Assert.assertEquals(test.getMoveDire(),STATUS_DOWN);
    Assert.assertEquals(Math.abs(test.getStartTime()-23.0)<0.0001,true);
    Assert.assertEquals(Math.abs(test.getCompleteTime()-25.0)<0.0001,true);

    //3.存在可直接捎带,且只有一个为FR
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setCompleteTime();test.setMoveDire();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setCompleteTime();test.setMoveDire();
    a11 = new SingleRequest("(FR,6,DOWN,21)",20);
    testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a11),false);
    Assert.assertEquals(bao.toString().equals("[FR,6,DOWN,21] / (6,DOWN,22.0)"+lineSeparator),true);
    bao.reset();
    Assert.assertEquals(test.getStartFloor(),6);
    Assert.assertEquals(test.getMoveDire(),STATUS_DOWN);
    Assert.assertEquals(Math.abs(test.getStartTime()-23.0)<0.0001,true);
    Assert.assertEquals(Math.abs(test.getCompleteTime()-25.0)<0.0001,true);
    //4.存在且>1个可直接捎带,只有一个最优
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setCompleteTime();test.setMoveDire();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setCompleteTime();test.setMoveDire();
    a12 = new SingleRequest("(ER,8,20)",20);
    testQueue.addRequest(a12);testQueue.addRequest(a10);testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(a12),false);
    Assert.assertEquals(testQueue.isContain(a10),true);
    Assert.assertEquals(testQueue.isContain(a11),true);
    bao.reset();
    //5.存在且>1个可直接捎带,有两个并列的最优
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setCompleteTime();test.setMoveDire();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setCompleteTime();test.setMoveDire();
    testQueue.addRequest(a10);testQueue.addRequest(a11);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(bao.toString().equals("[ER,6,21] / (6,DOWN,22.0)"+lineSeparator+
            "[FR,6,DOWN,21] / (6,DOWN,22.0)"+lineSeparator),true);
    bao.reset();

    //存在可直接捎带,但是与主请求楼层一致,状态重设不应该执行
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,7,0)",0));
    test.setCompleteTime();test.setMoveDire();
    SingleRequest g = new SingleRequest("(FR,7,UP,1)",0);
    testQueue.addRequest(g);
    Assert.assertEquals(testALSD.pickDirectRequest(),true);
    Assert.assertEquals(testQueue.isContain(g),false);
    Assert.assertEquals(test.getMoveDire(),STATUS_UP);
    Assert.assertEquals(test.getStartFloor(),1);
    Assert.assertEquals(Math.abs(test.getStartTime()-0.0)<0.0001,true);
    Assert.assertEquals(Math.abs(test.getCompleteTime()-4.0)<0.0001,true);
}

/** 
* 
* Method: addOrAbandon(List<Pick> tempStack, SingleRequest targetRequest, int indexOfFetch) 
* 
*/ 
@Test
public void testAddOrAbandon() throws Exception { 
//TODO: Test goes here...
    List<Pick> tempStack = new ArrayList<>();
    //1.when empty add ER
    SingleRequest a1 = new SingleRequest("(ER,5,1)",0);
    testALSD.addOrAbandon(tempStack,a1,3);
    Assert.assertEquals(tempStack.size(),1);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),3);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a1);
    //2.when empty add FR
    tempStack.clear();
    SingleRequest a2 = new SingleRequest("(FR,5,UP,1)",0);
    testALSD.addOrAbandon(tempStack,a2,7);
    Assert.assertEquals(tempStack.size(),1);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),7);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a2);
    //3.have ER add ER
    tempStack.clear();
    tempStack.add(new Pick(3,a1));
    SingleRequest a3 = new SingleRequest("(ER,5,2)",1);
    testALSD.addOrAbandon(tempStack,a3,5);
    Assert.assertEquals(tempStack.size(),1);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),3);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a1);
    //4.have FR add FR
    tempStack.clear();
    tempStack.add(new Pick(7,a2));
    SingleRequest a4 = new SingleRequest("(FR,5,UP,2)",1);
    testALSD.addOrAbandon(tempStack,a4,4);
    Assert.assertEquals(tempStack.size(),1);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),7);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a2);
    //5.have ER add FR
    tempStack.clear();
    tempStack.add(new Pick(3,a1));
    testALSD.addOrAbandon(tempStack,a2,7);
    Assert.assertEquals(tempStack.size(),2);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),3);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a1);
    Assert.assertEquals(tempStack.get(1).getIndexOfFetch(),7);
    Assert.assertEquals(tempStack.get(1).getPickAbleRequest(),a2);
    //6.have FR add ER
    tempStack.clear();
    tempStack.add(new Pick(7,a2));
    testALSD.addOrAbandon(tempStack,a1,3);
    Assert.assertEquals(tempStack.size(),2);
    Assert.assertEquals(tempStack.get(1).getIndexOfFetch(),3);
    Assert.assertEquals(tempStack.get(1).getPickAbleRequest(),a1);
    Assert.assertEquals(tempStack.get(0).getIndexOfFetch(),7);
    Assert.assertEquals(tempStack.get(0).getPickAbleRequest(),a2);

} 

/** 
* 
* Method: findPickedPower() 
* 
*/ 
@Test
public void testFindPickedPower() throws Exception { 
//TODO: Test goes here...
    //1.objRequest == null
    Elevator test = testALSD.getMyElevator();
    Assert.assertEquals(testALSD.findPickedPower(),false);
    //2.moveDire == STATUS_STILL
    test.setCompleteRequest(new SingleRequest("(FR,1,UP,0)",0));
    test.setMoveDire();test.setCompleteTime();
    Assert.assertEquals(testALSD.findPickedPower(),false);
    //3.no pickPower when UP
    test.setCompleteRequest(new SingleRequest("(ER,7,20)",20));
    test.setMoveDire();test.setCompleteTime();
    SingleRequest g  = new SingleRequest("(ER,7,20)",20);
    SingleRequest t1 = new SingleRequest("(ER,6,20)",20);
    SingleRequest t2 = new SingleRequest("(FR,8,UP,20)",20);
    SingleRequest t3 = new SingleRequest("(ER,8,23)",20);
    testQueue.addRequest(g);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.getRequestNext();//-->let indexOfFetch++
    Assert.assertEquals(testALSD.findPickedPower(),false);
    Assert.assertEquals(testQueue.getRequestAt(0),g);
    Assert.assertEquals(testQueue.getRequestAt(1),t1);
    Assert.assertEquals(testQueue.getRequestAt(2),t2);
    Assert.assertEquals(testQueue.getRequestAt(3),t3);
    //4.have pickPower when UP
    SingleRequest t4 = new SingleRequest("(ER,8,20)",20);
    testQueue.delRequestAt(3);
    testQueue.addRequest(t4);testQueue.addRequest(t3);
    Assert.assertEquals(testALSD.findPickedPower(),true);
    Assert.assertEquals(testQueue.getSizeOfQueue(),4);
    Assert.assertEquals(testQueue.getRequestAt(0),t4);
    Assert.assertEquals(testQueue.getRequestAt(1),t1);
    Assert.assertEquals(testQueue.getRequestAt(2),t2);
    Assert.assertEquals(testQueue.getRequestAt(3),t3);
    //5.no pickPower when DOWN
    testQueue = new RequestQueue();
    testALSD = new ALSDispatcher(testQueue);
    test = testALSD.getMyElevator();
    test.setCompleteRequest(new SingleRequest("(ER,10,0)",0));
    test.setMoveDire();test.setCompleteTime();
    test.setCompleteRequest(new SingleRequest("(ER,4,20)",0));
    test.setMoveDire();test.setCompleteTime();
    g = new SingleRequest("(ER,4,20)",20);
    t1 = new SingleRequest("(ER,5,20)",20);
    t2 = new SingleRequest("(FR,3,DOWN,20)",20);
    t3 = new SingleRequest("(ER,3,23)",20);
    testQueue.addRequest(g);
    testQueue.addRequest(t1);testQueue.addRequest(t2);testQueue.addRequest(t3);
    testQueue.getRequestNext();//-->let indexOfFetch++
    Assert.assertEquals(testALSD.findPickedPower(),false);
    Assert.assertEquals(testQueue.getRequestAt(0),g);
    Assert.assertEquals(testQueue.getRequestAt(1),t1);
    Assert.assertEquals(testQueue.getRequestAt(2),t2);
    Assert.assertEquals(testQueue.getRequestAt(3),t3);
    //6.have pickPower when DOWN
    t4 = new SingleRequest("(ER,3,20)",20);
    testQueue.delRequestAt(3);
    testQueue.addRequest(t4);testQueue.addRequest(t3);
    Assert.assertEquals(testALSD.findPickedPower(),true);
    Assert.assertEquals(testQueue.getSizeOfQueue(),4);
    Assert.assertEquals(testQueue.getRequestAt(0),t4);
    Assert.assertEquals(testQueue.getRequestAt(1),t1);
    Assert.assertEquals(testQueue.getRequestAt(2),t2);
    Assert.assertEquals(testQueue.getRequestAt(3),t3);
} 


} 
