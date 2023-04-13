package org.cdaz.monitor.provider.provider;

import org.apache.dubbo.config.annotation.DubboService;
import org.cdaz.monitor.api.entity.MonitorResult;
import org.cdaz.monitor.api.service.MonitorService;
import org.cdaz.monitor.provider.service.MonitorService0;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class MonitorServiceImpl implements MonitorService {
    @Autowired
    private MonitorService0 monitorService0;

    @Override
    public List<MonitorResult> getMonitorResult(String clientId) {
        return monitorService0.findMonitoringResultByClientId(clientId);
    }
}
