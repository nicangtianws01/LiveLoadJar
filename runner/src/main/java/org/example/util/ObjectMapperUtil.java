package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.example.common.anno.JsonTypeDef;

import java.lang.reflect.Modifier;
import java.util.Set;

public class ObjectMapperUtil {
    private ObjectMapperUtil(){

    }

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void init(Set<Class<?>> classes){
        // 从插件load的类中查找def子类
        classes.stream().filter(clazz -> {
            JsonTypeDef annotation = clazz.getAnnotation(JsonTypeDef.class);
            return annotation != null && clazz.getSimpleName().endsWith("Def");
        }).forEach(ObjectMapperUtil::omSubTypeSet);
    }

    public static void omSubTypeSet(Class<?> clazz) {
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

    public static ObjectMapper getObjectMapper(){
        return objectMapper;
    }

}
