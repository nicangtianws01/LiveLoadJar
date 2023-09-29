package org.example.proccesser.file;

import lombok.Getter;
import lombok.Setter;
import org.example.common.ProccessorDef;
import org.example.common.anno.JsonTypeDef;

@Getter
@Setter
@JsonTypeDef(value = "fileReader")
public class FileReaderDef extends ProccessorDef {
    protected static final String PROCCESSOR_NAME = "FileReaderProccessor";

    private String fileName;
}
