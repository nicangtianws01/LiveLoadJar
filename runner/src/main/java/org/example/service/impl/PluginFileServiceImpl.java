package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.PluginFileService;
import org.example.util.ClassLoaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@Slf4j
@Service
public class PluginFileServiceImpl implements PluginFileService {

    @Value("${org.example.plugin.path.target}")
    String targetPath;

    @Value("${org.example.plugin.path.tmp}")
    String tmpPath;

    @Value("${org.example.plugin.path.deleted}")
    String deletedPath;

    public void deleteFile(String name) {
        try {
            File pluginFile = new File(targetPath + "/" + name);
            File deletedFile = new File(deletedPath + "/" + name);
            Files.move(pluginFile.toPath(), deletedFile.toPath());
        } catch (IOException e) {
            log.error("插件删除失败: {}", e.getMessage());
        }
    }

    @Override
    public boolean deletePlugin(String pluginName, String path) {
        if(ClassLoaderUtil.releaseJar(pluginName)){
            deleteFile(path);
            return true;
        }
        return false;
    }

    @Override
    public void persistence(String tmpName) {
        try{
            File tmpFile = new File(tmpPath + "/" + tmpName);
            File targetFile = new File(targetPath + "/" + tmpFile.getName());
            Files.move(tmpFile.toPath(), targetFile.toPath());
        }catch (IOException e){
            log.error("插件移动到目标路径失败");
        }
    }
}
