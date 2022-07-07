package com.orion.ops.machine.monitor.entity.agent.dto;

import lombok.Data;

/**
 * 内存使用信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/27 18:09
 */
@Data
public class MemoryUsageDTO {

    /**
     * 总内存
     */
    private Long totalMemory;

    /**
     * 使用内存
     */
    private Long usageMemory;

    /**
     * 空闲内存
     */
    private Long freeMemory;

    /**
     * 内存使用率
     */
    private Double usage;

}