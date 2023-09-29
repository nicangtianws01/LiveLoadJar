package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取classLoader
 *
 * @author nicangtianws01
 * @since 0.1
 */
@Slf4j
public final class ClassLoaderUtil {

    private static final Map<String, JarURLConnection> cachedJarFiles = new HashMap<>();

    private ClassLoaderUtil() {
    }

    public static ClassLoader getClassLoader(String pluginPath, String pluginName) {
        try {
            // 打开并缓存文件url连接
            URL url = new URL("jar:file:" + pluginPath + "!/");
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            cachedJarFiles.put(pluginName, jarURLConnection);
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
            if (!method.canAccess(classLoader)) {
                method.setAccessible(true);
            }
            method.invoke(classLoader, url);
            return classLoader;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log.error("获取ClassLoader失败: {}", e.getMessage());
        } catch (IOException e) {
            log.error("缓存jar链接失败: {}", e.getMessage());
        }
        return null;
    }

    public static boolean releaseJar(String jarName) {
        try {
            JarURLConnection urlConnection = cachedJarFiles.get(jarName);
            if (urlConnection != null) {
                urlConnection.getJarFile().close();
            }
            System.gc();
            return true;
        } catch (IOException e) {
            log.error("插件释放失败");
        }
        return false;
    }

}
