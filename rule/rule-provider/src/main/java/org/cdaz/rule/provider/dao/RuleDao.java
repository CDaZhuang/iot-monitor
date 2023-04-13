package org.cdaz.rule.provider.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cdaz.rule.api.entity.Rule;


import java.util.List;

@Mapper
public interface RuleDao {
    List<Rule> findRuleByClientId(String clientId);
    Rule findRuleByClientIdAndProp(@Param("clientId") String clientId, @Param("prop") String prop);
    void createRule(Rule rule);
    void updateRule(Rule rule);
    void deleteRuleByClientIdAndProp(@Param("clientId") String clientId, @Param("prop") String prop);
}
