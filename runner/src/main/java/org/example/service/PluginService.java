package org.example.service;

public interface PluginService {
    boolean exists(String name);

    void load(String name, String path);

    void unload(String name);

    void loadAll();
}
