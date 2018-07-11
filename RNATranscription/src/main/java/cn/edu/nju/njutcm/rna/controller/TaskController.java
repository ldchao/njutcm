package cn.edu.nju.njutcm.rna.controller;

import cn.edu.nju.njutcm.rna.model.TaskEntity;
import cn.edu.nju.njutcm.rna.service.TaskService;
import cn.edu.nju.njutcm.rna.util.ApplicationUtil;
import cn.edu.nju.njutcm.rna.util.FileUtil;
import cn.edu.nju.njutcm.rna.vo.TaskVO;
import cn.edu.nju.njutcm.rna.vo.UserVO;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ldchao on 2018/5/12.
 */
@RestController
public class TaskController {

    @Autowired
    TaskService taskService;

//    //创建任务并运行
//    @PostMapping("/createTask")
//    public String createTask(HttpServletRequest request,String taskName,String type,Integer fileId){
//        // TODO: 2018/6/14 判断是否重名
//        TaskEntity taskEntity=new TaskEntity();
//        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
//        taskEntity.setUser(userVO.getUsername());
//        taskEntity.setTaskName(taskName);
//        taskEntity.setType(type);
//        taskEntity.setStartAt(new Timestamp(System.currentTimeMillis()));
//        taskEntity.setFileId(fileId);
//        taskEntity.setStatus("queuing");
//        return taskService.createTask(taskEntity);
//    }

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

    //获取所有任务列表
    @GetMapping("/getAllTaskByUser")
    public List<TaskVO> getAllTaskByUser(HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        return taskService.getAllTaskByUser(userVO.getUsername());
    }

    //1-fastQC
    @PostMapping("/createFastQCTask")
    public String createFastQCTask(String taskName,String threadNum,String relativePath,String fileType,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;
        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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


    //2-filter
    @PostMapping("/createTrimmomatic2Task")
    public String createTrimmomatic2Task(String taskName,String software,String seqType,String relativePath1,String relativePath2,String threadNum,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath1 = getUserRootPath(username) + relativePath1;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        if(seqType.equals("1")){
            cmd+="sh /home/soft/filter.sh "+software+" "+seqType+" "+threadNum+" "+inputFilePath1;
        }else{
            String inputFilePath2 = getUserRootPath(username) + relativePath2;
            cmd+="sh /home/soft/filter.sh "+software+" "+seqType+" "+threadNum+" "+inputFilePath1+" "+inputFilePath2;
        }
//        resultDir += "3DPCA.zip";
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

    //3-cufflinks
    @PostMapping("/createCufflinksTask")
    public String createTask(String taskName,String relativePath,int sampleNum,String threadNum,String gene,String untreaded,String treaded,String newTranscript,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath = getUserRootPath(username) + relativePath;
        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";
        cmd += "sh /home/soft/cufflinks.sh "+inputFilePath+" "+sampleNum+" "+threadNum+" "+gene+" "+untreaded+" "+treaded+" "+newTranscript;
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

    //4-count
    @PostMapping("/createCountTask")
    public String createCountTask(String taskName,String seqType,String threadNum,String specie,String relativePath1,String relativePath2,String fileName,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //测序文件
        String inputFilePath1 = getUserRootPath(username) + relativePath1;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
        FileUtil.makeSureDirExist(resultDir);
        String cmd = "cd "+resultDir+";";

        if(seqType.equals("1")){
            cmd +="sh /home/soft/count.sh 1 "+threadNum+" "+specie+" "+inputFilePath1+" "+fileName;
        }else{
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

    //5-DESeq2
    @PostMapping("/createDESeq2Task")
    public String createDESeq2Task(String taskName,String matrix,String conditionFile,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //6-edgeR
    @PostMapping("/createEdgeRTask")
    public String createEdgeRTask(String taskName,String matrix,String conditionFile,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //7-GO
    @PostMapping("/createGOTask")
    public String createGOTask(String taskName,String relativePath,String geneType,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //gene文件
        String genePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //8-KEGG
    @PostMapping("/createKEGGTask")
    public String createKEGGTask(String taskName,String relativePath,String geneType,String specie,String qValue,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //gene文件
        String genePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //9-PCA
    @PostMapping("/createPCATask")
    public String createPCATask(String taskName,String matrix,String conditionFile,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //10-3DPCA
    @PostMapping("/create3DPCATask")
    public String create3DPCATask(String taskName,String matrix,String conditionFile,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;
        //条件文件
        String conditionFilePath = getUserRootPath(username) + conditionFile;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //11-pheatmap
    @PostMapping("/createPheatmapTask")
    public String createPheatmapTask(String taskName,String matrix,String standard,String color1,String color2,String color3,String isCluster,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //表达矩阵
        String matrixPath = getUserRootPath(username) + matrix;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //12-Volcano
    @PostMapping("/createVolcanoTask")
    public String createVolcanoTask(String taskName,String heatMap,String scatterSize,String xWidth,String yWidth,String padj,String color1,String color2,String color3,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //热图矩阵
        String heatMapPath = getUserRootPath(username) + heatMap;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //13-venn
    @PostMapping("/createVennTask")
    public String createVennTask(String taskName,String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //14-IDconvert
    @PostMapping("/createIDconverttask")
    public String createIDconverttask(String taskName,String relativePath,String specie,String geneType,String ensembl,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        //基因文件
        String inputFilePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //15-pie
    @PostMapping("/createPieTask")
    public String createPieTask(String taskName,String relativePath,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    //16-matrix
    @PostMapping("/createMatrixTask")
    public String createMatrixTask(String taskName,String relativePath,String fileName,HttpServletRequest request){
        UserVO userVO = (UserVO) request.getSession().getAttribute("User");
        if(userVO==null){
            return "fail";
        }
        String username=userVO.getUsername();
        String inputFilePath = getUserRootPath(username) + relativePath;

        String resultDir = getUserResultRootPath(username) + File.separator + taskName + File.separator;
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

    private String getUserResultRootPath(String username){
        String rootPath = ApplicationUtil.getInstance().getRootPath() + File.separator+ "data" ;
        return rootPath + File.separator + username + File.separator+ "result";
    }

}
