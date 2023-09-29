package org.example.cache;

import org.example.common.ProccessorDef;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;


public class ProccessorDefCache {

    private ProccessorDefCache(){}

    private final static MultiValueMap<String, Class<?>> proccessorDefs = new LinkedMultiValueMap<>();

    public static void init(Set<Class<?>>  defs){
        proccessorDefs.put("inner", new ArrayList<>(defs));
    }

    public static List<Class<?>> getProccesserDefs(){
        return proccessorDefs.entrySet().stream().flatMap(e->e.getValue().stream()).collect(Collectors.toList());
    }

    public static void addProccesserDefs(String pluginName, List<Class<?>> pcs){
        proccessorDefs.addAll(pluginName, pcs);
    }

    public static void addProccesserDef(String pluginName, Class<?> def){
        proccessorDefs.add(pluginName, def);
    }

    public static void removeProccesserDef(String pluginName){
        proccessorDefs.remove(pluginName);
    }

}
