package org.cdaz.monitor.provider.entity;

import org.cdaz.rule.api.entity.Rule;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

public class ProcessingData {
    private PendingData data;
    private Rule rule;

    public ProcessingData() {
    }

    public ProcessingData(PendingData data, Rule rule) {
        this.data = data;
        this.rule = rule;
    }

    public PendingData getData() {
        return data;
    }

    public void setData(PendingData data) {
        this.data = data;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public boolean isError(ExpressionParser parser, EvaluationContext context) {
        Expression exp = parser.parseExpression(rule.getRuleExp());
        context.setVariable("prop", data.getData());
        Object passing = exp.getValue(context);

        if (!(passing instanceof Boolean)) {
            return false;
        }
        return !(boolean) passing;
    }
}
