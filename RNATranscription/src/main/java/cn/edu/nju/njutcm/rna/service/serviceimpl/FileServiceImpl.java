package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.FileDao;
import cn.edu.nju.njutcm.rna.model.FileEntity;
import cn.edu.nju.njutcm.rna.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}