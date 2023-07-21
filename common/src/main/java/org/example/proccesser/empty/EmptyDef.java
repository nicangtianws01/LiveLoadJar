package org.example.proccesser.empty;

import lombok.Getter;
import lombok.Setter;
import org.example.common.ProccesserDef;
import org.example.common.anno.JsonTypeDef;

@Getter
@Setter
@JsonTypeDef(value = "empty")
public class EmptyDef extends ProccesserDef {
    protected static final String PROCCESSER_NAME = "empty";
}
