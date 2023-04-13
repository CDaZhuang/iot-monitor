package org.cdaz.rule.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo(scanBasePackages = {"org.cdaz.rule.provider.provider"})
public class RuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuleApplication.class, args);
    }
}
