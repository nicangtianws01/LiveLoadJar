package org.example.runner;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Runner {
    /**
     * 执行任务
     * @param config 任务流配置文件
     */
    void run(String config) throws JsonProcessingException;
}
