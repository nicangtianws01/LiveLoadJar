package org.example.proccesser.empty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.example.common.ProccesserDef;
import org.example.common.anno.JsonTypeDef;

/**
 * 空事件处理器参数
 */
@Getter
@Setter
@JsonTypeDef(value = "empty")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmptyDef extends ProccesserDef {
    protected static final String PROCCESSER_NAME = "empty";
}
