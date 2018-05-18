package cn.edu.nju.njutcm.rna.util;

import cn.edu.nju.njutcm.rna.entity.UploadInfo;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//import com.ibot.gridtraining.entity.DataFile;

/**
 * Created by ldchao on 2018/5/7.
 */
public class FileUtil {


    private static boolean saveFile(String savePath, String fileFullName, MultipartFile file, HttpServletRequest request)
            throws Exception {
        byte[] data = readInputStream(file.getInputStream());
        // new一个文件对象用来保存图片，默认保存当前工程根目录
        File uploadFile = new File(savePath + fileFullName);
        // 判断文件夹是否存在，不存在就创建一个

        createDir(savePath);

        // 创建输出流
        try (FileOutputStream outStream = new FileOutputStream(uploadFile)) {// 写入数据
            outStream.write(data);
            outStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return uploadFile.exists();
    }

    private static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    private final static List<UploadInfo> uploadInfoList = new ArrayList<>();

    private static String Uploaded(String md5, String guid, String chunk, String chunks, String uploadFolderPath,
                                   String fileName, String ext, HttpServletRequest request) throws Exception {
        synchronized (uploadInfoList) {
            if ((md5 != null && !md5.equals("")) && (chunks != null && !chunks.equals("")) && !isExist(md5, chunk)) {
                uploadInfoList.add(new UploadInfo(md5, chunks, chunk, uploadFolderPath, fileName, ext));
            }
        }
        boolean allUploaded = isAllUploaded(md5, chunks);
        int chunksNumber = Integer.parseInt(chunks);

        if (allUploaded) {
            return mergeFile(chunksNumber, ext, guid, uploadFolderPath, request);
        }
        return null;
    }

    //判断在uploadInfoList是否有存在MD5和chunk都相同的元素
    public static boolean isExist(final String md5, final String chunk) {
        boolean flag = false;
        for (UploadInfo uploadInfo : uploadInfoList) {
            if (uploadInfo.getChunk().equals(chunk) && uploadInfo.getMd5().equals(md5)) {
                //若md5和chunk都相同，则认为两条记录相同，返回true
                flag = true;
            }
        }
        return flag;
    }

    private static boolean isAllUploaded(final String md5, String chunks) {
        int size = uploadInfoList.stream().filter(new Predicate<UploadInfo>() {
            @Override
            public boolean test(UploadInfo item) {
                return item.getMd5().equals(md5);
            }
        }).distinct().collect(Collectors.toList()).size();
        boolean bool = (size == Integer.parseInt(chunks));
        if (bool) {
            synchronized (uploadInfoList) {
                uploadInfoList.removeIf(new Predicate<UploadInfo>() {
                    @Override
                    public boolean test(UploadInfo item) {
                        return Objects.equals(item.getMd5(), md5);
                    }
                });
            }
        }
        return bool;
    }

    @SuppressWarnings("resource")
    private static String mergeFile(int chunksNumber, String ext, String guid, String uploadFolderPath,
                                    HttpServletRequest request) {
        /* 合并输入流 */
        String mergePath = uploadFolderPath;

        String destPath = getDestPath(request);// 文件路径
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ext;// 文件新名称

        SequenceInputStream s;
        InputStream s1;
        try {
            s1 = new FileInputStream(mergePath + 0 + ext);
            String tempFilePath;
            InputStream s2 = new FileInputStream(mergePath + 1 + ext);
            s = new SequenceInputStream(s1, s2);
            for (int i = 2; i < chunksNumber; i++) {
                tempFilePath = mergePath + i + ext;
                InputStream s3 = new FileInputStream(tempFilePath);
                s = new SequenceInputStream(s, s3);
            }
            // 通过输出流向文件写入数据(转移正式文件到目标目录)
            // uploadFolderPath + guid + ext
            saveStreamToFile(s, destPath, newName);
            // 删除保存分块文件的文件夹
            deleteFolder(mergePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destPath + newName;

    }

    private static boolean deleteFolder(String mergePath) {
        File dir = new File(mergePath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dir.delete();
    }

    private static void saveStreamToFile(SequenceInputStream inputStream, String filePath, String newName)
            throws Exception {
        createDir(filePath);

		/* 创建输出流，写入数据，合并分块 */
        OutputStream outputStream = new FileOutputStream(filePath + newName);
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private static void createDir(String savePath) throws Exception {
        File fileDirectory = new File(savePath);
        synchronized (fileDirectory) {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdirs()) {
                    throw new Exception("保存文件的父文件夹创建失败！路径为：" + fileDirectory);
                }
            }
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdirs()) {
                    throw new Exception("文件夹创建失败！路径为：" + fileDirectory);
                }
            }
        }
    }

    public static String savaFileInBlock(String guid, String md5value, String chunks, String chunk, String ext, MultipartFile file, String id,
                                         HttpServletRequest request) throws Exception {

        String mergePath = ApplicationUtil.getInstance().getRootPath() + File.separator + "fileData"
                + File.separator + getUsername(request) + File.separator + id + File.separator;
        // 将文件分块保存到临时文件夹里，便于之后的合并文件
        String fileName = chunk + ext;
        saveFile(mergePath, fileName, file, request);
        // 验证所有分块是否上传成功，成功的话进行合并
        return Uploaded(md5value, guid, chunk, chunks, mergePath, fileName, ext, request);
    }

    public static String savaFileNotInBlock(String ext, MultipartFile file, HttpServletRequest request) throws Exception {
        String destPath = getDestPath(request);
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ext;// 文件新名称

        // 上传文件没有分块的话就直接保存目标目录
        saveFile(destPath, newName, file, request);
        return destPath + newName;
    }

    private static String getDestPath(HttpServletRequest request) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        String destPath = ApplicationUtil.getInstance().getRootPath() + File.separator
                + "fileData" + File.separator+ getUsername(request) + File.separator
                + simpleDateFormat.format(date) + File.separator;// 文件路径
        return destPath;
    }

    public static boolean deleteUploadFile(String filePath) {
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

    public static void makeSureDirExist(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static String getUsername(HttpServletRequest request) {
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return userVO == null ? "illegal" : userVO.getUsername();
    }

}
