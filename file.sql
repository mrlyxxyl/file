/*
Navicat MySQL Data Transfer

Source Server         : 200
Source Server Version : 50641
Source Host           : 10.40.40.200:3306
Source Database       : file

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2018-11-02 15:57:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sys_file`
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_ip_address` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户端ip地址',
  `storage_root_path` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '本地存储根目录或云容器名',
  `file_path` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '文件路径',
  `file_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小（b）',
  `file_status` int(20) NOT NULL DEFAULT '1' COMMENT '文件状态 0-无效/1-有效',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件';

-- ----------------------------
-- Records of sys_file
-- ----------------------------
