package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.service.UserService;
import cn.edu.nju.njutcm.rna.util.GetClientMessageUtils;
import cn.edu.nju.njutcm.rna.util.UserValidate;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldchao on 2017/10/15.
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    //校验用户权限
    @GetMapping(value = "/validate")
    public String validate(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "false";
        }
        if (request.getSession(false).getAttribute("User") != null) {
            UserVO userVO = (UserVO) request.getSession().getAttribute("User");
            if (UserValidate.validate(userVO.getLicense())) {
                return "true";
            }

        }
        return "false";
    }

    //登录
    @PostMapping(value = "/login")
    public String login(String username, String password, HttpServletRequest request) {

        UserVO user = userService.login(username, password, GetClientMessageUtils.getIpAddr(request));
        if (user.getLoginMessage().equalsIgnoreCase("success")) {
            if (request.getSession(false) != null)
                request.getSession(false).invalidate();
            request.getSession(true);
            request.getSession().setAttribute("User", user);
        }
        return user.getLoginMessage();
    }

    //注销
    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession(false).removeAttribute("User");
        request.getSession(false).invalidate();
        return "logout_success";
    }

    //修改当前用户密码
    @PutMapping(value = "/changePassword")
    public String changePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        UserVO userVO = (UserVO) request.getSession(false).getAttribute("User");
        return userService.changePassword(userVO.getUsername(), oldPassword, newPassword);
    }

    //查看当前用户信息
    @GetMapping(value = "/getUserMessage")
    public UserVO getUserMessage(HttpServletRequest request) {
        UserVO userVO = (UserVO) request.getSession(false).getAttribute("User");
        return userVO;
    }

    //获取所有用户列表(只有admin用户有权限)
    @GetMapping(value = "/getAllUser")
    public List<UserVO> getAllUser(HttpServletRequest request) {
        UserVO userVO = (UserVO) request.getSession(false).getAttribute("User");
        if (userVO.getUsername().equals("admin")) {
            return userService.getAllUser();
        } else {
            return new ArrayList<UserVO>();
        }

    }

    //增加用户（只有admin用户有权限）
    @PostMapping(value = "/addUser")
    public String addUser(HttpServletRequest request, String username, String password) {
        UserVO userVO = (UserVO) request.getSession(false).getAttribute("User");
        if (userVO.getUsername().equals("admin")) {
            return userService.addUser(username, password);
        } else {
            return "no_permission";
        }
    }

    //删除用户（只有admin用户有权限）
    @DeleteMapping(value = "/deleteUser")
    public String deleteUser(HttpServletRequest request, String username) {
        UserVO userVO = (UserVO) request.getSession(false).getAttribute("User");
        if (userVO.getUsername().equals("admin")) {
            return userService.deleteUser(username);
        } else {
            return "no_permission";
        }
    }
}
