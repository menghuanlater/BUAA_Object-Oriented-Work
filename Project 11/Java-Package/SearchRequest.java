package core;

/**
 * Created on 17-4-16.
 */
public class SearchRequest implements GlobalConstant{
    /*
    Overview:查询出租车相关状态的请求.记录查询类型、查询出租车的编号、查询时间等
     */
    private int taxiCode;
    private int taxiStatus;
    private boolean taxiCodeSearch;//是否是指定出租车搜索
    private double requestTime;
    private String request;
    private boolean legacy;
    SearchRequest(String request,String idOrStatus,double requestTime){
        /*@REQUIRES:request & idOrStatus!=null and requestTime>=0.0
        @MODIFIES:\all member vars
        @EFFECTS:normal_behavior:提取字符串信息,构造出一个完整的查询请求请求.
                 非法字符串==>exceptional_behavior:(Exception) this.legacy = false;
        */
        this.request = request;
        this.legacy = true;
        this.requestTime = requestTime;
        try {
            taxiCode = Integer.parseInt(idOrStatus);
            if(taxiCode<0 || taxiCode>=SUM_CARS)
                legacy = false;
            taxiCodeSearch = true;
        }catch (Exception e){//代表不是指定出租车搜索
            taxiCodeSearch = false;
            switch (idOrStatus){
                case STOPPING:
                    taxiStatus = STOP_SERVICE;
                    break;
                case SERVING:
                    taxiStatus = IN_SERVICE;
                    break;
                case WAITING:
                    taxiStatus = WAIT_SERVICE;
                    break;
                case GRABBING:
                    taxiStatus = GRAB_SERVICE;
                    break;
                default:legacy = false;
            }
        }
    }
    /*@repOk.
    check:1.legacy==true
    2.0<=taxiCode<=99
    3.0<=taxiStatus<=3
    4.requestTime>0.0
    5.request!=null && request is instanceof String
     */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
         */
        if(!legacy) return false;
        if(taxiCode<0 && taxiCode>=SUM_CARS)
            return false;
        if(taxiStatus<0 || taxiStatus>3)
            return false;
        if(requestTime<0.0)
            return false;
        if(request==null)
            return false;
        Object x = request;
        if(!(x instanceof String)) return false;
        return true;
    }
    int getTaxiCode() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回taxiCode的值
        */
        return taxiCode;
    }
    int getTaxiStatus(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回taxiStatus的值
        */
        return taxiStatus;
    }
    boolean isLegacy(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回legacy的值
        */
        return legacy;
    }
    boolean isTaxiSearch(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回taxiCodeSearch的值
        */
        return taxiCodeSearch;
    }
    @Override
    public String toString(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回请求的字符串形式
        */
        return (taxiCodeSearch)?(request+":\t当前时间:"+requestTime+"s\t出租车编号:"+taxiCode):request;
    }
}