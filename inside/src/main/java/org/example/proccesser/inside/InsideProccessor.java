package org.example.proccesser.inside;

import lombok.extern.slf4j.Slf4j;
import org.example.common.Proccessor;
import org.example.common.ProccessorDef;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InsideProccessor extends Proccessor {

    @Override
    public boolean canProccess(ProccessorDef def) {
        return InsideDef.PROCCESSOR_NAME.equalsIgnoreCase(def.getName());
    }

    @Override
    public void run(ProccessorDef def) {
        log.info("Inside Proccess.");
    }
}
