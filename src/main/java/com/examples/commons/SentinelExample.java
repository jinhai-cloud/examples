package com.examples.commons;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentinelExample {

    @Test
    void rule() {
        initFlowQpsRule();
        initDegradeRule();
    }

    /**
     * 流量控制规则
     */
    private void initFlowQpsRule() {
        List<String> resources = new ArrayList<>();
        resources.add("");

        List<FlowRule> rules = new ArrayList<>();
        for (String resource : resources) {
            FlowRule rule = new FlowRule(resource)
                    .setGrade(RuleConstant.FLOW_GRADE_QPS)
                    .setCount(5000)
                    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
            rules.add(rule);
        }
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 熔断降级规则
     */
    private void initDegradeRule() {
        List<String> resources = new ArrayList<>();
        resources.add("");

        List<DegradeRule> rules = new ArrayList<>();
        for (String resource : resources) {
            DegradeRule rule = new DegradeRule(resource)
                    .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                    .setCount(0.7) // 熔断阈值
                    .setTimeWindow(30) // 熔断时长 s
                    .setMinRequestAmount(100) // 熔断触发的最小请求数
                    .setStatIntervalMs(30000); // 统计时长 ms
            rules.add(rule);
        }
        DegradeRuleManager.loadRules(rules);
    }
}
