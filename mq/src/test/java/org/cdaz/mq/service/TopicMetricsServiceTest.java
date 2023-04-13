package org.cdaz.mq.service;

import org.cdaz.mq.MqApplication;
import org.cdaz.mq.util.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MqApplication.class)
public class TopicMetricsServiceTest {
    @Autowired
    public TopicMetricsService topicMetricsService;

    private final Logger LOG = LoggerFactory.getLogger(TopicMetricsServiceTest.class);

    @Test
    public void testTopicMetricsService() {
        // topicMetricsService.createMetrics("topic");
        LOG.info("{}", JsonUtils.toJson(topicMetricsService.getMetrics("topic")));
        LOG.info("{}", JsonUtils.toJson(topicMetricsService.getAllMetrics()));
    }
}
