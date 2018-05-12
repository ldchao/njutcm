package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskDao taskDao;

    @Override
    public String createTask(TaskEntity taskEntity) {
        TaskEntity result=taskDao.saveAndFlush(taskEntity);
        // TODO: 2018/5/12 启动线程
        return null;
    }

    @Override
    public String getStatusById(Integer id) {
        TaskEntity taskEntity=taskDao.findOne(id);
        return taskEntity.getStatus();
    }

    @Override
    public String reStartTaskById(Integer id) {
        TaskEntity taskEntity=taskDao.findOne(id);
        // TODO: 2018/5/12 启动线程
        return null;
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
}
