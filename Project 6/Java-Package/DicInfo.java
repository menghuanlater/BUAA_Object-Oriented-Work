package core;

/**
 * Created on 17-4-11.
 * for monitor directory
 */
class DicInfo extends FileInfo{
    private boolean isDirectory;
    private String filename;//当是文件时,保存文件名,用于鉴别path-change
    private String parent;//父目录
    DicInfo(){
        super();
        isDirectory = false;
        filename = null;
        parent = null;
    }

    boolean isDirectory() {
        return isDirectory;
    }

    void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    String getFilename() {
        return filename;
    }

    void setFilename(String filename) {
        this.filename = filename;
    }

    String getParent() {
        return parent;
    }

    void setParent(String parent) {
        this.parent = parent;
    }
}
