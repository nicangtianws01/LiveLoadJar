package org.example.cache;

import org.example.common.Proccesser;

import java.util.HashMap;
import java.util.Map;


public class ProccesserCache {

    // 使用map防止重复，查找快速
    private final static Map<String, Proccesser> proccessers = new HashMap<>();

    public static Map<String, Proccesser> getProccessers(){
        return proccessers;
    }

    public static void addProccessers(Map<String, Proccesser> pcs){
        proccessers.putAll(pcs);
    }

    public static void addProccesser(String name, Proccesser proccesser){
        proccessers.put(name, proccesser);
    }
}
