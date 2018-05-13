package cn.edu.nju.njutcm.rna.task;

import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.model.TaskEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ldchao on 2018/5/13.
 */
public class RNATranscriptionTask implements Runnable {


    private Integer id;
    private String inputFile;
    private TaskDao taskDao;

    public RNATranscriptionTask(Integer id, String inputFile, TaskDao taskDao) {
        this.id = id;
        this.inputFile = inputFile;
        this.taskDao = taskDao;
    }

    @Override
    public void run() {
        TaskEntity taskEntity = taskDao.findOne(id);
        System.out.println("启动线程");
        //更新状态
        taskEntity.setStatus("正在执行");
        taskDao.saveAndFlush(taskEntity);

        String fileName = inputFile.substring(inputFile.lastIndexOf(File.separator)+1);
        int typeSplit=fileName.lastIndexOf(".");
        if(typeSplit!=-1){
            fileName = fileName.substring(0,typeSplit);
        }
        //执行命令
        try {
            String cmd = "fastqc -f fastq -o /opt/data/result/ "+inputFile;
            Process ps = Runtime.getRuntime().exec(cmd);
            Boolean result = ps.waitFor(60, TimeUnit.MINUTES);
            if(result){
                taskEntity.setStatus("执行成功");
                taskEntity.setResultFile("/opt/data/result/"+fileName + "_fastqc.html");
                taskEntity.setEndAt(new Timestamp(System.currentTimeMillis()));
            }else{
                taskEntity.setStatus("执行超时");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;


            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            taskEntity.setStatus("执行失败");
        } catch (InterruptedException e) {
            taskEntity.setStatus("执行失败");
        }
        taskDao.saveAndFlush(taskEntity);
    }

}
