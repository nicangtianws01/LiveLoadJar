package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.runner.Runner;
import org.example.service.PluginFileService;
import org.example.service.PluginService;
import org.example.util.SpringBeanRegister;
import org.example.vo.PluginUploadVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Tag(name = "插件管理接口")
@Slf4j
@RestController
@RequestMapping("/api/v1/plugin")
public class PluginController {

    private final SpringBeanRegister register;

    private final PluginService pluginService;

    private final PluginFileService pluginFileService;

    private final Runner runner;

    @Value("${org.example.plugin.path.tmp}")
    private String tmpPluginPath;

    public PluginController(SpringBeanRegister register, PluginService pluginService, PluginFileService pluginFileService, Runner runner) {
        this.register = register;
        this.pluginService = pluginService;
        this.pluginFileService = pluginFileService;
        this.runner = runner;
    }

    /**
     * 保存插件信息并加载插件
     *
     * @return
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    public String pushPlugin(@RequestBody PluginUploadVo pluginUploadVo){
//        if(!pluginUploadVo.isOverride()){
//            if(pluginService.exists(pluginUploadVo.getPluginName())){
//                return "插件已存在";
//            }
//        }
//        if(!pluginFileService.deletePlugin(pluginUploadVo.getPluginName(), pluginUploadVo.getPluginPath())){
//            return "插件删除失败";
//        }
//        pluginFileService.persistence(pluginUploadVo.getPluginPath());
//        pluginService.load(pluginUploadVo.getPluginName(), pluginUploadVo.getPluginPath());
        pluginService.unload(pluginUploadVo.getPluginName());
        pluginFileService.deletePlugin(pluginUploadVo.getPluginName(), pluginUploadVo.getPluginPath());
        return "插件已保存";
    }

    /**
     * 上传插件到临时目录
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public String test(@RequestPart("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        File target = new File(tmpPluginPath + "/" + filename);
        file.transferTo(target);
        log.info("Plugin uploaded.");
        return target.getName();
    }

    @RequestMapping(value = "/runTask", method = RequestMethod.GET)
    public String runTask() throws IOException {

        // 读取配置文件
        StringBuilder configBuilder = new StringBuilder();
        File file = ResourceUtils.getFile("classpath:task-config/task01.json");

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader);
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                configBuilder.append(line);
            }
        }
        String config = configBuilder.toString();
        runner.run(config);
        return "任务执行完成!";
    }

    @RequestMapping(value = "/loadAll", method = RequestMethod.GET)
    public String loadAll() {
        pluginService.loadAll();
        return "ok";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "ok";
    }
}
