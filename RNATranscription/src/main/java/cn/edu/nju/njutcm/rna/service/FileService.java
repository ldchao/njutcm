package cn.edu.nju.njutcm.rna.service;

import cn.edu.nju.njutcm.rna.vo.FileVO;

import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
public interface FileService {

    public List<FileVO> getByUserAndPath(String username,String relativePath);

    public String deleteFile(String username,String relativePath);

    public String changeFilePath(String username,String oldPath,String newPath);

    public String renameFile(String username,String oldPath,String newName);

    String creatDir(String username,String relativePath,String dirName);

    List<FileVO> searchFileByContains(String username,String relativePath,String keyWord);

    List<FileVO> searchFileByEndwith(String username,String relativePath,String keyWord);


}
