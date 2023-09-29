package org.example.service;

public interface PluginFileService {
    /**
     * 删除文件
     * @param path 文件路径
     */
    boolean deletePlugin(String pluginName, String path);

    /**
     * 持久化插件
     * @param tmpPath 临时路径
     */
    void persistence(String tmpPath);
}
