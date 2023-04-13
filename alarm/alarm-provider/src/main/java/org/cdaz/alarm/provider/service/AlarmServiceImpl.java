package org.cdaz.alarm.provider.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.cdaz.alarm.api.service.AlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@DubboService
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private JavaMailSender sender;

    private final Logger LOG = LoggerFactory.getLogger(AlarmServiceImpl.class);

    @Override
    public void alarm(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        sender.send(message);
        LOG.info("Send Email To {}, subject = {}, text = {}", to, subject, text);
    }
}
