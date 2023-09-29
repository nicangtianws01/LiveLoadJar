package org.example.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.entity.task.TaskConfig;
import org.example.repository.task.TaskRepository;
import org.example.schedule.DefaultQuartzBean;
import org.example.service.SchedulerService;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final Scheduler scheduler;

    private final TaskRepository taskRepository;

    public SchedulerServiceImpl(TaskRepository taskRepository, Scheduler scheduler) {
        this.taskRepository = taskRepository;
        this.scheduler = scheduler;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startJobTask(String group, String name, String cron) throws SchedulerException {
        TaskConfig taskConfig = taskRepository.findByGroupAndName(group, name);

        if (taskConfig == null || StringUtils.isBlank(taskConfig.getConfig())) {
            throw new RuntimeException("任务配置不存在");
        }

        String config = taskConfig.getConfig();

        // 对于身份
        JobDetail jobDetail = JobBuilder.newJob(DefaultQuartzBean.class)
                .usingJobData("config", config)
                .withIdentity(name, group)
                .build();

        // CronScheduleBuilder 用于构建Scheduler，定义任务调度的时间规则
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // 触发器
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }


    /**
     * 获取Job信息
     *
     * @param name
     * @param group
     */
    @Override
    public String getjobInfo(String group, String name) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        System.out.println("triggerKey:" + triggerKey);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    /**
     * 修改任务的执行时间
     *
     * @param name
     * @param group
     * @param cron  cron表达式
     * @return
     * @throws SchedulerException
     */
    @Override
    public boolean modifyJob(String group, String name, String cron) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(cron)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    @Override
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 暂停某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    @Override
    public void pauseJob(String group, String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    @Override
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 恢复某个任务
     */
    @Override
    public void resumeJob(String group, String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    @Override
    public void deleteJob(String group, String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.deleteJob(jobKey);
    }
}
