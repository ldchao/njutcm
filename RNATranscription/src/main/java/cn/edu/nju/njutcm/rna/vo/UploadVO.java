package cn.edu.nju.njutcm.rna.vo;

/**
 * Created by ldchao on 2018/5/7.
 */
public class UploadVO {
    private Integer code;
    private String msg;
    private String data;

    public UploadVO() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
