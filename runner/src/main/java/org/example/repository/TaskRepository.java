package org.example.repository;

import org.example.entity.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskConfig,Long> {
    TaskConfig findByName(String name);
}
