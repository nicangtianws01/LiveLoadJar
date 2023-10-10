package org.example.controller;

import org.example.service.SchedulerService;
import org.example.vo.ScheduleTaskVo;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quartz")
public class QuartzController {
    private final SchedulerService schedulerService;

    public QuartzController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String start(@RequestBody ScheduleTaskVo taskConfig) throws SchedulerException {
        schedulerService.startJobTask(taskConfig.getGroup(), taskConfig.getName(), taskConfig.getCron());
        return "success";
    }

    @RequestMapping(value = "/{group}/{name}", method = RequestMethod.PATCH)
    public String resume(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.resumeJob(group,  name);
        return "success";
    }

    @RequestMapping(value = "/resumeAll", method = RequestMethod.PUT)
    public String resumeAll() throws SchedulerException {
        schedulerService.resumeAllJob();
        return "success";
    }

    @RequestMapping(value = "/pause/{group}/{name}", method = RequestMethod.PATCH)
    public String pause(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.pauseJob(group,  name);
        return "success";
    }

    @RequestMapping(value = "/pauseAll", method = RequestMethod.PUT)
    public String pause() throws SchedulerException {
        schedulerService.pauseAllJob();
        return "success";
    }

    @RequestMapping(value = "/{group}/{name}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.deleteJob(group,  name);
        return "success";
    }
}
