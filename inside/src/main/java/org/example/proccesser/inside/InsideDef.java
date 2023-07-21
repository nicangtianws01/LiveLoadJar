package org.example.proccesser.inside;

import lombok.Getter;
import lombok.Setter;
import org.example.common.ProccesserDef;
import org.example.common.anno.JsonTypeDef;

@Getter
@Setter
@JsonTypeDef(value = "inside")
public class InsideDef extends ProccesserDef {
    protected static final String PROCCESSER_NAME = "inside";
}
