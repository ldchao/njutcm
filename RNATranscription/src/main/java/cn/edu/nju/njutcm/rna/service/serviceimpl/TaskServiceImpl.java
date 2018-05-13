package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.FileDao;
import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.task.RNATranscriptionTask;
import cn.edu.nju.njutcm.rna.task.ThreadPoolFactory;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ldchao on 2018/5/12.
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskDao taskDao;

    @Autowired
    FileDao fileDao;

    @Override
    public String createTask(TaskEntity taskEntity) {
        TaskEntity result=taskDao.saveAndFlush(taskEntity);
        return startTask(result);
    }

    @Override
    public String getStatusById(Integer id) {
        TaskEntity taskEntity=taskDao.findOne(id);
        return taskEntity.getStatus();
    }

    @Override
    public String reStartTaskById(Integer id) {
        TaskEntity taskEntity=taskDao.findOne(id);
        return startTask(taskEntity);
    }

    @Override
    public List<TaskVO> getAllTaskByUser(String username) {
        List<TaskEntity> taskEntityList=taskDao.findAllByUserOrderByStartAtDesc(username);
        List<TaskVO> result=new ArrayList<TaskVO>();
        for (TaskEntity taskEntity:taskEntityList) {
            TaskVO taskVO=new TaskVO();
            taskVO.update(taskEntity);
            result.add(taskVO);
        }
        return result;
    }

    @Override
    public String getResultPathById(Integer id) {
        TaskEntity taskEntity=taskDao.findOne(id);
        return taskEntity.getResultFile();
    }

    private String startTask(TaskEntity taskEntity){
        FileEntity fileEntity=fileDao.findOne(taskEntity.getFileId());
        String inputFilePath=fileEntity.getSavepath();
        File inputFile=new File(inputFilePath);
        if(!inputFile.exists()){
            return "file not exist";
        }
        RNATranscriptionTask transcriptionTask=new RNATranscriptionTask(taskEntity.getId(),inputFilePath,taskDao);
        ExecutorService executorService= ThreadPoolFactory.getExecutorService();
        executorService.execute(transcriptionTask);
        return "success";
    }
}
