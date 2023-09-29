package org.example.common;

import java.io.Serializable;

/**
 * 事件处理器接口
 * 所有处理器都需要继承该接口
 */
public abstract class Proccessor implements Serializable {
    /**
     * 判断事件处理器是否匹配
     *
     * @param def
     * @return
     */
    public abstract boolean canProccess(ProccessorDef def);

    /**
     * 执行事件
     * @param def
     */
    public abstract void run(ProccessorDef def);
}
