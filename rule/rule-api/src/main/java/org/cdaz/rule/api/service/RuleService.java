package org.cdaz.rule.api.service;

import org.cdaz.rule.api.entity.Rule;

public interface RuleService {
    Rule getRule(String clientId, String prop);
}
