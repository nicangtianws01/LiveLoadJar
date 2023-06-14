package org.example.proccesser.inside;

import lombok.extern.slf4j.Slf4j;
import org.example.common.Proccesser;
import org.example.common.ProccesserDef;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InsideProccesser implements Proccesser<InsideDef> {

    @Override
    public boolean canProccess(ProccesserDef def) {
        return InsideDef.PROCCESSER_NAME.equalsIgnoreCase(def.getName());
    }

    @Override
    public void run(InsideDef def) {
        log.info("Inside Proccess.");
    }
}
