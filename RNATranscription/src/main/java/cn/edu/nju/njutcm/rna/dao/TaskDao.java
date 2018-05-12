package cn.edu.nju.njutcm.rna.dao;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
public interface TaskDao extends JpaRepository<TaskEntity,Serializable> {

    public List<TaskEntity> findAllByUserOrderByStartAtDesc(String user);
}
