package cn.edu.nju.njutcm.rna.dao;

import cn.edu.nju.njutcm.rna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

/**
 * Created by ldchao on 2017/10/15.
 */
public interface UserDao extends JpaRepository<User,Serializable> {

}
