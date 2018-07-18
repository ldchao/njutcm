package cn.edu.nju.njutcm.rna.util;

import cn.edu.nju.njutcm.rna.entity.UploadInfo;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    private static String uploaded(String md5, String guid, String chunk, String chunks, String uploadFolderPath,
                                   String fileName, String name, String ext, HttpServletRequest request) throws Exception {
        synchronized (uploadInfoList) {
            if ((md5 != null && !md5.equals("")) && (chunks != null && !chunks.equals("")) && !isExist(md5, chunk)) {
                uploadInfoList.add(new UploadInfo(md5, chunks, chunk, uploadFolderPath, fileName, ext));
            }
        }
        boolean allUploaded = isAllUploaded(md5, chunks);
        int chunksNumber = Integer.parseInt(chunks);

        if (allUploaded) {
            return mergeFile(chunksNumber, name,ext, guid, uploadFolderPath, request);
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
    private static String mergeFile(int chunksNumber, String name, String ext, String guid, String uploadFolderPath,
                                    HttpServletRequest request) {
        /* 合并输入流 */
        String mergePath = uploadFolderPath;

        String destPath = getDestPath(request);// 文件路径
        String newName = getUploadFileName(destPath,name,ext);// 文件新名称

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

    public static String saveFileInBlock(String guid, String md5value, String chunks, String chunk,String name, String ext, MultipartFile file, String id,
                                         HttpServletRequest request) throws Exception {

        String mergePath = ApplicationUtil.getInstance().getRootPath() + File.separator + "data"
                + File.separator + getUsername(request) + File.separator + id + File.separator;
        // 将文件分块保存到临时文件夹里，便于之后的合并文件
        String fileName = chunk + ext;
        saveFile(mergePath, fileName, file, request);
        // 验证所有分块是否上传成功，成功的话进行合并
        return uploaded(md5value, guid, chunk, chunks, mergePath, fileName, name, ext, request);
    }

    public static String saveFileNotInBlock(String name,String ext, MultipartFile file, HttpServletRequest request) throws Exception {
        String destPath = getDestPath(request);
        String newName = getUploadFileName(destPath,name,ext);// 文件新名称

        // 上传文件没有分块的话就直接保存目标目录
        saveFile(destPath, newName, file, request);
        return destPath + newName;
    }

    private static String getDestPath(HttpServletRequest request) {

        String relativePath= null;
        try {
            relativePath = URLDecoder.decode(request.getParameter("relativePath"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            relativePath = "";
        }
        String destPath = ApplicationUtil.getInstance().getRootPath() + File.separator
                + "data" + File.separator+ getUsername(request) + relativePath + File.separator ;// 文件路径
        return destPath;
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


    private static String getUploadFileName(String path, String name, String ext){
        if (!path.endsWith(File.separator))
            path = path + File.separator;
        makeSureDirExist(path);
        File dirFile=new File(path);
        File[] files = dirFile.listFiles();
        ArrayList<String> fileNames=new ArrayList<String>();
        for (File f:files) {
            fileNames.add(f.getName());
        }

        int index=1;
        String fileName = name.substring(0,name.lastIndexOf("."));
        while(fileNames.contains(name)){
            name=fileName + "_" + index+ext;
            index++;
        }
        return name;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName  要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtil.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

}
