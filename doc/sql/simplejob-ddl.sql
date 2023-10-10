use simpleJob;

CREATE TABLE if not EXISTS `task_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(30) DEFAULT NULL,
    `group` varchar(30) DEFAULT NULL,
    `config` text DEFAULT NULL,
    `store_time` TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (`id`)
    );

create TABLE if not EXISTS task_quartz(
                                          `id` BIGINT auto_increment,
                                          `name` VARCHAR(30),
    `group` VARCHAR(30),
    `cron` VARCHAR(60),
    `enable` boolean,
    `store_time` TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY(id)
    );

show create table task_config;