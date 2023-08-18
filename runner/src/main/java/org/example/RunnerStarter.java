package org.example;

import org.example.cache.ProccesserCache;
import org.example.common.Proccesser;
import org.example.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class RunnerStarter implements CommandLineRunner {

    @Autowired
    private PluginService pluginService;

    @Autowired
    private List<Proccesser> proccessers;

    public static void main(String[] args) {
        SpringApplication.run(RunnerStarter.class, args);
    }

    @Override
    public void run(String... args) {
        // 缓存默认插件
        Map<String, Proccesser> collect = proccessers.stream()
                .collect(Collectors.toMap(
                        proccesser -> proccesser.getClass().getSimpleName(),
                        proccesser -> proccesser)
                );
        ProccesserCache.addProccessers(collect);
        // 加载已上传的插件
        pluginService.loadAllPlugins();
    }
}
