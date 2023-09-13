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

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String startSchedule(@RequestBody ScheduleTaskVo taskConfig) throws SchedulerException {
        schedulerService.startJobTask(taskConfig.getGroup(), taskConfig.getName(), taskConfig.getCron());
        return "success";
    }

    @RequestMapping(value = "/delete/{group}/{name}", method = RequestMethod.DELETE)
    public String deleteSchedule(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        schedulerService.deleteJob(group,  name);
        return "success";
    }
}
