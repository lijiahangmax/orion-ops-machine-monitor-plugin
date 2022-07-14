package com.orion.ops.monitor.agent.metrics.statistics;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.monitor.agent.constant.DataMetricsType;
import com.orion.ops.monitor.agent.utils.Utils;
import com.orion.ops.monitor.common.constant.GranularityType;
import com.orion.ops.monitor.common.entity.agent.bo.BaseRangeBO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据访问器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/4 22:48
 */
public class MetricsFileAccessor<T extends BaseRangeBO> {

    /**
     * 开始区间
     */
    private final long startRange;

    /**
     * 结束区间
     */
    private final long endRange;

    /**
     * 指标类型
     */
    private final DataMetricsType metricsType;

    /**
     * 数据粒度
     */
    private final GranularityType granularityType;

    /**
     * 数据类型
     */
    private final Class<T> dataType;

    @SuppressWarnings("unchecked")
    protected MetricsFileAccessor(long startRange, long endRange, DataMetricsType metricsType, GranularityType granularityType) {
        this.startRange = startRange;
        this.endRange = endRange;
        this.metricsType = metricsType;
        this.granularityType = granularityType;
        this.dataType = (Class<T>) metricsType.getBoClass();
    }

    /**
     * 获取数据
     *
     * @return rows
     */
    public List<T> access() {
        // 计算文件日期
        List<String> fileDates;
        if (granularityType.isQueryMonth()) {
            fileDates = this.getRangeMonths();
        } else {
            fileDates = this.getRangeDays();
        }
        // 获取文件绝对路径
        List<String> paths;
        if (granularityType.isQueryMonth()) {
            paths = fileDates.stream()
                    .map(metricsType.getMonthPathGetter())
                    .collect(Collectors.toList());
        } else {
            paths = fileDates.stream()
                    .map(metricsType.getDayPathGetter())
                    .collect(Collectors.toList());
        }
        // 读取数据
        return this.readFileLines(paths);
    }

    /**
     * 获取时间区间 天
     *
     * @return 天数
     */
    private List<String> getRangeDays() {
        List<String> list = Lists.newList();
        long startRange = this.startRange;
        // 获取文件名
        String startRangeTime = Utils.getRangeStartTime(startRange);
        String endRangeTime = Utils.getRangeStartTime(endRange);
        list.add(startRangeTime);
        // 无文件跨度 直接返回
        if (startRangeTime.equals(endRangeTime)) {
            return list;
        }
        // 累加天数 直到相同
        while (!startRangeTime.equals(endRangeTime)) {
            startRange += (Dates.DAY_STAMP / Dates.SECOND_STAMP);
            startRangeTime = Utils.getRangeStartTime(startRange);
            list.add(startRangeTime);
        }
        return list;
    }

    /**
     * 获取时间区间 月
     *
     * @return 月数
     */
    private List<String> getRangeMonths() {
        List<String> list = Lists.newList();
        long startRange = this.startRange;
        // 获取文件名
        String startRangeTime = Utils.getRangeStartMonth(startRange);
        String endRangeTime = Utils.getRangeStartMonth(endRange);
        list.add(startRangeTime);
        // 无文件跨度 直接返回
        if (startRangeTime.equals(endRangeTime)) {
            return list;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(startRange * Dates.SECOND_STAMP));
        // 累加月份 直到相同
        while (!startRangeTime.equals(endRangeTime)) {
            calendar.add(Calendar.MONTH, 1);
            startRange = calendar.getTimeInMillis() / Dates.SECOND_STAMP;
            startRangeTime = Utils.getRangeStartMonth(startRange);
            list.add(startRangeTime);
        }
        return list;
    }

    /**
     * 读取指标文件
     *
     * @param files files
     * @return rows
     */
    private List<T> readFileLines(List<String> files) {
        List<T> list = Lists.newList();
        for (String file : files) {
            Path path = Paths.get(file);
            // 不存在则跳过
            if (!Files.exists(path)) {
                continue;
            }
            // 存在则读取
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    T row = JSON.parseObject(line, dataType);
                    Long currentStart = row.getSr();
                    if (startRange <= currentStart && currentStart <= endRange) {
                        list.add(row);
                    }
                }
            } catch (IOException e) {
                throw Exceptions.ioRuntime(e);
            }
        }
        return list;
    }

}