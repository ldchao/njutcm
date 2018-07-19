package cn.edu.nju.njutcm.rna.task;

import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.TaskEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * Created by ldchao on 2018/5/13.
 */
public class TaskThread implements Runnable {


    private Integer id;
    private TaskDao taskDao;

    public TaskThread(Integer id, TaskDao taskDao) {
        this.id = id;
        this.taskDao = taskDao;
    }

    @Override
    public void run() {
        TaskEntity taskEntity = taskDao.findOne(id);
        System.out.println("线程启动");
        //更新状态
        taskEntity.setStatus("executing");
        taskDao.saveAndFlush(taskEntity);


        //执行命令
        String cmd =taskEntity.getTaskCode();
        Runtime run = Runtime.getRuntime();
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
//            for (String line : commands) {
//                out.println(line);
//            }
             out.println(cmd);
            out.println("exit");// 这个命令必须执行，否则in流不结束。
            StringBuffer sb = new StringBuffer();
            String line ;
            while ((line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println("*********任务"+taskEntity.getTaskName() +"*************");
            System.out.println(sb.toString());
            System.out.println("*****************************************");
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
            taskEntity.setStatus("success");
            taskEntity.setEndAt(new Timestamp(System.currentTimeMillis()));
        } catch (IOException | InterruptedException e1) {
            taskEntity.setStatus("fail");
        }

        taskDao.saveAndFlush(taskEntity);
        System.out.println("线程结束");
    }

}
