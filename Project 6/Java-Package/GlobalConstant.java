package core;

/**
 * Created on 2017-04-10.
 */
public interface GlobalConstant {
    int MAX_PATH = 8;
    int MIN_PATH = 5;
    int MONITOR_ARGUMENTS = 5;
    //for monitor
    int RENAME = 1;
    int MODIFY = 2;
    int PATH_CHANGE = 3;
    int SIZE_CHANGE = 4;
    //for task
    int SUMMARY = 1;
    int DETAIL  = 2;
    int RECOVER = 3;
    //for command type
    int READ_FILE_INFO = 1;//读取文件信息,包括名称大小修改时间
    int RENAME_FILE = 2;//重命名
    int MOVE = 3;//移动
    int ADD_FILE = 4;//新建文件
    int ADD_DIRECTORY = 5;//新增目录
    int DELETE_FILE = 6;//删除文件;
    int DELETE_DIRECTORY = 7;//删除目录
    int WRITE = 8;//向文件内写入内容，造成文件大小发生变化
    int QUIT = 9;
    //for summary and detail刷新
    int UPDATE_INTERVAL = 10000;//10s刷新一次.
    //for HashMap hold
    int HASH = 100;
}
