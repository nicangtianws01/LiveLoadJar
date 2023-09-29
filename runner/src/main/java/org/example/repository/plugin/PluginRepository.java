package org.example.repository.plugin;

import org.example.entity.plugin.PluginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PluginRepository extends JpaRepository<PluginInfo, Long> {
    PluginInfo findByName(String name);
}
