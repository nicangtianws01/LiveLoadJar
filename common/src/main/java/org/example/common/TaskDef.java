package org.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 任务流定义
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDef {
    private String name;
    private String desc;
    private Map<String, String> vars;
    private List<ProccesserDef> steps;
}
