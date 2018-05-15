package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@RestController
public class FileController {


    @Autowired
    FileService fileService;

    //查看当前用户的所有文件（按时间排序）
    @GetMapping(value = "/getFileByUser")
    public List<FileEntity> getFileListByUser(HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.getByUser(userVO.getUsername());
    }

    //删除文件
    @DeleteMapping(value = "/deleteFileById")
    public String deleteFile(Integer fileId){
        return fileService.deleteFileById(fileId);
    }


}
