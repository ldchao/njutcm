package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.ZipUtil;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by ldchao on 2018/5/13.
 */
@RestController
public class DownloadController {

    @Autowired
    TaskService taskService;

    @RequestMapping("/download")
    public String downloadNet(HttpServletResponse response,Integer taskId) {

        TaskEntity taskEntity = taskService.getTaskEntityById(taskId);
        String resultFile=taskEntity.getResultFile();
        File file = new File(resultFile);
        String result;
        if(file.isDirectory()){
            String path=taskService.zipResult(taskEntity.getResultFile(),taskEntity.getTaskName()); //打包结果文件
            result = downloadUtil(response,path);
            taskService.deleteZipResult(path); //将打包的结果文件删除掉
        }else{
            result = downloadUtil(response,resultFile);
        }

        return result;
    }

    @RequestMapping("/downloadFile")
    public String downloadFile(String relativePath, HttpServletRequest request,HttpServletResponse response) {
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        String rootPath = ApplicationUtil.getInstance().getRootPath()+ File.separator + "data"+ File.separator + userVO.getUsername();
        String path = rootPath + relativePath;
        File file = new File(path);
        String result;
        if(file.isDirectory()){
            String zipPath = rootPath + File.separator + "tmp"
                + File.separator + UUID.randomUUID().toString().replace("-","");
            String zipFileName = relativePath.substring(relativePath.lastIndexOf(File.separator)+1);
            ZipUtil.fileToZip(path,zipPath,zipFileName);
            zipPath = zipPath + File.separator + zipFileName + ".zip";
            result = downloadUtil(response,zipPath);
            taskService.deleteZipResult(zipPath); //将打包的文件删除掉
        }else{
            result = downloadUtil(response,path);
        }
        return result;
    }

    @RequestMapping("/downloadExample")
    public String downloadExample(HttpServletResponse response,Integer appNum) {

        String exampleRootPath = "/home/sample/";
        String[] examplePaths={"QC.zip","filter.zip",
                "cufflinks.zip","count.zip",
                "DESeq2.zip","edgeR.zip",
                "GO.zip","KEGG.zip",
                "PCA.zip","3DPCA.zip",
                "pheatmap.zip","Volcano.zip",
                "venn.zip","IDconvert.zip",
                "pie.zip","matrix.zip"};
        return downloadUtil(response,exampleRootPath+examplePaths[appNum-1]);
    }

    private String downloadUtil(HttpServletResponse response,String path) {

        InputStream fis = null;
        OutputStream out = null;

        try {
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
