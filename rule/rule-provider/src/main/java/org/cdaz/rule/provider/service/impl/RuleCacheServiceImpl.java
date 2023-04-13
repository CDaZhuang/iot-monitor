package org.cdaz.rule.provider.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.cdaz.rule.api.entity.Rule;
import org.cdaz.rule.provider.dao.RuleDao;
import org.cdaz.rule.provider.service.RuleCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class RuleCacheServiceImpl implements RuleCacheService {
    private Cache<String, Rule> cache;

    @Autowired
    private RuleDao ruleDao;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(1024 * 100L)
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build();
    }

    private String key(String clientId, String prop) {
        return clientId + '#' + prop;
    }


    @Override
    public Rule get(String clientId, String prop) {
        String key = key(clientId, prop);
        return cache.get(key, k -> ruleDao.findRuleByClientIdAndProp(clientId, prop));
    }
}
