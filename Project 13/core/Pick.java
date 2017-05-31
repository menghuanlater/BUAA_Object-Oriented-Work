package core;

/**
 * Created on 2017-03-19.
 * we use extend to reuse Dispatcher class
 * we build a local class to record the pick able requests -->we only need one instance
 * */
public class Pick{
    private int indexOfFetch;
    private SingleRequest pickAbleRequest;
    public Pick(int indexOfFetch,SingleRequest pickAbleRequest){
        this.indexOfFetch = indexOfFetch;
        this.pickAbleRequest = pickAbleRequest;
    }
    public int getIndexOfFetch(){
        return indexOfFetch;
    }
    public SingleRequest getPickAbleRequest(){
        return pickAbleRequest;
    }
}
