/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : jiayang

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 26/03/2025 17:52:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_comparison_table
-- ----------------------------
DROP TABLE IF EXISTS `data_comparison_table`;
CREATE TABLE `data_comparison_table`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `target_sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标样本名',
  `compare_sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比对样本名',
  `step` int NULL DEFAULT NULL COMMENT '容差',
  `haplogroup` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单倍群',
  `comparison_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '比对时间，默认当前时间',
  `file_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件来源（如上传的文件名）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_comparison_table
-- ----------------------------
INSERT INTO `data_comparison_table` VALUES (8, 't3', 'SETCMGI0426CS01', 13, NULL, '2025-03-26 15:42:59', NULL);
INSERT INTO `data_comparison_table` VALUES (46, 't2', 'SETCMGI0426CS01', 17, NULL, '2025-03-26 17:04:17', NULL);
INSERT INTO `data_comparison_table` VALUES (48, 't2', 't3', 8, NULL, '2025-03-26 17:04:17', NULL);

-- ----------------------------
-- Table structure for mitochondrial_detail
-- ----------------------------
DROP TABLE IF EXISTS `mitochondrial_detail`;
CREATE TABLE `mitochondrial_detail`  (
  `sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '样本名',
  `analysis_date` datetime NOT NULL COMMENT '分析日期',
  `original_data_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始数据名',
  PRIMARY KEY (`sample_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mitochondrial_detail
-- ----------------------------
INSERT INTO `mitochondrial_detail` VALUES ('SETCMGI0426CS01', '2025-03-13 09:33:11', 'FT100045627_L01_1069');
INSERT INTO `mitochondrial_detail` VALUES ('t2', '2025-03-26 10:06:08', '5P240604031UY192714BX_5_L01');
INSERT INTO `mitochondrial_detail` VALUES ('t3', '2025-03-26 10:06:08', '5P240604031UY192714BX_6_L01');

-- ----------------------------
-- Table structure for site_info
-- ----------------------------
DROP TABLE IF EXISTS `site_info`;
CREATE TABLE `site_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '样本名',
  `base_position` int NOT NULL COMMENT '碱基位置',
  `reference_base` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参考碱基',
  `mutant_base` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '突变碱基',
  `total_depth` int NOT NULL COMMENT '总深度',
  `heterogeneity` decimal(5, 2) NOT NULL COMMENT '异质性',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sample_name`(`sample_name` ASC) USING BTREE,
  CONSTRAINT `site_info_ibfk_1` FOREIGN KEY (`sample_name`) REFERENCES `mitochondrial_detail` (`sample_name`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of site_info
-- ----------------------------
INSERT INTO `site_info` VALUES (67, 'SETCMGI0426CS01', 263, 'A', 'G', 1051, 0.11, 'SNP');
INSERT INTO `site_info` VALUES (68, 'SETCMGI0426CS01', 489, 'T', 'C', 29, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (69, 'SETCMGI0426CS01', 499, 'G', 'C', 26, 42.11, 'SNP');
INSERT INTO `site_info` VALUES (70, 'SETCMGI0426CS01', 545, 'G', 'C', 21, 38.89, 'SNP');
INSERT INTO `site_info` VALUES (71, 'SETCMGI0426CS01', 16129, 'G', 'A', 1019, 2.08, 'SNP');
INSERT INTO `site_info` VALUES (72, 'SETCMGI0426CS01', 16164, 'A', 'G', 1028, 32.74, 'SNP');
INSERT INTO `site_info` VALUES (73, 'SETCMGI0426CS01', 16223, 'C', 'T', 21, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (74, 'SETCMGI0426CS01', 16311, 'T', 'C', 929, 2.75, 'SNP');
INSERT INTO `site_info` VALUES (75, 'SETCMGI0426CS01', 16357, 'T', 'C', 917, 0.24, 'SNP');
INSERT INTO `site_info` VALUES (76, 'SETCMGI0426CS01', 16497, 'A', 'G', 1004, 0.71, 'SNP');
INSERT INTO `site_info` VALUES (77, 'SETCMGI0426CS01', 16519, 'T', 'C', 1011, 1.21, 'SNP');
INSERT INTO `site_info` VALUES (101, 't3', 8270, 'CACCCCCTCTACCCCCTCTA', 'CACCCCCTCTA', 43, 0.00, 'INDEL');
INSERT INTO `site_info` VALUES (102, 't3', 73, 'A', 'G', 33, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (103, 't2', 247, 'GAA', 'GA', 67, 0.00, 'INDEL');
INSERT INTO `site_info` VALUES (104, 't2', 310, 'TCCCCC', 'TCCCCCC', 67, 2.99, 'SNP');
INSERT INTO `site_info` VALUES (105, 't2', 436, 'C', 'T', 26, 33.33, 'SNP');
INSERT INTO `site_info` VALUES (106, 't2', 8149, 'A', 'G', 147, 0.68, 'SNP');
INSERT INTO `site_info` VALUES (107, 't2', 16519, 'T', 'C', 180, 0.56, 'SNP');
INSERT INTO `site_info` VALUES (108, 't2', 73, 'A', 'G', 180, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (109, 't2', 150, 'C', 'T', 180, 1.11, 'SNP');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userRole` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123', 'admin');

SET FOREIGN_KEY_CHECKS = 1;
