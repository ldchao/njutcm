package cn.edu.nju.njutcm.rna.util;

import org.springframework.boot.ApplicationHome;

import java.io.File;

/**
 * Created by ldchao on 2018/5/18.
 */
public class ApplicationUtil {

    private static ApplicationUtil applicationUtil;
    private String rootPath;

    private ApplicationUtil(){
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        rootPath = jarFile.getParentFile().getAbsolutePath();

    }

    public static ApplicationUtil getInstance(){
        if(applicationUtil == null){
            synchronized (ApplicationUtil.class) {
                if(applicationUtil == null)
                    applicationUtil = new ApplicationUtil();
            }
        }
        return applicationUtil;
    }

    public String getRootPath(){
        return rootPath;
    }
}
