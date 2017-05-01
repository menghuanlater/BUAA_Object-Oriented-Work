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
        return (row>=0) && (row<ROW_NUMBER) && (col>=0) && (col<COL_NUMBER);
    }

    boolean isLegacy(){return legacy;}

    int getFirstRow() {
        return firstRow;
    }

    int getFirstCol() {
        return firstCol;
    }

    int getNextRow() {
        return nextRow;
    }

    int getNextCol() {
        return nextCol;
    }

    int getType() {
        return type;
    }
}
