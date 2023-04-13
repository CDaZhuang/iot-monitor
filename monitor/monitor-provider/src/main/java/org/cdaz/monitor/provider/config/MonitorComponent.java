package org.cdaz.monitor.provider.config;

import org.cdaz.monitor.provider.constant.RuleTypeConstant;
import org.cdaz.monitor.provider.entity.PendingData;
import org.cdaz.monitor.provider.entity.ProcessingData;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class MonitorComponent {
    @Bean
    public BlockingDeque<List<PendingData>> pendingBatchQueue() {
        return new LinkedBlockingDeque<>();
    }

    @Bean
    public Map<Integer, BlockingDeque<List<ProcessingData>>> processingBatchQueues() {
        Map<Integer, BlockingDeque<List<ProcessingData>>> map = new HashMap<>();
        map.put(RuleTypeConstant.DEFAULT, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_5_SECONDS, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_10_SECONDS, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_20_SECONDS, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_50_SECONDS, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_100_SECONDS, new LinkedBlockingDeque<>());
        map.put(RuleTypeConstant.COUNTING_1000_SECONDS, new LinkedBlockingDeque<>());
        return map;
    }
}
