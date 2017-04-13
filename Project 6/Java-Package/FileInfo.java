package core;

/**
 * Created on 17-4-11.
 * for monitor file
 */
class FileInfo {
    private String filepath;
    private long lastModified;
    private long size;
    private boolean isNewAdd;//检测与之前对比是否是新增的文件
    FileInfo(){
        filepath = null;
        lastModified = 0;
        size = 0;
        isNewAdd = true;
    }

    String getFilepath() {
        return filepath;
    }

    void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    long getLastModified() {
        return lastModified;
    }

    void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    long getSize() {
        return size;
    }

    void setSize(long size) {
        this.size = size;
    }

    public boolean isNewAdd() {
        return isNewAdd;
    }

    public void setNewAdd(boolean newAdd) {
        isNewAdd = newAdd;
    }
}
