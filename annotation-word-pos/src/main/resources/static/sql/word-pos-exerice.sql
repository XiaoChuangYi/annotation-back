DROP TABLE IF EXISTS `annotation_word_exercise`;
CREATE TABLE `annotation_word_exercise` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `origin_text` text  COMMENT '原始文本',
  `standard_annotation` text  not NULL DEFAULT '' COMMENT '标准答案标注',
  `auto_annotation` text  not NULL DEFAULT '' COMMENT '预标注',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `memo` varchar(512)  DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DROP TABLE IF EXISTS `user_word_exercise`;
CREATE TABLE `user_word_exercise` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `origin_text` text  COMMENT '原始文本',
  `practice_annotation` text  NOT NULL  DEFAULT  '' COMMENT '用户练习标注',
  `standard_annotation` text  NOT NULL DEFAULT '' COMMENT '标准答案标注',
  `state` varchar(64)  DEFAULT '' COMMENT '标注状态',
  `user_modifier` int(10) DEFAULT 1 COMMENT '用户id',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `memo` varchar(512)  DEFAULT '' COMMENT '备注',
  `anId` int(10) not NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `anId_modified` (`anId`,`user_modifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;