package org.cdaz.mq.service;

import org.cdaz.mq.MqApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MqApplication.class)
public class MqServiceTest {
    @Autowired
    public MqService service;

    @Test
    public void test() {

    }
}
