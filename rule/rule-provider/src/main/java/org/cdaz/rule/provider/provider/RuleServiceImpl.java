package org.cdaz.rule.provider.provider;

import org.apache.dubbo.config.annotation.DubboService;
import org.cdaz.rule.api.entity.Rule;
import org.cdaz.rule.api.service.RuleService;
import org.cdaz.rule.provider.dao.RuleDao;
import org.cdaz.rule.provider.service.RuleService0;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleService0 ruleService0;

    @Override
    public Rule getRule(String clientId, String prop) {
        return ruleService0.getRule(clientId, prop);
    }
}
