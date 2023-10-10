package org.example.entity.task;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 已保存的任务流的信息
 * @author sena
 * @since v1.0
 */
@Getter
@Setter
@Entity
@Table(name = "TASK_CONFIG")
public class TaskConfig implements Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 任务名称
     */
    @Column
    private String name;
    /**
     * 任务组
     */
    @Column
    private String group;
    /**
     * 任务配置
     */
    @Column
    private String config;
}
