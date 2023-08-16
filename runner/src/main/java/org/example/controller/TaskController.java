package org.example.controller;

import org.example.entity.TaskConfig;
import org.example.repository.TaskRepository;
import org.example.runner.Runner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务流手动执行接口
 */
@RequestMapping("/v1/task")
@RestController
public class TaskController {

    private final Runner runner;

    private final TaskRepository taskRepository;

    public TaskController(Runner runner, TaskRepository taskRepository) {
        this.runner = runner;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/run")
    public String runTaskOfName(@RequestParam String name) {
        TaskConfig taskConfig = taskRepository.findByName(name);
        runner.run(taskConfig.getConfig());
        return "任务执行完成!";
    }
}
