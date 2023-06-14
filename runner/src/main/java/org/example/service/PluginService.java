package org.example.service;

import org.example.cache.ProccesserCache;
import org.example.cache.ProccesserDefCache;
import org.example.common.Proccesser;
import org.example.common.anno.JsonTypeDef;
import org.example.entity.PluginInfo;
import org.example.repository.PluginRepository;
import org.example.util.ClassLoaderUtil;
import org.example.util.ClassUtil;
import org.example.util.SpringBeanRegister;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

@Service
public class PluginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SpringBeanRegister register;

    private final PluginRepository repository;

    public PluginService(SpringBeanRegister register, PluginRepository repository) {
        this.register = register;
        this.repository = repository;
    }

    public void loadAllPlugins() {
        logger.info("Load plugins...");
        // 加载所有插件
        List<PluginInfo> pluginInfos = repository.findAll();

        if (pluginInfos.size() == 0) {
            logger.info("No plugins found...");
            return;
        }

        // 插件及其对应的类
        HashMap<String, List<String>> pluginMap = new HashMap<>();

        pluginInfos.forEach(pluginInfo -> {
            logger.info("Plugin {} loading...", pluginInfo.getName());
            // 插件全路径
            String jarPath = pluginInfo.getPath();
            try {
                File file = new File(jarPath);
                // 扫描得到插件所有的class文件
                List<String> list = ClassUtil.getClassListSimple(file);
                ClassLoader classLoader = ClassLoaderUtil.getClassLoader(file.toURI().toURL());
                pluginMap.put(pluginInfo.getName(), list);

                pluginMap.forEach((s, strings) -> strings.forEach(clazzPath -> {
                    try {
                        Class<?> clazz = classLoader.loadClass(clazzPath);
                        // 将proccesser注册到spring bean中
                        if (clazzPath.endsWith("Proccesser")) {
                            register.registerBean(clazz.getSimpleName(), clazz);
                            Proccesser proccesser = (Proccesser) register.getBean(clazz.getSimpleName());
                            ProccesserCache.addProccesser(clazz.getSimpleName(), proccesser);
                            logger.info("Plugin {} register complete.", clazz.getSimpleName());
                        }
                        if(clazzPath.endsWith("Def")){
                            ProccesserDefCache.addProccesserDef(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("Class not found error: {}", e.getMessage());
                    } catch (NullPointerException e) {
                        logger.error("Class loader is null error: {}", e.getMessage());
                    }
                }));
            } catch (MalformedURLException e) {
                logger.error("Load jar error: {}", e.getMessage());
            } catch (FileNotFoundException e) {
                logger.error("File not found error: {}", e.getMessage());
            } catch (IOException e) {
                logger.error("Load class from jar error: {}", e.getMessage());
            }

        });
    }
}
