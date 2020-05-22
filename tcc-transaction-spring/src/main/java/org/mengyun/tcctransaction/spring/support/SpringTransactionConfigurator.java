package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.recover.RecoverConfig;
import org.mengyun.tcctransaction.repository.CachableTransactionRepository;
import org.mengyun.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by changmingxie on 11/11/15.
 */
public class SpringTransactionConfigurator implements TransactionConfigurator {

    private static volatile ExecutorService executorService = null;//线程连接池

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;


    private TransactionManager transactionManager;

    public void init() {
        transactionManager = new TransactionManager();//创建一个事物管理器
        transactionManager.setTransactionRepository(transactionRepository);//设置事物的持久层

        if (executorService == null) {


            Executors.defaultThreadFactory();
            synchronized (SpringTransactionConfigurator.class) {

                if (executorService == null) {
                    //初始化线程池
                    executorService = new ThreadPoolExecutor(
                            recoverConfig.getAsyncTerminateThreadCorePoolSize(),//核心线程数
                            recoverConfig.getAsyncTerminateThreadMaxPoolSize(),//最大线程数
                            5L,//空闲线程最大存活时间为5s
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(recoverConfig.getAsyncTerminateThreadWorkQueueSize()),//设置阻塞队列及其长度
                            new ThreadFactory() {

                                final AtomicInteger poolNumber = new AtomicInteger(1);//线程池大小
                                final ThreadGroup group;//线程组
                                final AtomicInteger threadNumber = new AtomicInteger(1);//线程数量
                                final String namePrefix;//名称前缀

                                {
                                    SecurityManager securityManager = System.getSecurityManager();
                                    this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
                                    this.namePrefix = "tcc-async-terminate-pool-" + poolNumber.getAndIncrement() + "-thread-";
                                }

                                public Thread newThread(Runnable runnable) {
                                    Thread thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
                                    if (thread.isDaemon()) {
                                        thread.setDaemon(false);//设置为非守护线程
                                    }

                                    if (thread.getPriority() != 5) {
                                        thread.setPriority(5);//线程优先级设置为5
                                    }

                                    return thread;
                                }
                            },//创建新线程用的工厂
                            new ThreadPoolExecutor.CallerRunsPolicy());//线程池阻塞策略
                }
            }
        }

        transactionManager.setExecutorService(executorService);

        //如果是缓存的持久层，则设置过期时间
        if (transactionRepository instanceof CachableTransactionRepository) {
            ((CachableTransactionRepository) transactionRepository).setExpireDuration(recoverConfig.getRecoverDuration());
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
