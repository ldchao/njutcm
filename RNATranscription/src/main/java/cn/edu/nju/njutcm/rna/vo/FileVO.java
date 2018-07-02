package cn.edu.nju.njutcm.rna.vo;

/**
 * Created by ldchao on 2018/6/15.
 */
public class FileVO {

    private String fileName;
    private boolean isDir;
    private String relativePath;

    public FileVO() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
