CREATE TABLE `t_user` (
	`id` INT (11) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
	`username` VARCHAR (20) DEFAULT NULL COMMENT '用户名称',
	PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4;