package org.example.common;

import java.io.Serializable;

public interface Proccesser<T> extends Serializable {
    boolean canProccess(ProccesserDef def);
    void run(T def);
}
