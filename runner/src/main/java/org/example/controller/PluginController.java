package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import lombok.extern.slf4j.Slf4j;
import org.example.cache.ProccesserCache;
import org.example.cache.ProccesserDefCache;
import org.example.common.Proccesser;
import org.example.common.ProccesserDef;
import org.example.common.TaskDef;
import org.example.common.anno.JsonTypeDef;
import org.example.service.PluginService;
import org.example.util.ClassLoaderUtil;
import org.example.util.SpringBeanRegister;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/plugin")
public class PluginController {

    private final SpringBeanRegister register;

    private final PluginService pluginService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${org.example.jar}")
    private String jarPath;

    @Value("${org.example.package}")
    private String basePackage;

    public PluginController(SpringBeanRegister register, PluginService pluginService) {
        this.register = register;
        this.pluginService = pluginService;
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
        // 输出插件个数
        Map<String, Proccesser> proccessers = ProccesserCache.getProccessers();
        log.info("Plugin number: {}", proccessers.size());

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

        // 加载定义执行步骤属性的子类
        // 从项目中加载def子类
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(JsonTypeDef.class);
        for (Class<?> type : types) {
            // 注册子类
            omSubTypeSet(type);
        }
        // 从插件load的类中查找def子类
        List<Class<?>> classes = ProccesserDefCache.getProccesserDefs();
        classes.stream().filter(clazz -> {
            JsonTypeDef annotation = clazz.getAnnotation(JsonTypeDef.class);
            return annotation != null && clazz.getSimpleName().endsWith("Def");
        }).forEach(this::omSubTypeSet);

        // 解析配置文件
        TaskDef taskDef = objectMapper.readValue(config, TaskDef.class);

        // 通过spel表达式进行变量替换
        Map<String, String> vars = taskDef.getVars();
        EvaluationContext context = new StandardEvaluationContext();
        vars.forEach(context::setVariable);
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(objectMapper.writeValueAsString(taskDef),
                new TemplateParserContext("${", "}"));
        String newDef = (String) expression.getValue(context);
        taskDef = objectMapper.readValue(newDef, TaskDef.class);

        // 执行任务
        List<ProccesserDef> steps = taskDef.getSteps();
        for (ProccesserDef def : steps) {
            Proccesser proccesser = proccessers.get(def.getName());
            if (proccesser != null) {
                proccesser.run(def);
            } else {
                log.info("Proccesser not found!");
            }
        }

        return config;
    }

    public void omSubTypeSet(Class<?> clazz) {
        // 跳过接口和抽象类
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }
        // 提取 JsonTypeDef 注解
        JsonTypeDef extendClassDefine = clazz.getAnnotation(JsonTypeDef.class);
        if (extendClassDefine == null) {
            return;
        }
        // 注册子类型，使用名称建立关联
        objectMapper.registerSubtypes(new NamedType(clazz, extendClassDefine.value()));
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
