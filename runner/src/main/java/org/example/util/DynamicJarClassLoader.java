package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DynamicJarClassLoader extends URLClassLoader{
    private static boolean canCloseJar = false;
    private List<JarURLConnection> cachedJarFiles;

    static {
        try {
            URLClassLoader.class.getMethod("close");
            canCloseJar = true;
        } catch (NoSuchMethodException e) {
            log.error("close method not found in URLClassLoader");
        } catch (SecurityException e) {
            log.error("{}", e.getMessage());
        }
    }


    public DynamicJarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        init(urls);
    }

    private void init(URL[] urls) {
        cachedJarFiles = canCloseJar ? null : new ArrayList<>();
        if (urls != null) {
            for (URL url : urls) {
                this.addURL(url);
            }
        }
    }

    @Override
    protected void addURL(URL url) {
        if (!canCloseJar) {
            try {
                // 打开并缓存文件url连接
                URLConnection uc = url.openConnection();
                if (uc instanceof JarURLConnection) {
                    uc.setUseCaches(true);
                    ((JarURLConnection) uc).getManifest();
                    cachedJarFiles.add((JarURLConnection) uc);
                }
            } catch (IOException e) {
                log.info("{}", e.getMessage());
            }
        }
        super.addURL(url);
    }

    public void close() throws IOException {
        if (canCloseJar) {
            try {
                super.close();
            } catch (IOException e) {
                log.info("{}", e.getMessage());
            }
        } else {
            for (JarURLConnection conn : cachedJarFiles) {
                conn.getJarFile().close();
            }
            cachedJarFiles.clear();
        }
    }
}
