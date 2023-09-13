package org.example.schedule;

import jakarta.annotation.Resource;
import org.example.runner.Runner;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class DefaultQuartzBean extends QuartzJobBean {

    @Resource
    private Runner runner;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        String config = context.getJobDetail().getJobDataMap().getString("config");
        runner.run(config);
    }
}
