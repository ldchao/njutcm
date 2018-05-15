package cn.edu.nju.njutcm.rna.service;

import cn.edu.nju.njutcm.rna.model.FileEntity;

import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
public interface FileService {

    public Integer addFile(FileEntity fileEntity);

    public List<FileEntity> getByUser(String username);

    public String deleteFileById(Integer id);
}
