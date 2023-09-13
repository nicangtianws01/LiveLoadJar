package org.example.config.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * 自定义Job工厂
 * 提供给Spring容器管理Job
 */
@Configuration
public class CustomJobFactory extends AdaptableJobFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public CustomJobFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    /**
     * Create the job instance, populating it with property values taken
     * from the scheduler context, job data map and trigger data map.
     *
     * @param bundle
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        autowireCapableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
