package org.cdaz.monitor.provider.service;


import org.cdaz.monitor.api.entity.MonitorResult;

import java.util.List;

public interface MonitorService0 {
    List<MonitorResult> findMonitoringResultByClientId(String clientId);
}
