package cn.edu.nju.njutcm.rna.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ldchao on 2018/5/13.
 */
public class ThreadPoolFactory {


    private static ExecutorService executorService;

    private ThreadPoolFactory(){
    }

    public static ExecutorService getExecutorService(){
        if(executorService == null){
            synchronized (ThreadPoolFactory.class) {
                if(executorService == null)
                    executorService = Executors.newFixedThreadPool(5) ;
            }
        }
        return executorService;
    }
}
