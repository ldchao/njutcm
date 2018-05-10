package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.ldchao.fileupload.util.FileUtil;
import cn.edu.nju.ldchao.fileupload.vo.UploadVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ldchao on 2018/5/7.
 */
@RestController
public class UploadController {
    /**
     *
     * @Description:
     *             接受文件分片，合并分片
     * @param guid
     *             可省略；每个文件有自己唯一的guid，后续测试中发现，每个分片也有自己的guid，所以不能使用guid来确定分片属于哪个文件。
     * @param md5value
     *             文件的MD5值
     * @param chunks
     *             当前所传文件的分片总数
     * @param chunk
     *             当前所传文件的当前分片数
     * @param id
     *             文件ID，如WU_FILE_1，后面数字代表当前传的是第几个文件,后续使用此ID来创建临时目录，将属于该文件ID的所有分片全部放在同一个文件夹中
     * @param name
     *             文件名称，如07-中文分词器和业务域的配置.avi
     * @param type
     *             文件类型，可选，在这里没有用到
     * @param lastModifiedDate 文件修改日期，可选，在这里没有用到
     * @param size  文件的总大小
     * @param file  当前所传分片
     * @return
     * @author: ldchao
     * @date: 2018/05/07
     */
    @RequestMapping(value = "/BigFileUp")
    public String fileUpload(String guid, String md5value, String chunks, String chunk, String id, String name,
                             String type, String lastModifiedDate, String size, MultipartFile file,HttpServletRequest request) {
        System.out.println("guid:"+guid+",md5value:"+md5value+",chunks:"+chunks+",chunk:"+chunk+
                ",id:"+id+",name:"+name+",type:"+type+",lastModifiedDate:"+lastModifiedDate+",size:"+size);
        String fileName;
        UploadVO result=new UploadVO();
        try {
            int index;
            String uploadFolderPath = FileUtil.getRealPath(request);

            String mergePath = uploadFolderPath + "\\fileDate\\" + id + "\\";
            String ext = name.substring(name.lastIndexOf("."));

            // 判断文件是否分块
            if (chunks != null && chunk != null) {
                index = Integer.parseInt(chunk);
                fileName = String.valueOf(index) + ext;
                // 将文件分块保存到临时文件夹里，便于之后的合并文件
                FileUtil.saveFile(mergePath, fileName, file, request);
                // 验证所有分块是否上传成功，成功的话进行合并
                FileUtil.Uploaded(md5value, guid, chunk, chunks, mergePath, fileName, ext, request);
            } else {
                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                SimpleDateFormat m = new SimpleDateFormat("MM");
                SimpleDateFormat d = new SimpleDateFormat("dd");
                Date date = new Date();
                String destPath = uploadFolderPath + "\\fileDate\\" + "video" + "\\" + year.format(date) + "\\"
                        + m.format(date) + "\\" + d.format(date) + "\\";// 文件路径
                String newName = System.currentTimeMillis() + ext;// 文件新名称
                // fileName = guid + ext;
                // 上传文件没有分块的话就直接保存目标目录
                FileUtil.saveFile(destPath, newName, file, request);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMsg("上传失败");
            result.setData( null);
            return result.toString();
        }
        result.setCode(1);
        result.setMsg("上传成功");
        return result.toString();
    }

    @RequestMapping(value = "/blockIsExist")
    public Boolean blockIsExist(String md5value, String chunk) {
        return FileUtil.isNotExist(md5value,chunk);
    }


}
