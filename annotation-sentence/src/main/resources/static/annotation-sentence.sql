drop table if EXISTS `annotation_sentence`;
create table `annotation_sentence`(
	`id` int UNSIGNED not null auto_increment,
	`origin_text` text not null default '' comment '原始文本',
	`annotation_text` text not null default '' comment '标注内容',
	`state` varchar(64) DEFAULT '' comment '标注状态',
	`user_modifier` varchar(32) DEFAULT '' comment '用户id',
	`gmt_created` timestamp not null Default CURRENT_TIMESTAMP comment '创建时间',
	`gmt_modified` timestamp not null Default CURRENT_TIMESTAMP comment '更新时间',
	`memo` VARCHAR(512) Default '' comment '备注',
	 primary key (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


drop table if EXISTS `user_account`;
create table `user_account`(
	`id` int  UNSIGNED not null auto_increment,
	`account_name` varchar(64) not null comment '用户名称',
	`password` varchar(64) not null comment '密码',
	`role` varchar(64) not null comment '角色/标注员,审核员,管理员',
	`state` varchar(512) default '',
	`gmt_created` timestamp not null Default CURRENT_TIMESTAMP comment '创建时间',
	`gmt_modified` timestamp not null Default CURRENT_TIMESTAMP comment '更新时间',
	 PRIMARY KEY(`id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

drop table if EXISTS `user_task`;
create table `user_task`(
	`id` int UNSIGNED not null auto_increment,
	`user_id` int not null comment '用户id',
	`task_id` int not null comment '任务id',
	`task_name` varchar(64) comment '任务名称',
	`gmt_created` timestamp not null Default CURRENT_TIMESTAMP comment '创建时间',
	`gmt_modified` timestamp not null Default CURRENT_TIMESTAMP comment '更新时间',
	 primary key(`id`),
	UNIQUE key `user_task_key` (`user_id`,`task_id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

对annotation_sentence表新增字段final_annotation_text
ALTER table annotation_sentence add final_annotation_text text not null DEFAULT ''

