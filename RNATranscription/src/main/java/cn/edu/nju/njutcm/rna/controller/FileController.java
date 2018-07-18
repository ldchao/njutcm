package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.vo.FileVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@RestController
public class FileController {


    @Autowired
    FileService fileService;

    /**
     * 根据路径，获取路径下所有文件和文件夹
     * @param relativePath 相对路径，从FileVO中获取，根目录直接传""
     * @param request
     * @return
     */
    @GetMapping(value = "/getFile")
    public List<FileVO> getFileList(String relativePath,HttpServletRequest request){
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            relativePath = "";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.getByUserAndPath(userVO.getUsername(),relativePath);
    }

    /**
     * 根据相对路径删除文件或文件夹
     * @param relativePath 相对路径，从FileVO中获取，根目录直接传""
     * @param request
     * @return
     */
    @DeleteMapping(value = "/deleteFile")
    public String deleteFile(String relativePath,HttpServletRequest request){
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.deleteFile(userVO.getUsername(),relativePath);
    }

    //根据相对路径下载文件或文件夹【参见DownloadController】

    /**
     * 根据原文件相对路径，将文件或文件夹移动到目的路径下
     * @param oldPath 文件相对路径，从FileVO中获取，根目录直接传""
     * @param newPath 目的文件夹相对路径，从FileVO中获取，根目录直接传""
     * @param request
     * @return
     */
    @PostMapping(value = "/changeFilePath")
    public String changeFilePath(String oldPath,String newPath,HttpServletRequest request){
        try {
            oldPath = URLDecoder.decode(oldPath,"utf-8");
            newPath = URLDecoder.decode(newPath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.changeFilePath(userVO.getUsername(),oldPath,newPath);
    }

    /**
     * 根据相对路径将文件或文件夹重命名
     * @param oldPath 文件相对路径，从FileVO中获取，根目录直接传""
     * @param newName 新的文件名
     * @param request
     * @return
     */
    @PostMapping(value = "/renameFile")
    public String renameFile(String oldPath,String newName ,HttpServletRequest request){
        try {
            oldPath = URLDecoder.decode(oldPath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.renameFile(userVO.getUsername(),oldPath,newName);
    }

    /**
     * 在相对路径下新建文件夹
     * @param relativePath 文件相对路径，从FileVO中获取，根目录直接传""
     * @param dirName 文件夹名字
     * @param request
     * @return dirExist或success，分别表示存在重名文件夹，成功
     */
    @PostMapping(value = "/createDir")
    public String createDir(String relativePath,String dirName ,HttpServletRequest request){
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.creatDir(userVO.getUsername(),relativePath,dirName);
    }

    /**
     * 根据文件名后缀是否是关键字，获取路径下所有文件
     * @param relativePath 相对路径，从FileVO中获取，根目录直接传""
     * @param keyWord 检所用关键字
     * @param request
     * @return
     */
    @GetMapping(value = "/searchFileByEndwith")
    public List<FileVO> searchFileByEndwith(String relativePath,String keyWord,HttpServletRequest request){
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            relativePath = "";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.searchFileByEndwith(userVO.getUsername(),relativePath,keyWord);
    }

    /**
     * 根据文件名是否包含关键字，获取路径下所有文件
     * @param relativePath 相对路径，从FileVO中获取，根目录直接传""
     * @param keyWord 检所用关键字
     * @param request
     * @return
     */
    @GetMapping(value = "/searchFileByContains")
    public List<FileVO> searchFileByContains(String relativePath,String keyWord,HttpServletRequest request){
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            relativePath = "";
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return fileService.searchFileByContains(userVO.getUsername(),relativePath,keyWord);
    }
}
