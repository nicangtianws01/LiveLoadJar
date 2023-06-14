package org.example.proccesser.file;

import org.example.common.ProccesserDef;
import lombok.Getter;
import lombok.Setter;
import org.example.common.anno.JsonTypeDef;

@Getter
@Setter
@JsonTypeDef(value = "fileReader")
public class FileReaderDef extends ProccesserDef {
    protected static final String PROCCESSER_NAME = "fileReader";

    private String fileName;
}
