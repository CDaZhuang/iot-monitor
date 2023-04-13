package org.cdaz.alarm.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo(scanBasePackages = {"org.cdaz.alarm.provider.service"})
public class AlarmApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlarmApplication.class, args);
    }
}
