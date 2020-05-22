package org.mengyun.tcctransaction.recover;

import java.util.Set;

/**
 * Created by changming.xie on 6/1/16.
 */
public interface RecoverConfig {

    public int getMaxRetryCount();//最大重试次数

    public int getRecoverDuration();//获取恢复操作持续时间

    public String getCronExpression();//获取cron表达式

    public Set<Class<? extends Exception>> getDelayCancelExceptions();

    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayRecoverExceptions);

    public int getAsyncTerminateThreadCorePoolSize();//获取异步核心线程数量

    public int getAsyncTerminateThreadMaxPoolSize();//获取异步线程最大数量

    public int getAsyncTerminateThreadWorkQueueSize();//获取线程工作队列大小
}
