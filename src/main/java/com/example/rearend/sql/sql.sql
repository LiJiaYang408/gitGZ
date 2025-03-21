SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mitochondrial_detail
-- ----------------------------
DROP TABLE IF EXISTS `mitochondrial_detail`;
CREATE TABLE `mitochondrial_detail`  (
                                         `sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '样本名',
                                         `analysis_date` datetime NOT NULL COMMENT '分析日期',
                                         `original_data_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始数据名',
                                         PRIMARY KEY (`sample_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mitochondrial_detail
-- ----------------------------
INSERT INTO `mitochondrial_detail` VALUES ('SETCMGI0426CS01', '2025-03-13 09:33:11', 'FT100045627_L01_1069');

-- ----------------------------
-- Table structure for site_info
-- ----------------------------
DROP TABLE IF EXISTS `site_info`;
CREATE TABLE `site_info`  (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `sample_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '样本名',
                              `base_position` int NOT NULL COMMENT '碱基位置',
                              `reference_base` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参考碱基',
                              `mutant_base` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '突变碱基',
                              `total_depth` int NOT NULL COMMENT '总深度',
                              `heterogeneity` decimal(5, 2) NOT NULL COMMENT '异质性',
                              `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `sample_name`(`sample_name` ASC) USING BTREE,
                              CONSTRAINT `site_info_ibfk_1` FOREIGN KEY (`sample_name`) REFERENCES `mitochondrial_detail` (`sample_name`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of site_info
-- ----------------------------
INSERT INTO `site_info` VALUES (1, 'SETCMGI0426CS01', 263, 'A', 'G', 1051, 0.11, 'SNP');
INSERT INTO `site_info` VALUES (2, 'SETCMGI0426CS01', 489, 'T', 'C', 29, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (3, 'SETCMGI0426CS01', 499, 'G', 'C', 26, 42.11, 'SNP');
INSERT INTO `site_info` VALUES (4, 'SETCMGI0426CS01', 545, 'G', 'C', 21, 38.89, 'SNP');
INSERT INTO `site_info` VALUES (5, 'SETCMGI0426CS01', 16129, 'G', 'A', 1019, 2.08, 'SNP');
INSERT INTO `site_info` VALUES (6, 'SETCMGI0426CS01', 16164, 'A', 'G', 1028, 32.74, 'SNP');
INSERT INTO `site_info` VALUES (7, 'SETCMGI0426CS01', 16223, 'C', 'T', 21, 0.00, 'SNP');
INSERT INTO `site_info` VALUES (8, 'SETCMGI0426CS01', 16311, 'T', 'C', 929, 2.75, 'SNP');
INSERT INTO `site_info` VALUES (9, 'SETCMGI0426CS01', 16357, 'T', 'C', 917, 0.24, 'SNP');
INSERT INTO `site_info` VALUES (10, 'SETCMGI0426CS01', 16497, 'A', 'G', 1004, 0.71, 'SNP');
INSERT INTO `site_info` VALUES (11, 'SETCMGI0426CS01', 16519, 'T', 'C', 1011, 1.21, 'SNP');

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
