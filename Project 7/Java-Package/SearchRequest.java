package core;

/**
 * Created on 17-4-16.
 */
class SearchRequest implements GlobalConstant{
    private int taxiCode;
    private double requestTime;
    private String request;
    private boolean legacy;
    SearchRequest(String request,String id,double requestTime){
        this.request = request;
        this.legacy = true;
        this.requestTime = requestTime;
        try {
            taxiCode = Integer.parseInt(id);
            if(taxiCode<0 || taxiCode>=SUM_CARS)
                legacy = false;
        }catch (Exception e){
            legacy = false;
        }
    }
    int getTaxiCode() {
        return taxiCode;
    }
    boolean isLegacy(){return legacy;}
    public String toString(){
        return request+":\t当前时间:"+requestTime+"s\t出租车编号:"+taxiCode;
    }
}
