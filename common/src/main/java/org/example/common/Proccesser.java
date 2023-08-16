package org.example.common;

import java.io.Serializable;

/**
 * 事件处理器接口
 * 所有处理器都需要继承该接口
 * @param <T>
 */
public interface Proccesser<T> extends Serializable {
    /**
     * 判断事件处理器是否匹配
     * @param def
     * @return
     */
    boolean canProccess(ProccesserDef def);

    /**
     * 执行事件
     * @param def
     */
    void run(T def);
}
