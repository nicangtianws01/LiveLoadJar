package org.example.controller;

import org.example.entity.task.TaskConfig;
import org.example.repository.task.TaskRepository;
import org.example.runner.Runner;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务流手动执行接口
 */
@RequestMapping("/api/v1/task")
@RestController
public class TaskController {

    private final Runner runner;

    private final TaskRepository taskRepository;

    private final ThreadPoolExecutor executor;

    public TaskController(Runner runner, TaskRepository taskRepository, ThreadPoolExecutor executor) {
        this.runner = runner;
        this.taskRepository = taskRepository;
        this.executor = executor;
    }

    @GetMapping("/run")
    public String runTaskOfName(@RequestParam String name) {
        TaskConfig taskConfig = taskRepository.findByName(name);
        runner.run(taskConfig.getConfig());
        return "任务执行完成!";
    }

    @GetMapping("/run/{name}")
    public String runTaskOfNameByThreadPool(@PathVariable String name) {
        TaskConfig taskConfig = taskRepository.findByName(name);
        executor.execute(() -> runner.run(taskConfig.getConfig()));
        return "任务执行完成!";
    }
}
