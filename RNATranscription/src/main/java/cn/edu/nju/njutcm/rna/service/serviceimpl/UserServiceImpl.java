package cn.edu.nju.njutcm.rna.service.serviceimpl;

import cn.edu.nju.njutcm.rna.dao.UserDao;
import cn.edu.nju.njutcm.rna.model.UserEntity;
import cn.edu.nju.njutcm.rna.service.UserService;
import cn.edu.nju.njutcm.rna.util.GetAddress;
import cn.edu.nju.njutcm.rna.util.PasswordEncryption;
import cn.edu.nju.njutcm.rna.util.UserValidate;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldchao on 2017/10/15.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    private static Log logger = LogFactory.getLog(UserServiceImpl.class);

    public UserVO login(String username, String password, String ip) {
        UserEntity user = userDao.findOne(username);
        UserVO userVO = new UserVO();
        if (user == null) {
            userVO.setLoginMessage("no_user");
        } else {
            String salt = user.getSalt();
            String encryptedPassword = user.getPassword();
            boolean result = false;
            try {
                result = PasswordEncryption.authenticate(password, encryptedPassword, salt);
            } catch (NoSuchAlgorithmException e) {
                logger.error(e);
            } catch (InvalidKeySpecException e) {
                logger.error(e);
            }

            if (result) {
                //获取上次登录信息
                userVO.setUsername(username);
                userVO.setLastLoginTime(user.getLastLoginTime());
                String address = getLastLoginAddress(user.getLastLoginIp());
                userVO.setLastLoginAddress(address);
                userVO.setLoginMessage("success");
                userVO.setLicense(UserValidate.getLicense());

                //更新该账户登录信息
                user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
                user.setLastLoginIp(ip);
                userDao.saveAndFlush(user);
            } else {
                userVO.setLoginMessage("wrong_password");
            }
        }
        return userVO;
    }

    @Override
    public String changePassword(String username, String oldPassword, String newPassword) {
        UserEntity user = userDao.findOne(username);
        String salt = user.getSalt();
        String encryptedPassword = user.getPassword();
        boolean result = false;
        try {
            result = PasswordEncryption.authenticate(oldPassword, encryptedPassword, salt);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        } catch (InvalidKeySpecException e) {
            logger.error(e);
        }

        if (result) {

            String newSalt = null;
            String newEncryptedPassword = null;
            try {
                newSalt = PasswordEncryption.generateSalt();
                newEncryptedPassword = PasswordEncryption.getEncryptedPassword(newPassword, newSalt);
                user.setSalt(newSalt);
                user.setPassword(newEncryptedPassword);
                userDao.saveAndFlush(user);
                return "success";

            } catch (NoSuchAlgorithmException e) {
                logger.error(e);
            } catch (InvalidKeySpecException e) {
                logger.error(e);
            }
            return "fail";
        } else {
            return "wrong_password";
        }
    }

    @Override
    public String addUser(String username, String password) {
        if (userDao.exists(username)) {
            return "username_existed";
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        try {
            String salt = PasswordEncryption.generateSalt();
            String encryptedPassword = PasswordEncryption.getEncryptedPassword(password, salt);
            user.setSalt(salt);
            user.setPassword(encryptedPassword);
            userDao.saveAndFlush(user);
            return "success";
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        } catch (InvalidKeySpecException e) {
            logger.error(e);
        }
        return "fail";
    }

    @Override
    public String deleteUser(String username) {
        if (userDao.exists(username)) {
            userDao.delete(username);
            return "success";
        } else {
            return "user_not_exist";
        }
    }

    @Override
    public List<UserVO> getAllUser() {
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<UserEntity> users = userDao.findAll();
        for (UserEntity user : users) {
            if (user.getUsername().equals("admin")) {
                continue;
            }
            UserVO userVO = new UserVO();
            userVO.setUsername(user.getUsername());
            userVO.setLastLoginTime(user.getLastLoginTime());
            userVO.setLastLoginAddress(user.getLastLoginIp());
            userVOS.add(userVO);
        }
        return userVOS;
    }

    private String getLastLoginAddress(String ip) {
        String address = "未知登录地";
        if (ip == null) {
            address = "暂无登录记录";
        } else {
            String ipParameter = "ip=" + ip;

            try {
                address = GetAddress.getAddresses(ipParameter, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e);
            }
        }
        return address;
    }
}
