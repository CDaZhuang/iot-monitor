package org.cdaz.alarm.api.service;

public interface AlarmService {
    void alarm(String from, String to, String subject, String text);
}
