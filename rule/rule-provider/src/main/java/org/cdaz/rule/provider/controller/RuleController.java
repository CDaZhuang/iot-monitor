package org.cdaz.rule.provider.controller;

import org.cdaz.rule.api.entity.Rule;

import org.cdaz.rule.provider.entity.ResponseResult;
import org.cdaz.rule.provider.service.RuleService0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rule")
public class RuleController {

    @Autowired
    private RuleService0 ruleService0;

    @RequestMapping(value = "/{clientId}", method = RequestMethod.GET)
    public ResponseResult<List<Rule>> getRule(@PathVariable("clientId") String clientId) {
        Map<String, Rule> ruleMap = ruleService0.getRule(clientId);
        return new ResponseResult<>(200,
                "success",
                ruleMap.values().stream().collect(Collectors.toList()));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseResult<Void> postRule(@RequestBody Rule rule) {
        ruleService0.postRule(rule);
        return new ResponseResult<>(200, "success", null);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseResult<Void> putRule(@RequestBody Rule rule) {
        ruleService0.putRule(rule);
        return new ResponseResult<>(200, "success", null);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseResult<Void> delete(@RequestBody Rule rule) {
        ruleService0.deleteRule(rule);
        return new ResponseResult<>(200, "success", null);
    }
}
