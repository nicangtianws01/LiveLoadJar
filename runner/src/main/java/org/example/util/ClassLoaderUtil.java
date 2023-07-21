package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 获取classLoader
 * @author nicangtianws01
 * @since 0.1
 */
public final class ClassLoaderUtil {

    private ClassLoaderUtil(){}

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);
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
            logger.error("Get ClassLoader error: ", e);
            return null;
        }
    }
}
