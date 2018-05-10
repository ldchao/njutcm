package cn.edu.nju.njutcm.rna.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by ldchao on 2018/5/7.
 */
@Controller
public class UrlController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/uploader")
    public String upload(){
        return "webuploader";
    }
}
