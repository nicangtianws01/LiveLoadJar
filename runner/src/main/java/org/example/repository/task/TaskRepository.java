package org.example.repository.task;

import org.example.entity.task.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface TaskRepository extends JpaRepository<TaskConfig,Long> {
    TaskConfig findByName(String name);

    TaskConfig findByGroupAndName(String group, String name);
}
