package cn.edu.nju.njutcm.rna.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ldchao on 2018/6/14.
 */
public class ZipUtil {

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath    :压缩后存放路径
     * @param fileName       :压缩后文件的名称
     * @return
     */
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        FileUtil.makeSureDirExist(zipFilePath);

        if (sourceFile.exists() == false) {
            System.out.println("待压缩的文件目录：" + sourceFilePath + "不存在.");
        } else {
            try {
                File zipFile = new File(zipFilePath + File.separator + fileName + ".zip");
                if (zipFile.exists()) {
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                        zipFile.createNewFile();
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (int i = 0; i < sourceFiles.length; i++) {
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                //关闭流
                try {
                    if (null != bis) bis.close();
                    if (null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    public static boolean deleteZipFile(String filePath) {
        String dirPath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
        return deleteFile(filePath) && deleteDir(dirPath);
    }

    private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {

            if (!file.delete()) {
                return false;
            }
        }
        return true;
    }

    private static boolean deleteDir(String dirPath) {
        File file = new File(dirPath);

        if (file.exists()) {
            if (file.listFiles().length == 0) {
                if (!file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

//    public static void main(String[] args){
//        String sourceFilePath = "D:\\upload\\fileDate\\20180512";
//        String zipFilePath = "D:\\upload\\tmp";
//        String fileName = "20180512";
//        boolean flag = ZipUtil.fileToZip(sourceFilePath, zipFilePath, fileName);
//        if(flag){
//            System.out.println("文件打包成功!");
//        }else{
//            System.out.println("文件打包失败!");
//        }
//    }
}
