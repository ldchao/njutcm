package cn.edu.nju.njutcm.rna.vo;

import cn.edu.nju.njutcm.rna.model.TaskEntity;

import java.sql.Timestamp;

/**
 * Created by ldchao on 2018/5/12.
 */
public class TaskVO {
    private int id;
    private String user;
    private String taskName;
    private String type;
    private Timestamp startAt;
    private Timestamp endAt;
    private String status;

    public TaskVO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStartAt() {
        return startAt;
    }

    public void setStartAt(Timestamp startAt) {
        this.startAt = startAt;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void update(TaskEntity taskEntity){
        this.id = taskEntity.getId();
        this.user = taskEntity.getUser();
        this.taskName = taskEntity.getTaskName();
        this.type = taskEntity.getType();
        this.startAt = taskEntity.getStartAt();
        this.endAt = taskEntity.getEndAt();
        this.status = taskEntity.getStatus();
    }
}
