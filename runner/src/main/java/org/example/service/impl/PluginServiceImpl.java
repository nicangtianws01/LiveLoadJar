package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.cache.ProccessorCache;
import org.example.cache.ProccessorDefCache;
import org.example.common.Proccessor;
import org.example.entity.plugin.PluginInfo;
import org.example.repository.plugin.PluginRepository;
import org.example.service.PluginFileService;
import org.example.service.PluginService;
import org.example.util.ClassLoaderUtil;
import org.example.util.ClassUtil;
import org.example.util.SpringBeanRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Introspector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

@Slf4j
@Service
public class PluginServiceImpl implements PluginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SpringBeanRegister register;

    private final PluginRepository repository;

    private final PluginFileService pluginFileService;

    @Value("${org.example.plugin.path.target}")
    String targetPath;

    @Value("${org.example.plugin.path.tmp}")
    String tmpPath;

    public PluginServiceImpl(SpringBeanRegister register, PluginRepository repository, PluginFileService pluginFileService) {
        this.register = register;
        this.repository = repository;
        this.pluginFileService = pluginFileService;
    }

    @Override
    public boolean exists(String name) {
        PluginInfo oldInfo = repository.findByName(name);
        return oldInfo != null && oldInfo.getId() != null;
    }

    /**
     * 保存插件并将其扫描到缓存中
     * 如果插件已经存在，则删除插件并重新保存插件
     *
     * @param name 插件名称
     * @param path 只是插件文件名
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void load(String name, String path) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName(name);
        pluginInfo.setPath(targetPath + "/" + path);
        repository.save(pluginInfo);
        log.info("插件已保存,  开始加载插件");
        loadPlugin(name, path);
    }

    @Override
    public void unload(String name) {
        ProccessorDefCache.removeProccesserDef(name);
        List<Proccessor> removeProccessors = ProccessorCache.removeProccessor(name);
        for (Proccessor p : removeProccessors) {
            register.unRegisterBean(p.getClass().getSimpleName());
        }
        ClassLoaderUtil.releaseJar(name);
    }

    /**
     * 加载插件到缓存中
     * 一个插件可能包括多个执行器
     *
     * @param name
     * @param path
     */
    private void loadPlugin(String name, String path) {
        File file = new File(path);

        // 插件及其对应的类
        HashMap<String, List<String>> pluginMap = new HashMap<>();

        try {
            // 扫描得到插件所有的class文件
            ClassLoader classLoader = ClassLoaderUtil.getClassLoader(path, name);
            List<String> list = ClassUtil.getClassListSimple(file);
            pluginMap.put(name, list);

            pluginMap.forEach((s, strings) -> strings.forEach(clazzPath -> {
                try {
                    Class<?> clazz = classLoader.loadClass(clazzPath);
                    // 将proccesser注册到spring bean中
                    if (clazzPath.endsWith("Proccesser")) {
                        register.registerBean(clazz.getSimpleName(), clazz);
                        Proccessor proccessor = (Proccessor) register.getBean(clazz.getSimpleName());
                        ProccessorCache.addProccessor(name, clazz.getSimpleName(), proccessor);
                        logger.info("Plugin {} register complete.", clazz.getSimpleName());
                    }
                    if (clazzPath.endsWith("Def")) {
                        ProccessorDefCache.addProccesserDef(name, clazz);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("类未找到: {}", e.getMessage());
                } catch (NullPointerException e) {
                    logger.error("类加载器为空: {}", e.getMessage());
                }
            }));
        } catch (MalformedURLException e) {
            logger.error("Jar加载失败: {}", e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("文件未找到: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("文件读取失败: {}", e.getMessage());
        }
    }

    @Override
    public void loadAll() {
        logger.info("Load plugins...");
        // 加载所有插件
        Set<PluginInfo> pluginInfos = new HashSet<>(repository.findAll());

        if (pluginInfos.size() == 0) {
            logger.info("No plugins found...");
            return;
        }

        pluginInfos.forEach(pluginInfo -> {
            // 扫描得到插件所有的class文件
            String pluginName = pluginInfo.getName();
            logger.info("Plugin {} loading...", pluginName);
            // 插件全路径
            String jarPath = pluginInfo.getPath();
            try {
                // 插件及其对应的类列表
                HashMap<String, List<String>> pluginMap = new HashMap<>();
                ClassLoader classLoader = ClassLoaderUtil.getClassLoader(jarPath, pluginName);
                List<String> list = ClassUtil.getClassListSimple(jarPath);
                pluginMap.put(pluginName, list);
                pluginMap.forEach((s, strings) -> strings.forEach(clazzPath -> {
                    try {
                        Class<?> clazz = classLoader.loadClass(clazzPath);
                        // 将执行器注册到spring bean中
                        if (clazzPath.endsWith("Proccessor")) {
                            register.registerBean(clazz.getSimpleName(), clazz);
                            Proccessor proccessor = (Proccessor) register.getBean(clazz.getSimpleName());
                            // 添加到缓存中
                            ProccessorCache.addProccessor(pluginName, clazz.getSimpleName(), proccessor);
                            logger.info("Plugin {} register complete.", clazz.getSimpleName());
                        }
                        // 将参数类添加到缓存中
                        if (clazzPath.endsWith("Def")) {
                            ProccessorDefCache.addProccesserDef(pluginName, clazz);
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
                logger.error("插件文件未找到: {}", e.getMessage());
            } catch (IOException e) {
                logger.error("类读取失败: {}", e.getMessage());
            }
        });
    }
    // TODO 从缓存中移除插件
}
