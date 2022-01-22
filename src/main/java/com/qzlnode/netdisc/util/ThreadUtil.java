package com.qzlnode.netdisc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qzlzzz
 */
public class ThreadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtil.class);

    /**
     * 当前设备CPU的核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * IO处理线程数
     */
    private static final int IO_MAX = Math.max(2,2 * CPU_COUNT);

    /**
     * 空闲的线程在线程池存活的时间
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 有界队列size
     */
    private static final int QUEUE_SIZE = 128;


    public static void shutdownThreadPoolGracefully(ExecutorService threadPool){
        if (threadPool.isTerminated()){
            return;
        }
        try {
            threadPool.shutdown();
        }catch (SecurityException | NullPointerException e){
            return;
        }
        try{
            if(!threadPool.awaitTermination(60, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
                if(!threadPool.awaitTermination(60,TimeUnit.SECONDS)){
                    LOGGER.error("线程池没有正常关闭");
                }
            }
        }catch (InterruptedException e){
            threadPool.shutdownNow();
        }
        try{
            if(!threadPool.isTerminated()){
                for(int i = 0; i < 100 ; i++){
                    if (threadPool.awaitTermination(10,TimeUnit.MILLISECONDS)){
                        break;
                    }
                    threadPool.shutdownNow();
                }
            }
        } catch (Throwable e){
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 用于文件上传这种Io型任务的线程池
     */
    private static class IoIntenseTargetThreadPool{

        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                IO_MAX,
                IO_MAX,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new FileUploadThreadFactory("fileTask")
        );

        static {
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                shutdownThreadPoolGracefully(EXECUTOR);
            },"IO密集型任务"));
        }
    }

    static public class FileUploadThreadFactory implements ThreadFactory{

        private AtomicInteger threadNo = new AtomicInteger();

        private String threadName;

        private String tmp;

        public FileUploadThreadFactory(String name){
            this.tmp = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            this.threadName = this.tmp + threadNo.get();
            threadNo.incrementAndGet();
            Thread thread = new Thread(r,threadName);
            return thread;
        }
    }

    /**
     * 获取
     * @return
     */
    public static ThreadPoolExecutor getIOTargetThreadPool(){
        return IoIntenseTargetThreadPool.EXECUTOR;
    }


}
