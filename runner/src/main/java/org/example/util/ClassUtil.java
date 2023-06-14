package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public final class ClassUtil {

    private ClassUtil(){}

    private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

    private static final String DOT_CLASS = ".class";
    private static final int DOT_CLASS_LEN = DOT_CLASS.length();

    /**
     * 从指定的 jar 文件中获取类的路径列表
     * @param jarFile File jar 文件对象
     * @param parents Class<?>[] 继承或者实现的接口
     * @param inner 是否包含内部类
     * @param contain 类路径中含有的字符串，比如 .function.
     * @param notContain 类路径中不含有的字符串 .gui.
     * @return List<String>
     * @throws IOException
     */
    public static List<String> getClassList(File jarFile, Class<?>[] parents, boolean inner, String contain, String notContain) throws IOException {
        Set<String> listClasses = new TreeSet<>();

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String strEntry = entries.nextElement().toString();
                if (strEntry.endsWith(DOT_CLASS)) {
                    String fixedClassName = fixClassName(strEntry);
                    if (accept(parents, fixedClassName, contain, notContain, inner)) {
                        listClasses.add(fixedClassName);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e) {
                }
            }
        }

        return new ArrayList<>(listClasses);
    }

    public static List<String> getClassListSimple(File jarFile) throws IOException {
        return getClassList(jarFile, new Class[]{}, false, null, null);
    }

    public static String fixClassName(String strClassName) {
        strClassName = strClassName.replace('\\', '.');
        strClassName = strClassName.replace('/', '.');
        // remove ".class"
        strClassName = strClassName.substring(0, strClassName.length() - DOT_CLASS_LEN);
        return strClassName;
    }

    public static boolean accept(Class<?>[] parents, String className, String contains, String notContains,
                                 boolean inner) {

        if (contains != null && !className.contains(contains)) {
            return false; // It does not contain a required string
        }
        if (notContains != null && className.contains(notContains)) {
            return false; // It contains a banned string
        }
        if (!className.contains("$") || inner) { // $NON-NLS-1$
            return parents.length == 0 || isChildOf(parents, className, Thread.currentThread().getContextClassLoader());
        }
        return false;
    }

    public static boolean isChildOf(Class<?>[] parentClasses, String strClassName, ClassLoader contextClassLoader) {
        // might throw an exception, assume this is ignorable
        try {
            Class<?> c = Class.forName(strClassName, false, contextClassLoader);

            if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                for (Class<?> parentClass : parentClasses) {
                    if (parentClass.isAssignableFrom(c)) {
                        return true;
                    }
                }
            }
        } catch (UnsupportedClassVersionError | ClassNotFoundException | NoClassDefFoundError e) {
            log.debug(e.getLocalizedMessage());
        }
        return false;
    }

}

