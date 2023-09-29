package org.example.cache;

import org.example.common.Proccessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProccessorCache {

    private ProccessorCache(){}

    // 使用map防止重复，查找快速
    private final static Map<String, Proccessor> proccessors = new HashMap<>();

    private final static MultiValueMap<String, Proccessor> pluginProccessors = new LinkedMultiValueMap<>();

    public static Map<String, Proccessor> getProccessors(){
        return proccessors;
    }

    public static void addProccessors(Map<String, Proccessor> pcs){
        proccessors.putAll(pcs);
    }

    public static void addProccessor(String name, Proccessor proccessor){
        proccessors.put(name, proccessor);
    }

    public static void addProccessor(String pluginName, String name, Proccessor proccessor){
        pluginProccessors.add(pluginName, proccessor);
        proccessors.put(name, proccessor);
    }

    public static List<Proccessor> removeProccessor(String pluginName){
        List<Proccessor> removeProccessors = pluginProccessors.remove(pluginName);
        for (Proccessor p : removeProccessors) {
            ProccessorCache.proccessors.remove(p.getClass().getSimpleName());
        }
        return removeProccessors;
    }
}
