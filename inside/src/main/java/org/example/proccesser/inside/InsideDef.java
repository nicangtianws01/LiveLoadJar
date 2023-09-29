package org.example.proccesser.inside;

import lombok.Getter;
import lombok.Setter;
import org.example.common.ProccessorDef;
import org.example.common.anno.JsonTypeDef;

@Getter
@Setter
@JsonTypeDef(value = "inside")
public class InsideDef extends ProccessorDef {
    protected static final String PROCCESSOR_NAME = "InsideProccessor";
}
