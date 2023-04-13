package org.cdaz.monitor.api.service;

import org.cdaz.monitor.api.entity.MonitorResult;

import java.util.List;

public interface MonitorService {
    List<MonitorResult> getMonitorResult(String clientId);
}
