package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.FileUtil;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@RestController
public class TaskController {

    @Autowired
    TaskService taskService;

    //根据id查询任务状态
    @GetMapping("/getStatusById")
    public String getStatusById(Integer id){
        return taskService.getStatusById(id);
    }

    //根据id重新运行任务
    @GetMapping("/reStartTaskById")
    public String reStartTaskById(Integer id){
        return taskService.reStartTaskById(id);
    }

    //根据id删除任务
    @GetMapping("/deleteTaskById")
    public String deleteTaskById(Integer id){
        return taskService.deleteTaskById(id);
    }

    //获取所有任务列表
    @GetMapping("/getAllTaskByUser")
    public List<TaskVO> getAllTaskByUser(HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return taskService.getAllTaskByUser(userVO.getUsername());
    }

    //根据任务名是否包含关键字获取所有任务列表
    @GetMapping("/searchTask")
    public List<TaskVO> searchTask(HttpServletRequest request,String keyWord){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return taskService.searchTask(userVO.getUsername(),keyWord);
    }

    /**
     * 1-fastQC
     * ##命令行：sh fastqc.sh 1.txt 1 24
     * ##1.txt:质检文件
     * ##1: fq or fq.gz(1:fq,2:fq.gz)
     * ##24:线程数
     * @param taskName 任务名
     * @param threadNum 线程数
     * @param relativePath 质检文件【选择文件，FileVO中获取】
     * @param fileType 文件类型【1:fq,2:fq.gz】
     * @param request
     * @return
     */
    @PostMapping("/createFastQCTask")
    public String createFastQCTask(String taskName,String threadNum,String relativePath,String fileType,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        System.out.println(relativePath);
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        System.out.println(relativePath);
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;
//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "sh /home/soft/fastqc.sh "+inputFilePath+" "+fileType+" "+threadNum;
        resultDir += "QC.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("fastqc");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 2-filter
     * ##命令行：sh filter.sh 1 2 36 filter.txt
     * ##1:软件选择 1：trimmomatic 2：NGSQC
     * ##2:单端还是双端  1：单端  2：双端
     * ##36：线程数
     * ##filter.txt：txt文件
     * @param taskName 任务名
     * @param software 软件选择【1：trimmomatic 2：NGSQC】
     * @param seqType 单端还是双端【1：单端  2：双端】
     * @param relativePath 过滤文件相对路径【选择文件，FileVO中获取】
     * @param threadNum 线程数
     * @param request
     * @return
     */
    @PostMapping("/createTrimmomatic2Task")
    public String createTrimmomatic2Task(String taskName,String software,String seqType,String relativePath,String threadNum,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd+="sh /home/soft/filter.sh "+software+" "+seqType+" "+threadNum+" "+inputFilePath;
        resultDir += "filter" + File.separator;
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("filter");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 3-cufflinks
     * ##命令行：sh cufflinks.sh cfff.txt 2 48 mouse x1 x2 1
     * ##cuff.txt:测序文件
     * ##2:每组样品数
     * ##48：线程数
     * ##mouse:基因组(mouse,human,rat)
     * ##x1：untreated组名
     * ##x2：treated组名
     * ##1:是否选择构建新转录本（1否；2是）
     * @param taskName 任务名
     * @param relativePath 测序文件相对路径【选择文件，FileVO中获取】
     * @param sampleNum 每组样品数
     * @param threadNum 线程数
     * @param gene 基因组(mouse,human,rat三种)
     * @param untreated untreated组名
     * @param treated treated组名
     * @param newTranscript 是否选择构建新转录本【1否；2是】
     * @param request
     * @return
     */
    @PostMapping("/createCufflinksTask")
    public String createTask(String taskName,String relativePath,int sampleNum,String threadNum,String gene,String untreated,String treated,String newTranscript,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath = getUserRootPath(username) + relativePath;
//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "sh /home/soft/cufflinks.sh "+inputFilePath+" "+sampleNum+" "+threadNum+" "+gene+" "+untreated+" "+treated+" "+newTranscript;
        resultDir += "cufflinks.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("cufflinks");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 4-count
     * ##命令行：sh count.sh 2 48 mouse A1_1.fq A1_2.fq x5
     * ##2: 双端测序还是单端测序(1,2)
     * ##48: 线程数
     * ##mouse：物种
     * ##A1_1.fq：左端测序
     * ##A1_2.fq：右端测序
     * ##x5:生成文件命名
     * @param taskName 任务名
     * @param seqType 单端还是双端【1：单端  2：双端】
     * @param threadNum 线程数
     * @param specie 物种【用户自己填写即可】
     * @param relativePath1 左端测序【选择文件，FileVO中获取】
     * @param relativePath2 右端测序(单端测序只有一个测序文件)【选择文件，FileVO中获取】
     * @param fileName 生成文件命名
     * @param request
     * @return
     */
    @PostMapping("/createCountTask")
    public String createCountTask(String taskName,String seqType,String threadNum,String specie,String relativePath1,String relativePath2,String fileName,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath1 = URLDecoder.decode(relativePath1,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath1 = getUserRootPath(username) + relativePath1;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath1);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";

        if(seqType.equals("1")){
            cmd +="sh /home/soft/count.sh 1 "+threadNum+" "+specie+" "+inputFilePath1+" "+fileName;
        }else{
            try {
                relativePath2 = URLDecoder.decode(relativePath2,"utf-8");
            } catch (UnsupportedEncodingException e) {
                return "fail";
            }
            String inputFilePath2 = getUserRootPath(username) + relativePath2;
            cmd +="sh /home/soft/count.sh 2 "+threadNum+" "+specie+" "+inputFilePath1+" "+inputFilePath2+" "+fileName;
        }
        resultDir += "count.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("count");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 5-DESeq2
     * ##命令行：Rscript DESeq2.R desmerged.txt descondition.txt 2 0.5
     * ##desmerged.txt：表达矩阵
     * ##descondition.txt：条件文件
     * ##2：物种选择（1：人类，2：小鼠，3：大鼠）
     * ##0.5：qvalue设置
     * @param taskName 任务名
     * @param matrix 表达矩阵相对路径【选择文件，FileVO中获取】
     * @param conditionFile 条件文件相对路径【选择文件，FileVO中获取】
     * @param specie 物种选择【1：人类，2：小鼠，3：大鼠】
     * @param qValue qvalue设置
     * @param request
     * @return
     */
    @PostMapping("/createDESeq2Task")
    public String createDESeq2Task(String taskName,String matrix,String conditionFile,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            matrix = URLDecoder.decode(matrix,"utf-8");
            conditionFile = URLDecoder.decode(conditionFile,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(matrixPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/DESeq2.R "+matrixPath+" "+
                conditionFilePath+" "+specie+" "+qValue;
        resultDir += "DESeq2.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("DESeq2");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 6-edgeR
     * ##命令行：Rscript edgeR.R edrmerged.txt edrcondition.txt 2 0.5
     * ##edrmerged.txt：表达矩阵
     * ##edrcondition.txt：条件文件
     * ##2：物种选择（1：人类，2：小鼠，3：大鼠）
     * ##0.5：qvalue设置
     * @param taskName 任务名
     * @param matrix 表达矩阵相对路径【选择文件，FileVO中获取】
     * @param conditionFile 条件文件相对路径【选择文件，FileVO中获取】
     * @param specie 物种选择【1：人类，2：小鼠，3：大鼠】
     * @param qValue qvalue设置
     * @param request
     * @return
     */
    @PostMapping("/createEdgeRTask")
    public String createEdgeRTask(String taskName,String matrix,String conditionFile,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            matrix = URLDecoder.decode(matrix,"utf-8");
            conditionFile = URLDecoder.decode(conditionFile,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(matrixPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/edgeR.R "+matrixPath+" "+
                conditionFilePath+" "+specie+" "+qValue;
        resultDir += "edgeR.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("edgeR");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 7-GO
     * ##命令行：Rscript GO.R GO.txt 2 SYMBOL 0.9
     * ##GO.txt : gene文件
     * ##2： 物种选择（1：人类,2：小鼠,3：大鼠）
     * ##SYMBOL：基因数据类型
     * ##0.9：qvalue设置
     * @param taskName 任务名
     * @param relativePath gene文件相对路径【选择文件，FileVO中获取】
     * @param geneType 基因数据类型
     * @param specie 物种选择【1：人类，2：小鼠，3：大鼠】
     * @param qValue qvalue设置
     * @param request
     * @return
     */
    @PostMapping("/createGOTask")
    public String createGOTask(String taskName,String relativePath,String geneType,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //gene文件
        String genePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(genePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/GO.R "+genePath+" "+
                specie+" "+ geneType +" "+qValue;
        resultDir += "GO.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("GO");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 8-KEGG
     * ##命令行：Rscript KEGG.R KEGG.txt 2 SYMBOL 0.9
     * ##KEGG.txt : 基因文件
     * ##2 : 物种选择（1,2,3）
     * ##SYMBOL ： 基因类型
     * ##0.9 ： qvalue设置
     * @param taskName 任务名
     * @param relativePath 基因文件相对路径【选择文件，FileVO中获取】
     * @param geneType 基因类型
     * @param specie 物种选择（1：人类，2：小鼠，3：大鼠）
     * @param qValue qvalue设置
     * @param request
     * @return
     */
    @PostMapping("/createKEGGTask")
    public String createKEGGTask(String taskName,String relativePath,String geneType,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //gene文件
        String genePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(genePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/KEGG.R "+genePath+" "+
                specie+" "+ geneType +" "+qValue;
        resultDir += "KEGG.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("KEGG");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 9-PCA
     * ##命令行：Rscript PCA.R pcaexpress.txt pcacondition.txt
     * ##pcaexpress.txt：表达矩阵
     * ##pcacondition.txt：条件文件
     * @param taskName 任务名
     * @param matrix 表达矩阵相对路径【选择文件，FileVO中获取】
     * @param conditionFile 条件文件相对路径【选择文件，FileVO中获取】
     * @param request
     * @return
     */
    @PostMapping("/createPCATask")
    public String createPCATask(String taskName,String matrix,String conditionFile,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            matrix = URLDecoder.decode(matrix,"utf-8");
            conditionFile = URLDecoder.decode(conditionFile,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(matrixPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/PCA.R "+matrixPath+" "+conditionFilePath;
        resultDir += "PCA.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("PCA");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 10-3DPCA
     * ##命令行：Rscript 3DPCA.R 3dexpress.txt 3dcondition.txt
     * ##3dexpress.txt：表达矩阵
     * ##3dcondition.txt：条件文件
     * @param taskName 任务名
     * @param matrix 表达矩阵相对路径【选择文件，FileVO中获取】
     * @param conditionFile 条件文件相对路径【选择文件，FileVO中获取】
     * @param request
     * @return
     */
    @PostMapping("/create3DPCATask")
    public String create3DPCATask(String taskName,String matrix,String conditionFile,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            matrix = URLDecoder.decode(matrix,"utf-8");
            conditionFile = URLDecoder.decode(conditionFile,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(matrixPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/3DPCA.R "+matrixPath+" "+conditionFilePath;
        resultDir += "3DPCA.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("3DPCA");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 11-pheatmap
     * ##命令行：Rscript pheatmap.R pheatmap.txt row green white black 1
     * ##pheatmap.txt：rpkm表达矩阵
     * ##row:标准化（row,column,none,一般推荐row）
     * ##green :颜色
     * ##white ：颜色
     * ##black：颜色
     * ##1：样品是否聚类（1：聚类，2：不聚类）
     * @param taskName 任务名
     * @param matrix rpkm表达矩阵相对路径【选择文件，FileVO中获取】
     * @param standard 标准化
     * @param color1 第一种颜色【颜色给文本框，用户填写即可】
     * @param color2 第二种颜色
     * @param color3 第三种颜色
     * @param isCluster 样品是否聚类（1：聚类，2：不聚类）
     * @param request
     * @return
     */
    @PostMapping("/createPheatmapTask")
    public String createPheatmapTask(String taskName,String matrix,String standard,String color1,String color2,String color3,String isCluster,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            matrix = URLDecoder.decode(matrix,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(matrixPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/pheatmap.R "+matrixPath+" "+standard+" "+color1+" "+color2+" "+color3+" "+isCluster;
        resultDir += "pheatmap.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("pheatmap");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 12-Volcano
     * ##命令行：Rscript Volcano.R Volcano.txt 0.5 10 15 0.05 blue green red
     * ##Volcano.txt：热图矩阵
     * ##0.5:散点大小
     * ##10：X轴宽度
     * ##15：Y轴宽度
     * ##0.05：padj阈值设置
     * ##blue green red后面的是三种颜色
     * @param taskName 任务名
     * @param heatMap 热图矩阵相对路径【选择文件，FileVO中获取】
     * @param scatterSize 散点大小
     * @param xWidth X轴宽度
     * @param yWidth Y轴宽度
     * @param padj padj阈值设置
     * @param color1 第一种颜色【颜色给文本框，用户填写即可】
     * @param color2 第二种颜色
     * @param color3 第三种颜色
     * @param request
     * @return
     */
    @PostMapping("/createVolcanoTask")
    public String createVolcanoTask(String taskName,String heatMap,String scatterSize,String xWidth,String yWidth,String padj,String color1,String color2,String color3,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            heatMap = URLDecoder.decode(heatMap,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //热图矩阵
        String heatMapPath = getUserRootPath(username) + heatMap;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(heatMapPath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/Volcano.R "+heatMapPath+" "+scatterSize+" "+xWidth+" "+yWidth+" "+padj+" "+color1+" "+color2+" "+color3;
        resultDir += "Volcano.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("Volcano");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 13-venn
     * ##命令行：Rscript venn.R venn.txt
     * ##venn.txt:文件
     * @param taskName 任务名
     * @param relativePath 文件相对路径【选择文件，FileVO中获取】
     * @param request
     * @return
     */
    @PostMapping("/createVennTask")
    public String createVennTask(String taskName,String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/venn.R "+inputFilePath;
        resultDir += "venn.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("venn");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 14-IDconvert
     * ##命令行：Rscript IDconvert.R convert.txt 2 SYMBOL ENSEMBL
     * ##convert.txt：基因文件
     * ##2:物种选择（1：人类，2：小鼠,3：大鼠）
     * ##SYMBOL：基因数据类型
     * ##ENSEMBL：想要转化成的基因数据类型
     * @param taskName 任务名
     * @param relativePath 基因文件相对路径【选择文件，FileVO中获取】
     * @param specie 物种选择（1：人类，2：小鼠,3：大鼠）
     * @param geneType 基因数据类型
     * @param ensembl 想要转化成的基因数据类型
     * @param request
     * @return
     */
    @PostMapping("/createIDconverttask")
    public String createIDconverttask(String taskName,String relativePath,String specie,String geneType,String ensembl,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        //基因文件
        String inputFilePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/IDconvert.R "+inputFilePath+" "+specie+" "+geneType+" "+ensembl;
        resultDir += "IDconvert.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("IDconvert");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 15-pie
     * ##命令行：Rscript pie.R pie.txt
     * ##pie.txt：文件
     * @param taskName 任务名
     * @param relativePath 文件相对路径【选择文件，FileVO中获取】
     * @param request
     * @return
     */
    @PostMapping("/createPieTask")
    public String createPieTask(String taskName,String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "Rscript /home/soft/pie.R "+inputFilePath;
        resultDir += "pie.zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("pie");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    /**
     * 16-matrix
     * ##命令行：sh matrix.sh 1.txt xu
     * ##1.txt：输入文件
     * ##xu:输出文件的名字
     * @param taskName 任务名
     * @param relativePath 输入文件相对路径【选择文件，FileVO中获取】
     * @param fileName 输出文件的名字
     * @param request
     * @return
     */
    @PostMapping("/createMatrixTask")
    public String createMatrixTask(String taskName,String relativePath,String fileName,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        try {
            relativePath = URLDecoder.decode(relativePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

//        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        String resultDir = getUserResultRootPath(inputFilePath);
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "sh /home/soft/matrix.sh "+inputFilePath + " " +fileName;
        resultDir += fileName + ".zip";
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUser(username);
        taskEntity.setUser(userVO.getUsername());
        taskEntity.setTaskName(taskName);
        taskEntity.setType("matrix");
        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
        taskEntity.setTaskCode(cmd);
        taskEntity.setStatus("queuing");
        taskEntity.setResultFile(resultDir);
        return taskService.createTask(taskEntity);
    }

    private String getUserRootPath(String username){
        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator+ "data" ;
        return rootPath + File.separator + username;
    }

//    private String getUserResultRootPath(String username){
//        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator+ "data" ;
//        return rootPath + File.separator + username + File.separator+ "result";
//    }

    private String getUserResultRootPath(String inputFilePath) {
        return inputFilePath.substring(0, inputFilePath.lastIndexOf(File.separator)+1);
    }

}
