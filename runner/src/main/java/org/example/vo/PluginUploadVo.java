package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PluginUploadVo {
    private String userId;
    private String pluginName;
    private String pluginPath;
    @JsonProperty("isOverride")
    private boolean isOverride;
}
