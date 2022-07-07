package com.orion.ops.machine.monitor.metrics.statistics;

import com.orion.lang.define.wrapper.TimestampValue;
import com.orion.ops.machine.monitor.constant.Const;
import com.orion.ops.machine.monitor.constant.DataMetricsType;
import com.orion.ops.machine.monitor.entity.agent.bo.CpuUsageBO;
import com.orion.ops.machine.monitor.entity.agent.request.MetricsStatisticsRequest;
import com.orion.ops.machine.monitor.entity.agent.vo.CpuMetricsStatisticsVO;
import com.orion.ops.machine.monitor.entity.agent.vo.MetricsStatisticsVO;
import com.orion.ops.machine.monitor.utils.Formats;
import com.orion.ops.machine.monitor.utils.Utils;

import java.util.List;
import java.util.stream.DoubleStream;

/**
 * 处理器数据指标统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/4 17:51
 */
public class CpuMetricsStatisticResolver extends BaseMetricsStatisticResolver<CpuUsageBO, CpuMetricsStatisticsVO> {

    /**
     * 使用率
     */
    private final MetricsStatisticsVO<Double> usage;

    public CpuMetricsStatisticResolver(MetricsStatisticsRequest request) {
        super(request, DataMetricsType.CPU, new CpuMetricsStatisticsVO());
        this.usage = new MetricsStatisticsVO<>();
        metrics.setUsage(usage);
    }

    @Override
    protected void computeMetricsData(List<CpuUsageBO> rows, Long start, Long end) {
        double avgUsage = Utils.getDoubleStream(rows, CpuUsageBO::getU)
                .average()
                .orElse(Const.D_0);
        usage.getMetrics().add(new TimestampValue<>(start, Formats.roundToDouble(avgUsage, 3)));
    }

    @Override
    protected void computeMetricsMax() {
        double max = super.calcDataAgg(usage.getMetrics(), DoubleStream::max);
        usage.setMax(max);
    }

    @Override
    protected void computeMetricsMin() {
        double min = super.calcDataAgg(usage.getMetrics(), DoubleStream::min);
        usage.setMin(min);
    }

    @Override
    protected void computeMetricsAvg() {
        double avg = super.calcDataAgg(usage.getMetrics(), DoubleStream::average);
        usage.setAvg(avg);
    }

}
