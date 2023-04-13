package org.cdaz.rule.provider.service;

import org.cdaz.rule.api.entity.Rule;

public interface RuleCacheService {
    Rule get(String clientId, String prop);
}
