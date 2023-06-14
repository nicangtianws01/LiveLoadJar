package org.example.proccesser.file;

import lombok.extern.slf4j.Slf4j;
import org.example.common.Proccesser;
import org.example.common.ProccesserDef;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileReaderProccesser implements Proccesser<FileReaderDef> {

    @Override
    public boolean canProccess(ProccesserDef def){
        return FileReaderDef.PROCCESSER_NAME.equalsIgnoreCase(def.getName());
    }

    @Override
    public void run(FileReaderDef def) {
        log.info("File reader proccess");
        log.info("File name: {}", def.getFileName());
    }
}
