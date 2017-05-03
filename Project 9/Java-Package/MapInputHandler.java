package core;

import java.io.*;

/**
 * Created on 17-4-16.
 * 读取图文件,进行图的存储
 */
class MapInputHandler implements GlobalConstant{
    private BufferedReader bufferedReader;
    MapInputHandler(){
        /*@REQUIRES:None
        @MODIFIES:bufferedReader,System.out
        @EFFECTS:normal_behavior:如果文件存在==>读取文件内容
                 文件输入流打开失败==>exceptional_behavior:(FileNotFoundException)终端输出提示,结束程序
        */
        File mapFile = new File(MAP_NAME);
        try {
            bufferedReader = new BufferedReader(new FileReader(mapFile));
        } catch (FileNotFoundException e) {
            System.out.println("not found map.txt");
            System.exit(0);
        }
    }
    void readTheMapFile() throws IOException {
        /*@REQUIRES:None
        @MODIFIES:bufferedReader,Main.matrix,Main.matrixGui
        @EFFECTS:normal_behavior:如果文件输入流读取正常==>读取地图,修改邻接矩阵,如果发现地图不合法则调用Main.illegalMap()结束程序
                 文件输入流读取异常==>exceptional_behavior:(IOException)throw it.
        */
        int rowCount = 0;
        String line;
        while((line=bufferedReader.readLine())!=null && (!line.equals("")) && rowCount<ROW_NUMBER){
            String numbers[] = line.split("\\s+");//分割字符串
            if(numbers.length!=COL_NUMBER){
                Main.illegalMap();
            }
            for(int i=0;i<COL_NUMBER;i++){
                try{
                    int relation = Integer.parseInt(numbers[i]);
                    int objCode = Main.getCodeByRowCol(rowCount,i);
                    switch (relation){
                        case NO_CONNECT:
                            Main.matrixGui[rowCount][i] = NO_CONNECT;
                            break;
                        case CONNECT_RIGHT:
                            Main.matrixGui[rowCount][i] = CONNECT_RIGHT;
                            int rightCode = Main.getCodeByRowCol(rowCount,i+1);
                            Main.matrix[objCode][rightCode] = Main.matrix[rightCode][objCode] = true;
                            break;
                        case CONNECT_DOWN:
                            Main.matrixGui[rowCount][i] = CONNECT_DOWN;
                            int downCode = Main.getCodeByRowCol(rowCount+1,i);
                            Main.matrix[objCode][downCode] = Main.matrix[downCode][objCode] = true;
                            break;
                        case ALL_CONNECT:
                            Main.matrixGui[rowCount][i] = ALL_CONNECT;
                            rightCode = Main.getCodeByRowCol(rowCount,i+1);
                            downCode = Main.getCodeByRowCol(rowCount+1,i);
                            Main.matrix[rightCode][objCode] = Main.matrix[objCode][rightCode] = true;
                            Main.matrix[downCode][objCode] = Main.matrix[objCode][downCode] = true;
                            break;
                        default:Main.illegalMap();break;
                    }
                }catch (Exception e){
                    Main.illegalMap();
                }
            }
            rowCount++;
        }
        if(rowCount!=ROW_NUMBER)
            Main.illegalMap();
    }
}
