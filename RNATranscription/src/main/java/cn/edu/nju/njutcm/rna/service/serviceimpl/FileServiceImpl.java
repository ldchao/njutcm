package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.service.FileService;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.FileUtil;
import cn.edu.nju.njutcm.rna.vo.FileVO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<FileVO> getByUserAndPath(String username, String relativePath) {
        String rootPath = getUserRootPath(username);
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
            fileVO.setSize(f.length());
            fileVO.setLastModifiedTime(f.lastModified());
            fileVO.setRelativePath(relativePath + File.separator + f.getName());
            fileList.add(fileVO);
        }
        return fileList;
    }

    @Override
    public String deleteFile(String username, String relativePath) {
        String rootPath = getUserRootPath(username);
        String filePath = rootPath + relativePath;
        return FileUtil.delete(filePath) ? "success" : "fail";
    }

    @Override
    public String changeFilePath(String username, String oldPath, String newPath) {

        String rootPath = getUserRootPath(username);
        String oldFilePath = rootPath + oldPath;
        String newFilePath = rootPath + newPath;
        if (!newFilePath.endsWith(File.separator)) {
            newFilePath += File.separator;
        }
        newFilePath += oldFilePath.substring(oldFilePath.lastIndexOf(File.separator) + 1);
        return rename(oldFilePath, newFilePath);
    }

    @Override
    public String renameFile(String username, String oldPath, String newName) {
        String rootPath = getUserRootPath(username);
        String oldFilePath = rootPath + oldPath;
        String newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf(File.separator) + 1) + newName;
        return rename(oldFilePath, newFilePath);
    }

    private String rename(String oldName, String newName) {
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

    @Override
    public String creatDir(String username, String relativePath, String dirName) {
        String rootPath = getUserRootPath(username);
        String parentPath = rootPath + relativePath;
        if (!parentPath.endsWith(File.separator)) {
            parentPath += File.separator;
        }
        String dirPath = parentPath + dirName;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
            return "success";
        }
        return "dirExist";
    }

    @Override
    public List<FileVO> searchFileByContains(String username, String relativePath, String keyWord) {
        String rootPath = getUserRootPath(username);
        int rootPathLength = rootPath.length();
        File file = new File(rootPath + relativePath);
        return searchFiles(file, keyWord.toLowerCase(), true, rootPathLength);
    }

    @Override
    public List<FileVO> searchFileByEndwith(String username, String relativePath, String keyWord) {
        String rootPath = getUserRootPath(username);
        int rootPathLength = rootPath.length();
        File file = new File(rootPath + relativePath);
        return searchFiles(file, keyWord.toLowerCase(), false, rootPathLength);
    }

    private List<FileVO> searchFiles(File folder, final String keyword, final boolean isContains, final int rootPathLength) {
        List<FileVO> result = new ArrayList<FileVO>();
        if (folder.isFile()) {
            if (folder.getName().toLowerCase().contains(keyword)) {
                result.add(fileToFileVO(folder, rootPathLength));
            }
            return result;
        }

        File[] subFolders;
        if (isContains) {
            subFolders = folder.listFiles(file -> {
                if (file.isDirectory() || file.getName().toLowerCase().contains(keyword)) {
                    return true;
                }
                return false;
            });
        } else {

            subFolders = folder.listFiles(file -> {
                if (file.isDirectory() || file.getName().toLowerCase().endsWith(keyword)) {
                    return true;
                }
                return false;
            });
        }

        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    result.add(fileToFileVO(file,rootPathLength));
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchFiles(file, keyword,isContains,rootPathLength));
                }
            }
        }
        return result;
    }

    private FileVO fileToFileVO(final File f, final int rootPathLength) {
        FileVO fileVO = new FileVO();
        fileVO.setFileName(f.getName());
        fileVO.setDir(f.isDirectory());
        fileVO.setSize(f.length());
        fileVO.setLastModifiedTime(f.lastModified());
        fileVO.setRelativePath(f.getAbsolutePath().substring(rootPathLength));
        return fileVO;
    }

    private String getUserRootPath(String username) {
        String rootPath = ApplicationUtil.getInstance().getRootPath() +
                File.separator + "data" + File.separator + username;
        return rootPath;
    }
}
