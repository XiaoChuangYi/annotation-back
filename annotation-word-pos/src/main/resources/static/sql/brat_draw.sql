/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost
 Source Database       : annotation_dev

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : utf-8

 Date: 04/27/2018 10:17:10 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `brat_draw`
-- ----------------------------
DROP TABLE IF EXISTS `brat_draw`;
CREATE TABLE `brat_draw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `draw_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `type_label` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `type_id` int(11) NOT NULL DEFAULT '0' COMMENT '类型ID，关联type的主键',
  `task_id` int(11) DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
--  Records of `brat_draw`
-- ----------------------------
BEGIN;
INSERT INTO `brat_draw` VALUES ('1', 'fgColor:#F5ECEC,bgColor:#FF9500', '身体结构|体', '1', '1'), ('2', 'fgColor:#adfb9e,bgColor:#cccdfb', '区域|Z', '2', '1'), ('3', 'bgColor:#CC0033', '疾病|病', '3', '1'), ('4', 'fgColor:#FFFFFF,bgColor:#FF0000', '错误|误', '4', '1'), ('5', 'fgColor:#FFFFFF,bgColor:#7D0552', '未知|未', '5', '1'), ('6', 'bgColor:#FFCCCC', '临床发现|CF', '6', '1'), ('7', 'bgColor:#FF6666', '病态结构|病体|bt', '7', '1'), ('8', 'bgColor:#99CCFF', '操作|cz', '8', '1'), ('9', 'bgColor:#0099CC', '方案|fa', '9', '1'), ('10', 'bgColor:#0099CC', '放', '10', '1'), ('11', 'bgColor:#99CCCC', '手术|术', '11', '1'), ('12', 'bgColor:#336699', '疗法|疗', '12', '1'), ('13', 'bgColor:#336699', '查', '13', '1'), ('14', 'bgColor:#CCCC33', '观察对象|察', '15', '1'), ('15', 'bgColor:#99CC00', '药', '16', '1'), ('16', '', '分期与等级|级', '17', '1'), ('17', '', '分子生物学|分', '18', '1'), ('18', 'bgColor:#CCCCFF', '修饰词|饰', '19', '1'), ('19', '', '空间|S', '20', '1'), ('20', 'bgColor:#9966CC', '程度指示|程度|度', '21', '1'), ('21', 'fgColor:#F1E5E5,bgColor:#8AB8E3', '逻辑提示词|逻辑|逻', '22', '1'), ('22', 'bgColor:#90E81D', '单位|U', '23', '1'), ('23', 'fgColor:#43C6DB', '时间|T', '24', '1'), ('24', 'fgColor:#2D2222,bgColor:#4F86DE', '数', '26', '1'), ('25', '', '可能性|P', '27', '1'), ('26', 'fgColor:#141212,bgColor:#FFFEFE', '频', '28', '1'), ('27', '', '标本和样本|样', '29', '1'), ('28', '', '社会|社', '30', '1'), ('29', '', '家庭|家', '31', '1'), ('30', '', '生物体|生', '32', '1'), ('31', 'fgColor:#FFF380,bgColor:#7D0552', '环境和地理位置|环境位置|位', '33', '1'), ('32', 'fgColor:#FFD904,bgColor:#087777', '物', '34', '1'), ('33', '', '物理对象|PO', '35', '1'), ('34', '', '物理力|力', '36', '1'), ('35', '', '事件|事', '37', '1'), ('36', 'bgColor:ghostwhite', '单词|词', '38', '1'), ('37', 'bgColor:cornsilk', '句末|句', '39', '1'), ('38', '', '完成|完', '40', '1'), ('39', 'fgColor:#CCCCFF,bgColor:#43C6DB', '情', '41', '1'), ('40', 'fgColor:#040404,bgColor:#b8d661,dashArray:3-3', '药', '42', '1'), ('41', 'fgColor:#CC2216,bgColor:#BB83EB,dashArray:3-3', 's', '43', '1'), ('42', 'dashArray:3-3,fgColor:white,bgColor:#7D0552', '未知|未', '44', '1'), ('43', 'dashArray:3-3,bgColor:ghostwhite', '单词|词', '45', '1'), ('44', 'dashArray:3-3,bgColor:cornsilk', '句末|句', '46', '1'), ('45', 'dashArray:3-3', '完成|完', '47', '1'), ('46', 'dashArray:3-3,bgColor:#FFCCCC', '临床发现|CF', '48', '1'), ('47', 'dashArray:3-3,bgColor:#CC0033', '疾病|病', '49', '1'), ('48', 'dashArray:3-3,bgColor:#FF9900', '身体结构|体', '50', '1'), ('49', 'dashArray:3-3,bgColor:#FF6666', '病体|bt|病态结构', '51', '1'), ('50', 'dashArray:3-3,bgColor:#99CCFF', '操作|cz', '52', '1'), ('51', 'dashArray:3-3,bgColor:#0099CC', '方案|fa', '53', '1'), ('52', 'dashArray:3-3,bgColor:#99CCCC', '手术|术', '54', '1'), ('53', 'dashArray:3-3,bgColor:#336699', '疗法|疗', '55', '1'), ('54', 'dashArray:3-3,bgColor:#CCCC33', '观察对象|察', '56', '1'), ('55', 'dashArray:3-3', '生物体|生', '57', '1'), ('56', 'dashArray:3-3', '物质|物', '58', '1'), ('57', 'dashArray:3-3', '物理力|力', '59', '1'), ('58', 'dashArray:3-3', '事件|事', '60', '1'), ('59', 'dashArray:3-3,fgColor:#FFF380,bgColor:#7D0552', '环境位置|位', '61', '1'), ('60', 'dashArray:3-3', '级', '62', '1'), ('61', 'dashArray:3-3', '样', '63', '1'), ('62', 'dashArray:3-3,bgColor:#CCCCFF', '饰', '64', '1'), ('63', 'dashArray:3-3', '区域|Z', '65', '1'), ('64', 'dashArray:3-3,bgColor:#9966CC', '程度|度', '66', '1'), ('65', 'dashArray:3-3', '逻辑|逻', '67', '1'), ('66', 'dashArray:3-3', '单位|U', '68', '1'), ('67', 'dashArray:3-3,fgColor:#43C6DB', '时间|T', '69', '1'), ('68', 'dashArray:3-3', '数字|N', '70', '1'), ('69', 'dashArray:3-3', '可能性|P', '71', '1'), ('70', 'dashArray:3-3', '物理对象|PO', '72', '1'), ('71', 'dashArray:3-3', '社会|社', '73', '1'), ('72', 'dashArray:3-3', '家庭|家', '74', '1'), ('73', 'dashArray:3-3', '分', '75', '1'), ('74', 'fgColor:#cccdfb,bgColor:#69c4d8,dashArray:3-3', '情', '76', '1'), ('75', 'bgColor:#0099CC,dashArray:3-3', '放', '77', '1'), ('76', 'bgColor:#336699,dashArray:3-3', '查', '78', '1'), ('77', 'fgColor:#CCCCFF,bgColor:#43C6DB', 'T', '80', '1'), ('78', 'fgColor:#CCCCFF,bgColor:#43C6DB,dashArray:3-3', 'T', '81', '1'), ('79', 'fgColor:#CCCCFF,bgColor:#43C6DB', 'T', '82', '1'), ('80', 'fgColor:#CCCCFF,bgColor:#43C6DB,dashArray:3-3', 'T', '83', '1'), ('81', 'fgColor:#CCCCFF,bgColor:#43C6DB', 'T', '84', '1'), ('82', 'fgColor:#CCCCFF,bgColor:#43C6DB,dashArray:3-3', 'T', '85', '1'), ('83', 'dashArray:3-3', '频', '86', '1'), ('84', 'fgColor:#EA0F0F', '金额', '90', '1'), ('85', 'fgColor:#1EB7D6', '诊断', '91', '1'), ('86', 'fgColor:#0073FF', '药', '92', '1'), ('87', 'fgColor:#93D61E', '装备', '93', '1'), ('88', 'fgColor:#C7D61E', '服务', '94', '1'), ('89', 'fgColor:#91BFBC', '类别', '95', '1'), ('90', 'fgColor:#F1E7E7,bgColor:#F2C910', '测试|测', '96', '1'), ('91', 'fgColor:#FBF6F6,bgColor:#FFB700', '句末', '97', '1'), ('92', 'fgColor:#F7EFEF,bgColor:#EEB12D', '句末|末', '98', '1'), ('93', 'fgColor:#FCF7F7,bgColor:#FFB700,dashArray:3-3', '句末|末', '99', '1'), ('97', 'fgColor:#F5F3F3,bgColor:#EB1414', '否定词|否', '100', '1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
