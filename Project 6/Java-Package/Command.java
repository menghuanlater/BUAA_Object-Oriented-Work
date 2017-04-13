package core;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created on 2017-04-10.
 * support some command for users to operate file system.
 *
 */
public class Command extends Thread implements GlobalConstant{
    private Scanner input = new Scanner(System.in);
    public void run(){
        String pathname;//输入的路径名
        int choice; boolean quitFlag = false;
        while(true){
            if(quitFlag){ //over
                input.close();
                break;
            }
            //打印菜单
            outPutMenu();
            //读取输入选项
            try{
                choice = input.nextInt();
                input.nextLine();
            }catch (InputMismatchException e){
                System.out.println("Illegal choice.");
                input.nextLine();
                continue;
            }

            switch (choice){
                case READ_FILE_INFO:
                    System.out.print("请输入路径:");
                    pathname = input.nextLine();
                    Main.safeFile.readFileInfo(pathname);
                    break;
                case RENAME_FILE:
                    System.out.print("请输入目标文件:");
                    String objPathName = input.nextLine();
                    System.out.print("请输入重命名文件名(无需路径):");
                    String targetFilename = input.nextLine();
                    Main.safeFile.renameTo(objPathName,targetFilename);
                    break;
                case MOVE:
                    System.out.print("请输入要移动的目标文件：");
                    objPathName = input.nextLine();
                    System.out.print("请输入移动的目标文件夹：");
                    String targetPath = input.nextLine();
                    Main.safeFile.moveTo(objPathName,targetPath);
                    break;
                case ADD_FILE:
                    System.out.print("请输入文件加入目录:");
                    objPathName = input.nextLine();
                    System.out.print("请输入新增的文件名:");
                    targetFilename = input.nextLine();
                    Main.safeFile.createNewFile(objPathName,targetFilename);
                    break;
                case ADD_DIRECTORY:
                    System.out.print("请直接输入新建后目录路径(支持多重创建)：");
                    objPathName = input.nextLine();
                    Main.safeFile.mkdirs(objPathName);
                    break;
                case DELETE_FILE:
                    System.out.print("请输入要删除的文件:");
                    targetFilename = input.nextLine();
                    Main.safeFile.delete(targetFilename);
                    break;
                case DELETE_DIRECTORY:
                    System.out.println("!!!注意!!! 请务必确保删除的目录不是操作系统保护的目录或文件的顶层目录以及触发器直接关联的目录.");
                    System.out.print("请输入所需删除的目录路径：");
                    objPathName = input.nextLine();
                    Main.safeFile.deleteDirectory(objPathName);
                    break;
                case WRITE:
                    System.out.print("请输入写入的文件名：");
                    targetFilename = input.nextLine();
                    System.out.print("请输入写入的字符串,(一行输入):");
                    String wrietString = input.nextLine();
                    Main.safeFile.write(targetFilename,wrietString);
                    break;
                case QUIT:
                    quitFlag = true;
                    break;
                default:
                    System.out.println("Illegal choice.");
                    break;
            }
        }
    }
    //打印菜单
    private void outPutMenu(){
        System.out.println("-----------------Command Menu------------------");
        System.out.println("(1)读取文件/文件夹相关信息\t\t(2)重命名文件");
        System.out.println("(3)移动文件\t\t\t\t\t(4)新建文件");
        System.out.println("(5)新建文件夹\t\t\t\t\t(6)删除文件");
        System.out.println("(7)删除文件夹\t\t\t\t\t(8)写入内容到文件");
        System.out.println("(9)退出命令输入,停止所有监控**********************");
        System.out.println("-----------------------------------------------");
        System.out.print("enter choice:");
    }
}
