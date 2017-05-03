package core;

/**
 * Created on 2017-04-30.
 */
class RoadRequest implements GlobalConstant{
    private boolean legacy;
    private int firstRow;
    private int firstCol;
    private int nextRow;
    private int nextCol;
    private int type;
    RoadRequest(String first,String next,String type) {
        /*@REQUIRES:first & next & type!=null;
        @MODIFIES:\all member vars
        @EFFECTS:normal_behavior:提取字符串信息,构造出一个完整的道路请求.
                 非法字符串==>exceptional_behavior:(Exception) this.legacy = false;
        */
        this.legacy = true;
        switch (type) {
            case OPEN_ROAD:
                this.type = openRoad;
                break;
            case CLOSE_ROAD:
                this.type = closeRoad;
                break;
            default:
                this.legacy = false;
                return;
        }
        //如果类型错误,直接退出.
        if (first.length() < 5 || next.length() < 5)
            this.legacy = false;
        else {
            try {
                String firstArg[] = first.substring(1, first.length() - 1).split(",");
                String nextArg[] = next.substring(1, next.length() - 1).split(",");
                if (firstArg.length != 2 || nextArg.length != 2)
                    this.legacy = false;
                else {
                    firstRow = Integer.parseInt(firstArg[0]);
                    firstCol = Integer.parseInt(firstArg[1]);
                    nextRow = Integer.parseInt(nextArg[0]);
                    nextCol = Integer.parseInt(nextArg[1]);
                    this.legacy = checkRowCol(firstRow, firstCol) && checkRowCol(nextRow, nextCol) && ((firstRow == nextRow &&
                            Math.abs(firstCol - nextCol) == 1) || (firstCol == nextCol && Math.abs(firstRow - nextRow) == 1));
                }
            } catch (Exception e) {
                this.legacy = false;
            }
        }
    }
    private boolean checkRowCol(int row, int col){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回(row,col)是否合法
        */
        return (row>=0) && (row<ROW_NUMBER) && (col>=0) && (col<COL_NUMBER);
    }

    boolean isLegacy(){
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回legacy的值
        */
        return legacy;
    }

    int getFirstRow() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回firstRow的值
        */
        return firstRow;
    }

    int getFirstCol() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回firstCol的值
        */
        return firstCol;
    }

    int getNextRow() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回nextRow的值
        */
        return nextRow;
    }

    int getNextCol() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回nextCol的值
        */
        return nextCol;
    }

    int getType() {
        /*@REQUIRES:None
        @MODIFIES:None
        @EFFECTS:返回type的值
        */
        return type;
    }
}
