package core;

/**
 * Created on 17-4-16.
 */
class SearchRequest implements GlobalConstant{
    private int taxiCode;
    private int taxiStatus;
    private boolean taxiCodeSearch;//是否是指定出租车搜索
    private double requestTime;
    private String request;
    private boolean legacy;
    SearchRequest(String request,String idOrStatus,double requestTime){
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
                case SERVIING:
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
    int getTaxiCode() {
        return taxiCode;
    }
    int getTaxiStatus(){return taxiStatus;}
    boolean isLegacy(){return legacy;}
    boolean isTaxiSearch(){return taxiCodeSearch;}
    public String toString(){
        return request+":\t当前时间:"+requestTime+"s\t出租车编号:"+taxiCode;
    }
}
