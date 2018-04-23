drop table if EXISTS `annotation_appose`;
create table `annotation_appose`(
	`id` int UNSIGNED not null auto_increment,
	`origin_text` text  comment '原始文本',
	`annotation_text` text comment '标注内容',
	`state` varchar(64) DEFAULT '未分配' comment '标注状态',
	`user_modifier` varchar(32) DEFAULT '' comment '用户id',
	`gmt_created` timestamp not null Default CURRENT_TIMESTAMP comment '创建时间',
	`gmt_modified` timestamp not null Default CURRENT_TIMESTAMP comment '更新时间',
	`memo` VARCHAR(512) Default '' comment '备注',
	 primary key (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;