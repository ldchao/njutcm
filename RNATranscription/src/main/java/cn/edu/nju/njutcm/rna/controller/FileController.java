package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.vo.FileVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    //查看当前用户的所有文件（按时间排序）【不区分层次】
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

    //根据路径，获取路径下所有文件和文件夹
    @GetMapping(value = "/getFile")
    public List<FileVO> getFileList(String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.getByUserAndPath(userVO.getUsername(),relativePath);
    }

//    @GetMapping(value = "/testGetFile")
//    public List<FileVO> testGetFile(){
//        return fileService.getByUserAndPath("admin","\\");
//    }

    //根据相对路径删除文件或文件夹
    @DeleteMapping(value = "/deleteFile")
    public String deleteFile(String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.deleteFile(userVO.getUsername(),relativePath);
    }

    //根据相对路径下载文件或文件夹【参见DownloadController】

    //根据原文件相对路径，将文件或文件夹移动到目的路径下
    @PostMapping(value = "/changeFilePath")
    public String changeFilePath(String oldPath,String newPath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.changeFilePath(userVO.getUsername(),oldPath,newPath);
    }

    //根据相对路径将文件或文件夹重命名
    @PostMapping(value = "/renameFile")
    public String renameFile(String oldPath,String newName ,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.renameFile(userVO.getUsername(),oldPath,newName);
    }

}
