package org.example.entity.plugin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 包含已保存的插件的信息
 * @author sena
 * @since v1.0
 */
@Getter
@Setter
@Entity
@Table(name = "plugin_info")
public class PluginInfo implements Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 插件名称
     */
    @Column
    private String name;
    /**
     * 插件保存全路径
     */
    @Column
    private String path;
}
