package cn.edu.nju.njutcm.rna;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.service.UserService;
import cn.edu.nju.njutcm.rna.vo.FileVO;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RnaApplicationTests {

	@Autowired
	FileService fileService;

	@Autowired
	TaskService taskService;

	@Autowired
	UserService userService;

	@Test
	public void contextLoads() {
		System.out.println(fileService.getByUser("test").size());
	}

	@Test
	public void testGetAllTaskByUser(){
		List<TaskVO> list=taskService.getAllTaskByUser("admin");
		for (TaskVO t:
			 list) {
			System.out.println(t.getStatus());
		}

	}

	@Test
	public void testCreateTask(){
		TaskEntity taskEntity=new TaskEntity();
		taskEntity.setUser("admin");
		taskEntity.setTaskName("test");
		taskEntity.setType("all");
		taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
		taskEntity.setFileId(1);
		taskEntity.setStatus("queuing");
		System.out.println(taskService.createTask(taskEntity));
	}

	@Test
	public void testGetStatusById(){
		System.out.println(taskService.getStatusById(1));
	}
	@Test
	public void testReStartTaskById(){
		System.out.println(taskService.reStartTaskById(1));
	}
	@Test
	public void testGetResultPathById(){
		System.out.println(taskService.getTaskEntityById(1).getTaskName());
	}

	@Test
	public void addUser(){
		userService.addUser("admin","123");
		userService.addUser("test","123");
	}

	@Test
	public void getFileList(){
		List<FileVO> list=fileService.getByUserAndPath("admin","\\");
		for (FileVO file:list) {
			System.out.println(file.getFileName() + "_" + file.isDir() +"_" + file.getRelativePath());
		}
	}

}
