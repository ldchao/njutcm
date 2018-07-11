package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.FileDao;
import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.FileUtil;
import cn.edu.nju.njutcm.rna.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileDao fileDao;

    @Override
    public Integer addFile(FileEntity fileEntity) {
        FileEntity result = fileDao.saveAndFlush(fileEntity);
        return result.getId();
    }

    @Override
    public List<FileEntity> getByUser(String username) {
        return fileDao.findAllByUserOrderByUploadAtDesc(username);
    }

    @Override
    public String deleteFileById(Integer id) {
        FileEntity fileEntity = fileDao.findOne(id);
        String path = fileEntity.getSavepath();
        if (FileUtil.delete(path)) {
            fileDao.delete(fileEntity);
            return "success";
        } else {
            return "fail";
        }
    }

    @Override
    public List<FileVO> getByUserAndPath(String username, String relativePath) {
        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator + "data"+ File.separator + username;
        String dirPath = rootPath + relativePath;
        if (!dirPath.endsWith(File.separator))
            dirPath = dirPath + File.separator;
        System.out.println(dirPath);
        File dirFile = new File(dirPath);
        ArrayList<FileVO> fileList = new ArrayList<FileVO>();
        if (!dirFile.isDirectory()) {
            return fileList;
        }
        File[] files = dirFile.listFiles();
        for (File f : files) {
            FileVO fileVO = new FileVO();
            fileVO.setFileName(f.getName());
            fileVO.setDir(f.isDirectory());
            fileVO.setRelativePath(relativePath + File.separator + f.getName());
            fileList.add(fileVO);
        }
        return fileList;
    }

    @Override
    public String deleteFile(String username, String relativePath) {
        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator + "data"+ File.separator + username;
        String filePath = rootPath + relativePath;
        return FileUtil.delete(filePath) ? "success" : "fail";
    }

    @Override
    public String changeFilePath(String username, String oldPath, String newPath) {

        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator + "data"+ File.separator + username;
        String oldFilePath = rootPath + oldPath;
        String newFilePath = rootPath + newPath;
        if(!newFilePath.endsWith(File.separator)){
            newFilePath += File.separator;
        }
        newFilePath += oldFilePath.substring(oldFilePath.lastIndexOf(File.separator)+1);
        return rename(oldFilePath,newFilePath);
    }

    @Override
    public String renameFile(String username, String oldPath, String newName) {
        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator + "data"+ File.separator + username;
        String oldFilePath = rootPath + oldPath;
        String newFilePath = oldFilePath.substring(0,oldFilePath.lastIndexOf(File.separator)+1)+newName;
        return rename(oldFilePath,newFilePath);
    }

    private String rename(String oldName,String newName){
        String result;
        try {
            File afile = new File(oldName);
            if (afile.renameTo(new File(newName))) {
                result = "success";
            } else {
                result = "fail";
            }
        } catch (Exception e) {
            result = "fail";
        }
        return result;
    }

}
