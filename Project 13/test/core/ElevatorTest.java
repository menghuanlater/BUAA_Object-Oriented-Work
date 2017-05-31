package test.core; 

import core.Elevator;
import core.ElevatorConstant;
import core.SingleRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** 
* Elevator Tester. 
*
*/ 
public class ElevatorTest implements ElevatorConstant{
    private Elevator testElev;
    private ByteArrayOutputStream bao = new ByteArrayOutputStream(4096);
    private PrintStream cacheStream = new PrintStream(bao);
    private PrintStream oldStream = System.out;
    private String lineSeparator = System.lineSeparator();

@Before
public void before() throws Exception {
    testElev = new Elevator();
    System.setOut(cacheStream);
} 

@After
public void after() throws Exception {
    System.setOut(oldStream);
    bao.reset();//清空
} 

/** 
* 
* Method: setCompleteRequest(SingleRequest completeRequest) 
* 
*/ 
@Test
public void testSetCompleteRequest() throws Exception { 
//TODO: Test goes here...
    SingleRequest test = new SingleRequest("(FR,1,UP,0)",0);
    Assert.assertEquals(test.isLegalRequest(),true);
    testElev.setCompleteRequest(test);
    Assert.assertEquals(testElev.getCompleteRequest(),test);
    //now we test whether startFloor is 1.
    Assert.assertEquals(testElev.getStartFloor(),1);
    //reset completeRequest
    int testFloor = test.getTargetFloor();
    test = new SingleRequest("(ER,10,9)",4);
    Assert.assertEquals(test.isLegalRequest(),true);
    testElev.setCompleteRequest(test);
    Assert.assertEquals(testElev.getCompleteRequest(),test);
    Assert.assertEquals(testElev.getStartFloor(),testFloor);
} 

/** 
* 
* Method: setMoveDire() 
* 
*/ 
@Test
public void testSetMoveDire() throws Exception { 
//TODO: Test goes here...
    SingleRequest t = new SingleRequest("(FR,3,UP,0)",0);//预设
    SingleRequest t1 = new SingleRequest("(ER,8,6)",3);//测试UP
    SingleRequest t2 = new SingleRequest("(FR,4,DOWN,8)",6);//测试DOWN
    SingleRequest t3 = new SingleRequest("(FR,4,DOWN,20)",10);//测试STILL

    Assert.assertEquals(t.isLegalRequest(),true);
    Assert.assertEquals(t1.isLegalRequest(),true);
    Assert.assertEquals(t2.isLegalRequest(),true);
    Assert.assertEquals(t3.isLegalRequest(),true);

    Assert.assertEquals(testElev.getMoveDire(),STATUS_STILL);
    testElev.setCompleteRequest(t);
    testElev.setMoveDire();
    Assert.assertEquals(testElev.getMoveDire(),STATUS_UP);

    testElev.setCompleteRequest(t1);
    testElev.setMoveDire();
    Assert.assertEquals(testElev.getMoveDire(),STATUS_UP);

    testElev.setCompleteRequest(t2);
    testElev.setMoveDire();
    Assert.assertEquals(testElev.getMoveDire(),STATUS_DOWN);

    testElev.setCompleteRequest(t3);
    testElev.setMoveDire();
    Assert.assertEquals(testElev.getMoveDire(),STATUS_STILL);
} 

/** 
* 
* Method: setCompleteTime() 
* 
*/ 
@Test
public void testSetCompleteTime() throws Exception { 
//TODO: Test goes here...
    SingleRequest t = new SingleRequest("(FR,3,UP,2)",0);
    SingleRequest g = new SingleRequest("(ER,5,3)",2);
    Assert.assertEquals(t.isLegalRequest(),true);
    Assert.assertEquals(g.isLegalRequest(),true);

    Assert.assertEquals(Math.abs(testElev.getStartTime()-0.0)<0.0001,true);

    testElev.setCompleteRequest(t);
    testElev.setCompleteTime();
    //1.startTime < completeRequest.requestTime
    Assert.assertEquals(Math.abs(testElev.getCompleteTime()-4.0)<0.0001,true);
    Assert.assertEquals(Math.abs(testElev.getStartTime()-2.0)<0.0001,true);

    testElev.setCompleteRequest(g);
    testElev.setCompleteTime();
    //2.startTime >= completeRequestTime
    Assert.assertEquals(Math.abs(testElev.getCompleteTime()-6.0)<0.0001,true);
    Assert.assertEquals(Math.abs(testElev.getStartTime()-4.0)<0.0001,true);
} 

/** 
* 
* Method: outPut() 
* 
*/ 
@Test
public void testOutPut() throws Exception { 
//TODO: Test goes here...
    SingleRequest t = new SingleRequest("(FR,3,UP,0)",0);//预设
    SingleRequest t1 = new SingleRequest("(ER,8,6)",3);//测试UP
    SingleRequest t2 = new SingleRequest("(FR,4,DOWN,8)",6);//测试DOWN
    SingleRequest t3 = new SingleRequest("(FR,4,DOWN,20)",10);//测试STILL

    Assert.assertEquals(t.isLegalRequest(),true);
    Assert.assertEquals(t1.isLegalRequest(),true);
    Assert.assertEquals(t2.isLegalRequest(),true);
    Assert.assertEquals(t3.isLegalRequest(),true);

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals(""),true);
    bao.reset();
    testElev.setCompleteRequest(t);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[FR,3,UP,0] / (3,UP,1.0)"+lineSeparator),true);
    bao.reset();
    testElev.setCompleteRequest(t1);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[ER,8,6] / (8,UP,8.5)"+lineSeparator),true);
    bao.reset();
    testElev.setCompleteRequest(t2);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[FR,4,DOWN,8] / (4,DOWN,11.5)"+lineSeparator),true);
    bao.reset();
    testElev.setCompleteRequest(t3);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[FR,4,DOWN,20] / (4,STILL,21.0)"+lineSeparator),true);
    bao.reset();
} 

/** 
* 
* Method: getCompleteTime() 
* 
*/ 
@Test
public void testGetCompleteTime() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(Math.abs(testElev.getCompleteTime()-0.0)<0.0001,true);
    SingleRequest t = new SingleRequest("(FR,3,UP,2)",0);
    Assert.assertEquals(t.isLegalRequest(),true);
    testElev.setCompleteRequest(t);
    testElev.setCompleteTime();
    Assert.assertEquals(Math.abs(testElev.getCompleteTime()-4.0)<0.0001,true);
} 

/** 
* 
* Method: getCompleteRequest() 
* 
*/ 
@Test
public void testGetCompleteRequest() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(testElev.getCompleteRequest(),null);
    SingleRequest t = new SingleRequest("(FR,3,UP,2)",0);
    testElev.setCompleteRequest(t);
    Assert.assertEquals(testElev.getCompleteRequest(),t);
} 

/** 
* 
* Method: getMoveDire() 
* 
*/ 
@Test
public void testGetMoveDire() throws Exception { 
//TODO: Test goes here...
    //get MoveDire 的测试可以直接使用testSetMoveDire方法
    Assert.assertEquals(testElev.getMoveDire(),STATUS_STILL);
    testSetMoveDire();//使用set的测试,set已经包含对get的测试
} 

/** 
* 
* Method: getArriveTime(int targetFloor) 
* 
*/ 
@Test
public void testGetArriveTime() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(Math.abs(testElev.getArriveTime(1)-0.0)<0.0001,true);
    Assert.assertEquals(Math.abs(testElev.getArriveTime(6)-2.5)<0.0001,true);
    Assert.assertEquals(Math.abs(testElev.getArriveTime(10)-4.5)<0.0001,true);
} 

/** 
* 
* Method: isAblePick(int requestTime, int targetFloor, boolean mode) 
* 
*/ 
@Test
public void testIsAblePick() throws Exception { 
//TODO: Test goes here...
    //1.测试mode == true
    //预先将电梯开往三层,从5s开始测试,主请求是7楼向上的楼层请求
    SingleRequest t = new SingleRequest("(FR,3,UP,0)",0);
    Assert.assertEquals(t.isLegalRequest(),true);
    SingleRequest g = new SingleRequest("(FR,7,UP,5)",0);
    Assert.assertEquals(g.isLegalRequest(),true);
    testElev.setCompleteRequest(t);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    //完成预设,设置主请求
    testElev.setCompleteRequest(g);
    testElev.setMoveDire();
    testElev.setCompleteTime();
    //1.1 targetFloor <= startFloor &&  getArriveTime(targetFloor) <= requestTime
    Assert.assertEquals(testElev.isAblePick(6,2,true),false);
    Assert.assertEquals(testElev.isAblePick(5,3,true),false);
    Assert.assertEquals(testElev.isAblePick(6,1,true),false);
    Assert.assertEquals(testElev.isAblePick(6,3,true),false);
    //1.2 targetFloor <= startFloor &&  getArriveTime(targetFloor) > requestTime
    Assert.assertEquals(testElev.isAblePick(5,2,true),false);
    Assert.assertEquals(testElev.isAblePick(4,3,true),false);
    //1.3 targetFloor >  startFloor &&  getArriveTime(targetFloor) <= requestTime
    Assert.assertEquals(testElev.isAblePick(6,5,true),false);
    Assert.assertEquals(testElev.isAblePick(6,4,true),false);
    //1.4 targetFloor >  startFloor &&  getArriveTime(targetFloor) > requestTime
    Assert.assertEquals(testElev.isAblePick(5,5,true),true);

    //2.测试mode == false
    //8s时电梯开关门完毕停止在7楼,从9s开始测试,设置主请求为下行到2层的电梯请求
    SingleRequest v = new SingleRequest("(ER,2,9)",0);
    Assert.assertEquals(v.isLegalRequest(),true);
    //完成预设,设置主请求
    testElev.setCompleteRequest(v);
    testElev.setMoveDire();
    testElev.setCompleteTime();
    //2.1 targetFloor >= startFloor && getArriveTime(targetFloor) <= requestTime
    Assert.assertEquals(testElev.isAblePick(9,7,false),false);
    Assert.assertEquals(testElev.isAblePick(10,7,false),false);
    Assert.assertEquals(testElev.isAblePick(10,9,false),false);
    Assert.assertEquals(testElev.isAblePick(10,8,false),false);
    //2.2 targetFloor >= startFloor && getArriveTime(targetFloor) > requestTime
    Assert.assertEquals(testElev.isAblePick(8,7,false),false);
    Assert.assertEquals(testElev.isAblePick(9,9,false),false);
    //2.3 targetFloor < startFloor && getArriveTime(targetFloor) <= requestTime
    Assert.assertEquals(testElev.isAblePick(10,6,false),false);
    Assert.assertEquals(testElev.isAblePick(10,5,false),false);
    //2.4 targetFloor < startFloor && getArriveTime(targetFloor) < requestTime
    Assert.assertEquals(testElev.isAblePick(9,5,false),true);
} 

/** 
* 
* Method: resetMemberVars(int pickFloor) 
* 
*/ 
@Test
public void testResetMemberVars() throws Exception { 
//TODO: Test goes here...
    SingleRequest t = new SingleRequest("(ER,7,0)",0);
    Assert.assertEquals(t.isLegalRequest(),true);
    testElev.setCompleteRequest(t);
    testElev.setMoveDire();
    testElev.setCompleteTime();

    //主请求预设完成
    Assert.assertEquals(testElev.getMoveDire(),STATUS_UP);
    testElev.resetMemberVars(5);//5层的捎带请求
    Assert.assertEquals(testElev.getMoveDire(),STATUS_UP);
    Assert.assertEquals(Math.abs(testElev.getCompleteTime()-5.0)<0.0001,true);
    Assert.assertEquals(testElev.getStartFloor(),5);
    Assert.assertEquals(Math.abs(testElev.getStartTime()-3.0)<0.0001,true);

}

/** 
* 
* Method: accomplishPickedRequest(SingleRequest objRequest) 
* 
*/ 
@Test
public void testAccomplishPickedRequest() throws Exception { 
//TODO: Test goes here...
    //t1向上捎带t2
    //t3向下捎带t4
    SingleRequest t1 = new SingleRequest("(ER,7,0)",0);
    SingleRequest t2 = new SingleRequest("(ER,3,0)",0);
    SingleRequest t3 = new SingleRequest("(ER,3,10)",0);
    SingleRequest t4 = new SingleRequest("(FR,5,DOWN,10)",10);
    Assert.assertEquals(t1.isLegalRequest(),true);
    Assert.assertEquals(t2.isLegalRequest(),true);
    Assert.assertEquals(t3.isLegalRequest(),true);
    Assert.assertEquals(t4.isLegalRequest(),true);

    testElev.setCompleteRequest(t1);
    testElev.setCompleteTime();
    testElev.setMoveDire();

    testElev.accomplishPickedRequest(t2);
    Assert.assertEquals(bao.toString().equals("[ER,3,0] / (3,UP,1.0)"+lineSeparator),true);
    bao.reset();
    testElev.resetMemberVars(3);//捎带导致的重设

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[ER,7,0] / (7,UP,4.0)"+lineSeparator),true);
    bao.reset();

    testElev.setCompleteRequest(t3);
    testElev.setCompleteTime();
    testElev.setMoveDire();

    testElev.accomplishPickedRequest(t4);
    Assert.assertEquals(bao.toString().equals("[FR,5,DOWN,10] / (5,DOWN,11.0)"+lineSeparator),true);
    bao.reset();
    testElev.resetMemberVars(5);//捎带导致的重设

    testElev.outPut();
    Assert.assertEquals(bao.toString().equals("[ER,3,10] / (3,DOWN,13.0)"+lineSeparator),true);
} 

/** 
* 
* Method: getStartFloor() 
* 
*/ 
@Test
public void testGetStartFloor() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(testElev.getStartFloor(),1);
    SingleRequest t1 = new SingleRequest("(ER,5,1)",0);
    SingleRequest t2 = new SingleRequest("(FR,8,UP,3)",1);
    Assert.assertEquals(t1.isLegalRequest(),true);
    Assert.assertEquals(t2.isLegalRequest(),true);
    testElev.setCompleteRequest(t1);
    Assert.assertEquals(testElev.getStartFloor(),1);
    testElev.setCompleteRequest(t2);
    Assert.assertEquals(testElev.getStartFloor(),5);
} 

/** 
* 
* Method: getStartTime() 
* 
*/ 
@Test
public void testGetStartTime() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(Math.abs(testElev.getStartTime()-0.0)<0.0001,true);
    SingleRequest t1 = new SingleRequest("(ER,5,1)",0);
    SingleRequest t2 = new SingleRequest("(FR,8,UP,6)",1);
    Assert.assertEquals(t1.isLegalRequest(),true);
    Assert.assertEquals(t2.isLegalRequest(),true);
    testElev.setCompleteRequest(t1);
    testElev.setCompleteTime();

    Assert.assertEquals(Math.abs(testElev.getStartTime()-1.0)<0.0001,true);
    testElev.setCompleteRequest(t2);
    testElev.setCompleteTime();
    Assert.assertEquals(Math.abs(testElev.getStartTime()-6.0)<0.0001,true);
} 


} 
