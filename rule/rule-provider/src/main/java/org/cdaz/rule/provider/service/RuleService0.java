package org.cdaz.rule.provider.service;

import org.cdaz.rule.api.entity.Rule;

import java.util.Map;

public interface RuleService0 {
    Map<String, Rule> getRule(String clientId);
    Rule getRule(String clientId, String prop);
    void postRule(Rule rule);
    void putRule(Rule rule);
    void deleteRule(Rule rule);
}
