package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 获取classLoader
 * @author nicangtianws01
 * @since 0.1
 */
@Slf4j
public final class ClassLoaderUtil {

    private ClassLoaderUtil(){}

    public static ClassLoader getClassLoader(URL url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, Thread.currentThread().getContextClassLoader());
            if (!method.canAccess(classLoader)) {
                method.setAccessible(true);
            }
            method.invoke(classLoader, url);
            return classLoader;
        } catch (Exception e) {
            log.error("Get ClassLoader error: ", e);
            return null;
        }
    }
}
