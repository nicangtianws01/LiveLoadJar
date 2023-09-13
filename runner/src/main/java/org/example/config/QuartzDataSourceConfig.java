package org.example.config;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryQuartz",
        transactionManagerRef = "transactionManagerQuartz",
        basePackages = {"org.example.repository.quartz"})
public class QuartzDataSourceConfig {

    @Resource(name = "quartzDataSource")
    private DataSource quartzDataSource;

    @Resource
    JpaProperties jpaProperties;

    @Bean(name = "entityManagerQuartz")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactorySecondary(builder).getObject()).createEntityManager();
    }

    @Bean(name = "entityManagerFactoryQuartz")
    LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(quartzDataSource)
                .properties(jpaProperties.getProperties())
                .packages("org.example.entity.quartz")
                .persistenceUnit("pu2")
                .build();
    }

    @Bean(name = "transactionManagerQuartz")
    PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactorySecondary(builder).getObject()));
    }
}
