package org.example.cache;

import org.example.common.ProccesserDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProccesserDefCache {

    // 使用map防止重复，查找快速
    private final static List<Class<?>> proccesserDefs = new ArrayList<>();

    public static List<Class<?>> getProccesserDefs(){
        return proccesserDefs;
    }

    public static void addProccesserDefs(List<Class<?>> pcs){
        proccesserDefs.addAll(pcs);
    }

    public static void addProccesserDef(Class<?> def){
        proccesserDefs.add(def);
    }
}
