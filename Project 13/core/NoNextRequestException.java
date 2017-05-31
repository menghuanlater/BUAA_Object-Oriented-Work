package core;

/**
 * Created on 2017-05-28.
 * Overview:当请求队列没有下一个请求却仍然调用取请求方法抛出的异常
 */
public class NoNextRequestException extends Exception{
    public NoNextRequestException(String expMessage){
        super(expMessage);
    }
}
