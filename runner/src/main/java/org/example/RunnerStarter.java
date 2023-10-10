package org.example;

import org.example.common.anno.JsonTypeDef;
import org.example.util.ObjectMapperUtil;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Set;

@EnableScheduling
@SpringBootApplication
public class RunnerStarter implements CommandLineRunner {

    @Value("${org.example.package}")
    private String basePackage;

    public static void main(String[] args) {
        SpringApplication.run(RunnerStarter.class, args);
    }

    @Override
    public void run(String... args) {
        // 从项目中加载内置的def子类
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> defs = reflections.getTypesAnnotatedWith(JsonTypeDef.class);

        // 刷新json解析中的def类
        ObjectMapperUtil.init(defs);
    }
}
