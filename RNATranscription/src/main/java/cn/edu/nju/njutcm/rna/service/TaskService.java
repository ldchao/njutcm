package cn.edu.nju.njutcm.rna.service;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.vo.TaskVO;

import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
public interface TaskService {

    //创建任务并运行
    public String createTask(TaskEntity taskEntity);

    //根据id删除任务
    public String deleteTaskById(Integer id);

    //根据id查询任务状态
    public String getStatusById(Integer id);

    //根据id重新运行任务
    public String reStartTaskById(Integer id);

    //获取所有任务列表
    public List<TaskVO> getAllTaskByUser(String username);

    //根据关键字搜索所有任务列表
    public List<TaskVO> searchTask(String username,String keyWord);

    //根据id获取结果文件路径
    public TaskEntity getTaskEntityById(Integer id);

    public String zipResult(String path,String taskName);

    public void deleteZipResult(String filePath);

}
