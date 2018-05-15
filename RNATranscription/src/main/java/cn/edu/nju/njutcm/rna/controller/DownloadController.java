package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by ldchao on 2018/5/13.
 */
@RestController
public class DownloadController {

    @Autowired
    TaskService taskService;

    @RequestMapping("/download")
    public String downloadNet(HttpServletResponse response,Integer taskId) {

        InputStream fis = null;
        OutputStream out = null;

        try {
            String path = taskService.getResultPathById(taskId);
            File file = new File(path);
            if (file.exists()) {
                String filename = file.getName();
                fis = new BufferedInputStream(new FileInputStream(file));
                response.reset();
//                response.setContentType("application/x-download");
                response.setContentType("application/x-msdownload");
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "UTF-8"));
                response.addHeader("Content-Length", "" + file.length());
                out = new BufferedOutputStream(response.getOutputStream());
//                response.setContentType("application/octet-stream");
                byte[] buffer = new byte[1024 * 1024];
                int i = -1;
                while ((i = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, i);

                }
                return "success";

            } else {
                return "not find the file";
            }
        } catch (IOException ex) {
            return "not find the file";
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                }catch (IOException e){
                    return "file flow error";
                }

            }
            if (out != null) {

                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    return "file flow error";
                }
            }
        }
    }
}
