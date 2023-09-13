package org.example.service;

import org.quartz.*;

public interface SchedulerService {

    void startJobTask(String group, String name, String cron) throws SchedulerException;

    String getjobInfo(String group, String name) throws SchedulerException;

    boolean modifyJob(String group, String name, String cron) throws SchedulerException;

    void pauseAllJob() throws SchedulerException;

    void pauseJob(String group, String name) throws SchedulerException;

    void resumeAllJob() throws SchedulerException;

    void resumeJob(String group, String name) throws SchedulerException;

    void deleteJob(String group, String name) throws SchedulerException;
}
