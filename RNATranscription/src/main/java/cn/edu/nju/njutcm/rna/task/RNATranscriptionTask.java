package cn.edu.nju.njutcm.rna.task;

import cn.edu.nju.njutcm.rna.dao.TaskDao;
import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.FileUtil;

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
        System.out.println("线程启动");
        //更新状态
        taskEntity.setStatus("executing");
        taskDao.saveAndFlush(taskEntity);

        String fileName = inputFile.substring(inputFile.lastIndexOf(File.separator) + 1);
        int typeSplit = fileName.lastIndexOf(".");
        if (typeSplit != -1) {
            fileName = fileName.substring(0, typeSplit);
        }

        String resultDir = ApplicationUtil.getInstance().getRootPath() +File.separator+ "result" + File.separator
                + taskEntity.getUser() + File.separator + taskEntity.getId() + File.separator;
        FileUtil.makeSureDirExist(resultDir);
//        try {
//            Thread.sleep(20000);
//            taskEntity.setStatus("success");
//            taskEntity.setResultFile(inputFile);
//            taskEntity.setEndAt(new Timestamp(System.currentTimeMillis()));
//
//        } catch (InterruptedException e) {
//            taskEntity.setStatus("fail");
//        }

        //执行命令
        try {
            String cmd = "fastqc -f fastq -o " + resultDir + " " + inputFile;
            Process ps = Runtime.getRuntime().exec(cmd);
            Boolean result = ps.waitFor(60, TimeUnit.MINUTES);
            if (result) {
                taskEntity.setStatus("success");
                taskEntity.setResultFile(resultDir + fileName + "_fastqc.zip");
                taskEntity.setEndAt(new Timestamp(System.currentTimeMillis()));
            } else {
                taskEntity.setStatus("overtime");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;


            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            taskEntity.setStatus("fail");
        } catch (InterruptedException e) {
            taskEntity.setStatus("fail");
        }
        taskDao.saveAndFlush(taskEntity);
        System.out.println("线程结束");
    }

}
