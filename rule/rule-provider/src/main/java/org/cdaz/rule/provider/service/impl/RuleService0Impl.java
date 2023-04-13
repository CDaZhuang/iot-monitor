package org.cdaz.rule.provider.service.impl;

import org.cdaz.rule.api.entity.Rule;
import org.cdaz.rule.provider.dao.RuleDao;
import org.cdaz.rule.provider.service.RuleCacheService;
import org.cdaz.rule.provider.service.RuleService0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RuleService0Impl implements RuleService0 {

    @Autowired
    private RuleCacheService ruleCacheService;

    @Autowired
    private RuleDao ruleDao;

    @Override
    public Map<String, Rule> getRule(String clientId) {
        List<Rule> rules = ruleDao.findRuleByClientId(clientId);
        return rules.stream().collect(Collectors.toMap(Rule::getProp,
                rule -> rule));
    }

    @Override
    public Rule getRule(String clientId, String prop) {
        return ruleCacheService.get(clientId, prop);
    }

    @Override
    public void postRule(Rule rule) {
        ruleDao.createRule(rule);
    }

    @Transactional
    @Override
    public void putRule(Rule rule) {
        if (ruleDao.findRuleByClientIdAndProp(rule.getClientId(), rule.getProp()) != null) {
            ruleDao.deleteRuleByClientIdAndProp(rule.getClientId(), rule.getProp());
        }
        ruleDao.createRule(rule);
    }

    @Override
    public void deleteRule(Rule rule) {
        ruleDao.deleteRuleByClientIdAndProp(rule.getClientId(), rule.getProp());
    }
}
