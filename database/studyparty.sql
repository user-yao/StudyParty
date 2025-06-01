/*
 Navicat MySQL Data Transfer

 Source Server         : yzt
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : studyparty

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 30/05/2025 15:46:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for achievement
-- ----------------------------
DROP TABLE IF EXISTS `achievement`;
CREATE TABLE `achievement`  (
  `achievement_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成就名称（唯一）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '成就描述（用户可见）',
  `type` tinyint NOT NULL COMMENT '成就类型（1-一次性；2-阶段性；3-累积型）',
  `target_value` int NULL DEFAULT NULL COMMENT '目标值（如累积次数、积分阈值）',
  `reward_type` tinyint NULL DEFAULT NULL COMMENT '奖励类型（1-积分；2-物品；3-称号）',
  `reward_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖励值（积分数量/物品ID/称号名称）',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成就图标路径',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否生效（0-停用；1-启用）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`achievement_id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成就定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_user
-- ----------------------------
DROP TABLE IF EXISTS `achievement_user`;
CREATE TABLE `achievement_user`  (
  `owner_id` bigint UNSIGNED NOT NULL COMMENT '用户ID或小组ID',
  `achievement_id` bigint UNSIGNED NOT NULL COMMENT '成就ID',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`owner_id`, `achievement_id`) USING BTREE,
  INDEX `achievement_id`(`achievement_id`) USING BTREE,
  INDEX `idx_user`(`owner_id`) USING BTREE,
  CONSTRAINT `achievement_user_ibfk_1` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`achievement_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户成就进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uploader` int UNSIGNED NULL DEFAULT NULL COMMENT 'up主',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签（用于推荐）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `context_json` json NULL COMMENT '内容',
  `nice` int NULL DEFAULT NULL COMMENT '点赞',
  `bad` int NULL DEFAULT NULL COMMENT '不推荐',
  `collect` int NULL DEFAULT NULL COMMENT '收藏',
  `date` datetime NULL DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_comment
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `article_id` int UNSIGNED NULL DEFAULT NULL COMMENT '文章id',
  `user` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '评论内容',
  `nice` int NULL DEFAULT NULL COMMENT '点赞',
  `bad` int NULL DEFAULT NULL COMMENT '不推荐',
  `date` datetime NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `user`(`user`) USING BTREE,
  CONSTRAINT `article_comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_comment_ibfk_2` FOREIGN KEY (`user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_tags
-- ----------------------------
DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE `article_tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_id` int UNSIGNED NULL DEFAULT NULL,
  `tags_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `tags_id`(`tags_id`) USING BTREE,
  CONSTRAINT `article_tags_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_tags_ibfk_2` FOREIGN KEY (`tags_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bill
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user` int UNSIGNED NULL DEFAULT NULL COMMENT '用户',
  `type` tinyint NULL DEFAULT NULL COMMENT '类型（1用户积分，2团队积分，3学术声望）',
  `num` int NULL DEFAULT NULL COMMENT '数量',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容描述',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user`) USING BTREE,
  CONSTRAINT `bill_ibfk_1` FOREIGN KEY (`user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `leader` int UNSIGNED NOT NULL COMMENT '组长',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '小组名称',
  `group_level` int NULL DEFAULT NULL COMMENT '小组等级',
  `suffer` int NULL DEFAULT NULL COMMENT '经验值',
  `people_name` int NULL DEFAULT NULL COMMENT '当前小组人数',
  `max_people_num` int NULL DEFAULT NULL COMMENT '最大小组人数',
  `slogan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '口号',
  `rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '规则',
  PRIMARY KEY (`id`, `leader`) USING BTREE,
  INDEX `leader`(`leader`) USING BTREE,
  INDEX `id`(`id`) USING BTREE,
  CONSTRAINT `group_ibfk_1` FOREIGN KEY (`leader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_tags
-- ----------------------------
DROP TABLE IF EXISTS `group_tags`;
CREATE TABLE `group_tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` int UNSIGNED NULL DEFAULT NULL,
  `tag_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `group_tags_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_user
-- ----------------------------
DROP TABLE IF EXISTS `group_user`;
CREATE TABLE `group_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` int UNSIGNED NOT NULL COMMENT '小组id',
  `group_user` int UNSIGNED NOT NULL COMMENT '用户id',
  `contribution` int NULL DEFAULT NULL COMMENT '贡献积分（小组升级）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `group_user`(`group_user`) USING BTREE,
  CONSTRAINT `group_user_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_user_ibfk_2` FOREIGN KEY (`group_user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for image
-- ----------------------------
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路径',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型（头像，图片，工具）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公告标题',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公告内容',
  `date` datetime NULL DEFAULT NULL COMMENT '公告日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路径',
  `date` datetime NULL DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `report_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uploader` int UNSIGNED NULL DEFAULT NULL COMMENT '上传者',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片路径',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '问题描述',
  `isOver` tinyint NULL DEFAULT NULL COMMENT '是否完成',
  `star_coin` int NULL DEFAULT NULL COMMENT '悬赏星币',
  `star_prestige` int NULL DEFAULT NULL COMMENT '星愿',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task_answer
-- ----------------------------
DROP TABLE IF EXISTS `task_answer`;
CREATE TABLE `task_answer`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` int UNSIGNED NULL DEFAULT NULL COMMENT '任务id',
  `answerer` int UNSIGNED NULL DEFAULT NULL COMMENT '回答者',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '答案文本',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路径',
  `isTrue` tinyint NULL DEFAULT NULL COMMENT '是否被认可',
  `nice` int NULL DEFAULT NULL COMMENT '点赞量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `answerer`(`answerer`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE,
  CONSTRAINT `task_answer_ibfk_1` FOREIGN KEY (`answerer`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_answer_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_tags
-- ----------------------------
DROP TABLE IF EXISTS `user_tags`;
CREATE TABLE `user_tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户id',
  `tag_id` int UNSIGNED NOT NULL COMMENT '标签id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_tag_unique`(`user_id`, `tag_id`) USING BTREE,
  INDEX `user_tags_ibfk_2`(`tag_id`) USING BTREE,
  CONSTRAINT `user_tags_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名（与手机号一致）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '头像',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '性别',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '年级',
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  `star_coin` int NULL DEFAULT NULL COMMENT '星币（学习积分）',
  `group_coin` int NULL DEFAULT NULL COMMENT '团币（团队积分）',
  `star_prestige` int NULL DEFAULT NULL COMMENT '星愿（学术声望）',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号码',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '学校',
  `clock_in` int NULL DEFAULT NULL COMMENT '打卡（记录连续打卡日期）',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
