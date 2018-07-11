package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.FileDao;
import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.task.TaskThread;
import cn.edu.nju.njutcm.rna.task.ThreadPoolFactory;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.ZipUtil;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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
    public boolean isTaskNameExist(String username, String taskName) {
        return taskDao.countByUserEqualsAndTaskNameEquals(username,taskName) ==0 ? false:true;
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
    public TaskEntity getTaskEntityById(Integer id) {
        return taskDao.findOne(id);
    }

    private String startTask(TaskEntity taskEntity){

        TaskThread transcriptionTask=new TaskThread(taskEntity.getId(),taskDao);
        ExecutorService executorService= ThreadPoolFactory.getExecutorService();
        executorService.execute(transcriptionTask);
        return "success";
    }

    @Override
    public String zipResult(String path,String taskName) {
        String zipPath = ApplicationUtil.getInstance().getRootPath() + File.separator + "tmp"
                + File.separator + UUID.randomUUID().toString().replace("-","");
        ZipUtil.fileToZip(path,zipPath,taskName);
        return zipPath + File.separator + taskName + ".zip";
    }

    @Override
    public void deleteZipResult(String filePath) {
        ZipUtil.deleteZipFile(filePath);
    }


}
