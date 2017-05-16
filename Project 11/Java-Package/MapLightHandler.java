package core;

import java.io.*;

/**
 * Created on 17-4-16.
 * 读取图文件,进行图的存储
 * 读取红绿灯文件,进行红绿灯初始化信息存储
 */
public class MapLightHandler implements GlobalConstant{
    /*
    Overview:读取map.txt以及light.txt文件中的相关内容,将相关信息存储在数据结构中
    */
    private BufferedReader mapReader;
    private BufferedReader lightReader;
    MapLightHandler(){
        /*@REQUIRES:None
        @MODIFIES:mapReader,lightReader,System.out
        @EFFECTS:normal_behavior:如果文件存在==>读取文件内容
                 文件输入流打开失败==>exceptional_behavior:(FileNotFoundException)终端输出提示,结束程序
        */
        File mapFile = new File(MAP_NAME);
        File lightFile = new File(LIGHT_NAME);
        try {
            mapReader = new BufferedReader(new FileReader(mapFile));
            lightReader = new BufferedReader(new FileReader(lightFile));
        } catch (FileNotFoundException e) {
            System.out.println("not found map.txt or light.txt");
            System.exit(0);
        }
    }
    /*@repOk.(当文件正常打开时)
    check:mapReader!=null && lightReader!=null
    */
    public boolean repOk(){
        /*
        @EFFECTS:\result == invariant(this)
        */
        if(mapReader==null ||lightReader==null )
            return false;
        return true;
    }
    void readTheMapFile() throws IOException {
        /*@REQUIRES:mapReader and lightReader is Ok; Main.matrix,Main.matrixGui,Main.matrixInit,Main.redGreenLight all Ok.
        @MODIFIES:mapReader,lightReader,Main.matrix,Main.matrixInit,Main.matrixGui，Main.redGreenLight.globalLight,
                 Main.redGreenLight.lightSets.
        @EFFECTS:normal_behavior:如果文件输入流读取正常==>读取地图,修改邻接矩阵,如果发现地图不合法则调用Main.illegalMap()结束程序
                 文件输入流读取异常==>exceptional_behavior:(IOException)throw it.
                 存在非法字符==>exceptional_behavior:(Exception)文件非法,调用Main类方法终止程序
        */
        int rowCount = 0;
        String mapLine,lightLine;
        int lightStatus = 1+(int)(Math.random()*2);//设置所有初始东西向红绿灯的颜色1 or 2.
        while((mapLine=mapReader.readLine())!=null && (!mapLine.equals("")) && (lightLine=lightReader.readLine())!=null &&
                (!lightLine.equals("")) && rowCount<ROW_NUMBER){
            String mapNumbers[] = mapLine.replaceAll("\\s+","").split("");//分割字符串
            String lightNumbers[] = lightLine.replaceAll("\\s+","").split("");//分割字符串
            if(mapNumbers.length!=COL_NUMBER || lightNumbers.length!=COL_NUMBER){
                Main.illegalMapLight();
            }
            for(int i=0;i<COL_NUMBER;i++){
                try{
                    int relation = Integer.parseInt(mapNumbers[i]);
                    int light = Integer.parseInt(lightNumbers[i]);
                    int objCode = Main.getCodeByRowCol(rowCount,i);//节点编号
                    switch (relation){
                        case NO_CONNECT:
                            Main.matrixGui[rowCount][i] = NO_CONNECT;
                            break;
                        case CONNECT_RIGHT:
                            Main.matrixGui[rowCount][i] = CONNECT_RIGHT;
                            int rightCode = Main.getCodeByRowCol(rowCount,i+1);
                            Main.matrix[objCode][rightCode] = Main.matrix[rightCode][objCode] = true;
                            Main.matrixInit[objCode][rightCode] = Main.matrixInit[rightCode][objCode] = true;
                            break;
                        case CONNECT_DOWN:
                            Main.matrixGui[rowCount][i] = CONNECT_DOWN;
                            int downCode = Main.getCodeByRowCol(rowCount+1,i);
                            Main.matrix[objCode][downCode] = Main.matrix[downCode][objCode] = true;
                            Main.matrixInit[objCode][downCode] = Main.matrixInit[downCode][objCode] = true;
                            break;
                        case ALL_CONNECT:
                            Main.matrixGui[rowCount][i] = ALL_CONNECT;
                            rightCode = Main.getCodeByRowCol(rowCount,i+1);
                            downCode = Main.getCodeByRowCol(rowCount+1,i);
                            Main.matrix[rightCode][objCode] = Main.matrix[objCode][rightCode] = true;
                            Main.matrix[downCode][objCode] = Main.matrix[objCode][downCode] = true;
                            Main.matrixInit[rightCode][objCode] = Main.matrixInit[objCode][rightCode] = true;
                            Main.matrixInit[downCode][objCode] = Main.matrixInit[objCode][downCode] = true;
                            break;
                        default:Main.illegalMapLight();break;
                    }
                    switch (light){
                        case NOT_LIGHT:
                            Main.redGreenLight.globalLight[objCode] = NOT_LIGHT;
                            break;
                        case HAVE_LIGHT:
                            Main.redGreenLight.lightSets.add(objCode);//写入哪个路口有红绿灯
                            Main.redGreenLight.globalLight[objCode] = lightStatus;//标记红绿灯值
                            break;
                        default:Main.illegalMapLight();break;
                    }
                }catch (Exception e){
                    Main.illegalMapLight();
                }
            }
            rowCount++;
        }
        if(rowCount!=ROW_NUMBER)
            Main.illegalMapLight();
    }
}
