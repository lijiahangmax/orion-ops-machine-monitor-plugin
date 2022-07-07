package com.orion.ops.machine.monitor.metrics.collect;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.machine.monitor.constant.Const;
import com.orion.ops.machine.monitor.entity.agent.bo.MemoryUsageBO;
import com.orion.ops.machine.monitor.metrics.MetricsProvider;
import com.orion.ops.machine.monitor.utils.Formats;
import com.orion.ops.machine.monitor.utils.PathBuilders;
import com.orion.ops.machine.monitor.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import oshi.hardware.GlobalMemory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 内存指标收集器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/4 12:34
 */
@Slf4j
@Order(510)
@Component
public class MemoryMetricsCollector implements IMetricsCollector<MemoryUsageBO> {

    @Resource
    private MetricsProvider metricsProvider;

    /**
     * 内存信息
     */
    private GlobalMemory memory;

    /**
     * 上次采集内存信息时间
     */
    private long prevTime;

    @PostConstruct
    private void initCollector() {
        log.info("初始化内存指标收集器");
        this.memory = metricsProvider.getHardware().getMemory();
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    public MemoryUsageBO collect() {
        long prevTime = this.prevTime;
        long total = memory.getTotal();
        long usage = total - memory.getAvailable();
        long currentTime = this.prevTime = System.currentTimeMillis();
        // 计算
        MemoryUsageBO mem = new MemoryUsageBO();
        mem.setUr(Formats.roundToDouble((double) usage / (double) total * 100, 3));
        mem.setUs(usage / Const.BUFFER_KB_1 / Const.BUFFER_KB_1);
        mem.setSr(Dates.getSecondTime(prevTime));
        mem.setEr(Dates.getSecondTime(currentTime));
        log.debug("内存指标: {}", JSON.toJSONString(mem));
        // 拼接到天级数据
        String path = PathBuilders.getMemoryDayDataPath(Utils.getRangeStartTime(mem.getSr()));
        Utils.appendMetricsData(path, mem);
        return mem;
    }

}
