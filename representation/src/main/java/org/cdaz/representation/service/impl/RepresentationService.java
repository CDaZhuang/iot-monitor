package org.cdaz.representation.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.dubbo.config.annotation.DubboReference;
import org.cdaz.monitor.api.entity.MonitorResult;
import org.cdaz.monitor.api.service.MonitorService;
import org.cdaz.representation.entity.Operation;
import org.cdaz.representation.service.SessionService;
import org.cdaz.representation.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
public class RepresentationService implements WebSocketHandler {
    @Autowired
    private SessionService sessionService;

    @DubboReference
    private MonitorService monitorService;


    private final Logger LOG = LoggerFactory.getLogger(RepresentationService.class);
    private ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(2);
    private Map<String, Set<String>> queryClientIdMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        scheduledExecutor.scheduleAtFixedRate(this::pushMonitorResults, 10, 2, TimeUnit.SECONDS);
    }

    private void pushMonitorResults() {
        for (Map.Entry<String, Set<String>> entry : queryClientIdMap.entrySet()) {
            List<MonitorResult> results = new ArrayList<>();
            for (String clientId : entry.getValue()) {
                results.addAll(monitorService.getMonitorResult(clientId));
            }

            LOG.info("Receive results from Monitor Service, results' size: {}", results.size());


            try {
                WebSocketSession session = sessionService.getSession(entry.getKey());
                TextMessage message = new TextMessage(JsonUtils.toJson(results));

                session.sendMessage(message);
                LOG.info("Push message to client, message: {}, client session: {}", message.getPayload(), session.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionService.login(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        LOG.info("Receive message: {}", message.getPayload().toString());

        TypeToken<Operation> typeToken = new TypeToken<Operation>(){};
        Operation operation = (Operation) JsonUtils.fromJson(message.getPayload().toString(), typeToken.getType());

        LOG.info("Transform message to operation, operation: {}", JsonUtils.toJson(operation));

        if (operation.getType() == Operation.TYPE_ADD) {
            Set<String> set = queryClientIdMap.getOrDefault(session.getId(), new HashSet<>());
            set.add(operation.getClientId());
            queryClientIdMap.put(session.getId(), set);
        } else {
            queryClientIdMap.getOrDefault(session.getId(), new HashSet<>()).remove(operation.getClientId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        queryClientIdMap.remove(session.getId());
        sessionService.logout(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
