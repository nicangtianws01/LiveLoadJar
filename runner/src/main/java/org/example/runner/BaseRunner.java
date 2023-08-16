package org.example.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import lombok.extern.slf4j.Slf4j;
import org.example.cache.ProccesserCache;
import org.example.cache.ProccesserDefCache;
import org.example.common.Proccesser;
import org.example.common.ProccesserDef;
import org.example.common.TaskDef;
import org.example.common.anno.JsonTypeDef;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基础执行类
 * 能够执行任务流
 */
@Slf4j
@Component
public class BaseRunner implements Runner{

    @Value("${org.example.package}")
    private String basePackage;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String config) {
        try{
            // 输出插件个数
            Map<String, Proccesser> proccessers = ProccesserCache.getProccessers();
            log.info("Plugin number: {}", proccessers.size());
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
        }catch (JsonProcessingException e) {
            log.error("Parse task config failed, please check it!");
            throw new RuntimeException(e);
        }
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
}
