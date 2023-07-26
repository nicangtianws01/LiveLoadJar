package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.cache.ProccesserCache;
import org.example.common.Proccesser;
import org.example.runner.Runner;
import org.example.service.PluginService;
import org.example.util.ClassLoaderUtil;
import org.example.util.SpringBeanRegister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/plugin")
public class PluginController {

    private final SpringBeanRegister register;

    private final PluginService pluginService;

    private final Runner runner;

    @Value("${org.example.jar}")
    private String jarPath;

    public PluginController(SpringBeanRegister register, PluginService pluginService, Runner runner) {
        this.register = register;
        this.pluginService = pluginService;
        this.runner = runner;
    }

    /**
     * 上传并载入插件
     *
     * @return
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     */
    @RequestMapping(value = "/push", method = RequestMethod.GET)
    public String pushPlugin() throws ClassNotFoundException, MalformedURLException {
        File file = new File(jarPath + "inside-1.0-SNAPSHOT.jar");
        ClassLoader classLoader = ClassLoaderUtil.getClassLoader(file.toURI().toURL());
        assert classLoader != null;

        classLoader.loadClass("org.example.proccesser.inside.InsideDef");
        Class<?> clazz = classLoader.loadClass("org.example.proccesser.inside.InsideProccesser");

        register.registerBean(clazz.getSimpleName(), clazz);

        Proccesser proccesser = (Proccesser) register.getBean(clazz.getSimpleName());
        ProccesserCache.addProccesser(clazz.getSimpleName(), proccesser);
        return "ok";
    }

    @RequestMapping(value = "/runTask", method = RequestMethod.GET)
    public String runTask() throws IOException {

        // 读取配置文件
        StringBuilder configBuilder = new StringBuilder();
        File file = ResourceUtils.getFile("classpath:task-config/task01.json");

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader);
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                configBuilder.append(line);
            }
        }
        String config = configBuilder.toString();
        runner.run(config);
        return "任务执行完成!";
    }

    @RequestMapping(value = "/loadAll", method = RequestMethod.GET)
    public String loadAll() {
        pluginService.loadAllPlugins();
        return "ok";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "ok";
    }
}
