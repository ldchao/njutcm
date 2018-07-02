package cn.edu.nju.njutcm.rna.dao;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
public interface TaskDao extends JpaRepository<TaskEntity,Serializable> {

    List<TaskEntity> findAllByUserOrderByStartAtDesc(String user);

//    @Query("Select count(*) from TaskEntity t where t.user = ?1 and t.taskName = ?2")
    int countByUserEqualsAndTaskNameEquals(String user,String taskName);
}
