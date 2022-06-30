package com.orion.ops.machine.monitor.collect;

import com.orion.constant.Const;
import com.orion.lang.thread.ThreadFactoryBuilder;
import com.orion.utils.Threads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 机器指标采集调度器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/29 14:40
 */
@Slf4j
@Order(500)
@Component
public class MetricsCollectScheduler implements DisposableBean {

    @Value("${machineId}")
    private Long machineId;

    @Value("${collect.period.second}")
    private Integer collectPeriodSecond;

    @Resource
    private MetricsCollector metricsCollector;

    /**
     * 调度器
     */
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void initScheduler() {
        log.info("初始化数据采集调度器-machineId: {}, 采集周期: {}s", machineId, collectPeriodSecond);
        // 初始化线程池
        ThreadFactory threadFactory = ThreadFactoryBuilder.create()
                .setPrefix("machine-collector-thread-")
                .build();
        this.scheduler = new ScheduledThreadPoolExecutor(1, threadFactory);
        MetricsCollectTask task = new MetricsCollectTask(machineId, metricsCollector);
        scheduler.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        Threads.shutdownPool(scheduler, Const.MS_S_5);
    }

}