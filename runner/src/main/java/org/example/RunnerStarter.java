package org.example;

import jakarta.annotation.Resource;
import org.example.cache.ProccessorCache;
import org.example.cache.ProccessorDefCache;
import org.example.common.Proccessor;
import org.example.common.anno.JsonTypeDef;
import org.example.service.PluginService;
import org.example.util.ObjectMapperUtil;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EnableScheduling
@SpringBootApplication
public class RunnerStarter implements CommandLineRunner {

    @Resource
    private PluginService pluginService;

    @Resource
    private List<Proccessor> proccessors;

    @Value("${org.example.package}")
    private String basePackage;

    public static void main(String[] args) {
        SpringApplication.run(RunnerStarter.class, args);
    }

    @Override
    public void run(String... args) {
        // 缓存默认插件
        Map<String, Proccessor> collect = proccessors.stream()
                .collect(Collectors.toMap(
                        proccesser -> proccesser.getClass().getSimpleName(),
                        proccesser -> proccesser)
                );
        ProccessorCache.addProccessors(collect);
        // 加载已上传的插件
        pluginService.loadAll();

        // 从项目中加载内置的def子类
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> defs = reflections.getTypesAnnotatedWith(JsonTypeDef.class);
        ProccessorDefCache.init(defs);

        // 刷新json解析中的def类
        ObjectMapperUtil.init();
    }
}
