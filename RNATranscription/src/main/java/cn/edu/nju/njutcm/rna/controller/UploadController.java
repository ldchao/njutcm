package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.util.FileUtil;
import cn.edu.nju.njutcm.rna.vo.UploadVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ldchao on 2018/5/7.
 */
@RestController
public class UploadController {

    @Autowired
    FileService fileService;

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
        UploadVO result=new UploadVO();
        try {
            String ext = name.substring(name.lastIndexOf("."));

            String destPath = null;
            // 判断文件是否分块
            if (chunks != null && chunk != null) {
                destPath = FileUtil.savaFileInBlock(guid,md5value,chunks,chunk,ext, file, id, request);

            } else {
                destPath = FileUtil.savaFileNotInBlock(ext,file,request);
            }
            if(destPath!=null){
                FileEntity fileEntity=new FileEntity();
                fileEntity.setFileName(name);
                UserVO userVO = (UserVO) request.getSession().getAttribute("User");
                fileEntity.setUser(userVO.getUsername());
                fileEntity.setUser("test");
                fileEntity.setSize(Double.valueOf(size));
                fileEntity.setSavepath(destPath);
                fileEntity.setUploadAt(new Timestamp(System.currentTimeMillis()));
                fileService.addFile(fileEntity);
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
        return FileUtil.isExist(md5value,chunk);
    }


}
