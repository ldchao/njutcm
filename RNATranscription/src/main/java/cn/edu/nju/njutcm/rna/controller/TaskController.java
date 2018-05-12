package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@RestController
public class TaskController {

    @Autowired
    TaskService taskService;

    //创建任务并运行
    @PostMapping("/createTask")
    public String createTask(HttpServletRequest request,String taskName,String type,Integer fileId){
        TaskEntity taskEntity=new TaskEntity();
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType(type);
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setFileId(fileId);
        taskEntity.setStatus("排队中");
        return taskService.createTask(taskEntity);
    }

    //根据id查询任务状态
    @GetMapping("/getStatusById")
    public String getStatusById(Integer id){
        return taskService.getStatusById(id);
    }

    //根据id重新运行任务
    @GetMapping("/reStartTaskById")
    public String reStartTaskById(Integer id){
        return taskService.reStartTaskById(id);
    }

    //获取所有任务列表
    public List<TaskVO> getAllTaskByUser(HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return taskService.getAllTaskByUser(userVO.getUsername());
    }


}
