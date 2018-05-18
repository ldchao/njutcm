package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ldchao on 2018/5/7.
 */
@Controller
public class UrlController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/uploader")
    public String upload() {
        return "webuploader";
    }
//
//    @GetMapping("/testPath")
//    @ResponseBody
//    public String testPath() {
//        return ApplicationUtil.getInstance().getRootPath();
//    }
}
