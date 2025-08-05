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

 Date: 05/08/2025 00:43:14
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
-- Records of achievement
-- ----------------------------

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
-- Records of achievement_user
-- ----------------------------

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uploader` int UNSIGNED NULL DEFAULT NULL COMMENT 'up主',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '内容',
  `nice` int NULL DEFAULT NULL COMMENT '点赞',
  `collect` int NULL DEFAULT NULL COMMENT '收藏',
  `view_count` int NULL DEFAULT NULL COMMENT '阅读量',
  `comment_count` int NULL DEFAULT NULL COMMENT '评论量',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `is_featured` tinyint NULL DEFAULT NULL COMMENT '是否推荐，0，不推荐，1，推荐',
  `stastus` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for article_collect
-- ----------------------------
DROP TABLE IF EXISTS `article_collect`;
CREATE TABLE `article_collect`  (
  `id` int NOT NULL,
  `article_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_collect
-- ----------------------------

-- ----------------------------
-- Table structure for article_comment
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `article_id` int UNSIGNED NULL DEFAULT NULL COMMENT '文章id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '评论内容',
  `nice` int NULL DEFAULT NULL COMMENT '点赞',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '评论时间',
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `user`(`user_id`) USING BTREE,
  CONSTRAINT `article_comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_comment
-- ----------------------------

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
-- Records of article_tags
-- ----------------------------

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
-- Records of bill
-- ----------------------------

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `leader` int UNSIGNED NOT NULL COMMENT '组长',
  `deputy` int UNSIGNED NULL DEFAULT NULL COMMENT '代理组长（用于轮换）',
  `deputy_time` datetime NULL DEFAULT NULL COMMENT '代理组长上任时间（轮换）',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '小组名称',
  `group_level` int NULL DEFAULT NULL COMMENT '小组等级',
  `experience` int NULL DEFAULT NULL COMMENT '经验值',
  `need_experience` int NULL DEFAULT NULL COMMENT '需要的经验值',
  `people_num` int NULL DEFAULT NULL COMMENT '当前小组人数',
  `max_people_num` int NULL DEFAULT NULL COMMENT '最大小组人数',
  `slogan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '口号',
  `rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '规则',
  `head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '小组头像',
  `create_Time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `can_join` tinyint NULL DEFAULT NULL COMMENT '1.可以加入 0.不可以加入',
  `teacher` int UNSIGNED NULL DEFAULT NULL COMMENT '负责老师',
  `enterprise` int UNSIGNED NULL DEFAULT NULL COMMENT '负责企业',
  PRIMARY KEY (`id`, `leader`) USING BTREE,
  INDEX `leader`(`leader`) USING BTREE,
  INDEX `id`(`id`) USING BTREE,
  INDEX `deputy`(`deputy`) USING BTREE,
  INDEX `teacher`(`teacher`) USING BTREE,
  INDEX `enterprise`(`enterprise`) USING BTREE,
  CONSTRAINT `group_ibfk_1` FOREIGN KEY (`leader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_ibfk_2` FOREIGN KEY (`deputy`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_ibfk_3` FOREIGN KEY (`teacher`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_ibfk_4` FOREIGN KEY (`enterprise`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (1, 3, NULL, NULL, '11111', 1, 11, NULL, 11, 1111, '11', '1111', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for group_join
-- ----------------------------
DROP TABLE IF EXISTS `group_join`;
CREATE TABLE `group_join`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` int UNSIGNED NULL DEFAULT NULL COMMENT '小组id',
  `group_leader` int UNSIGNED NULL DEFAULT NULL COMMENT '组长id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '申请内容',
  `is_pass` tinyint NULL DEFAULT NULL COMMENT '0.未判断 1.通过 2.拒绝',
  `join_time` timestamp NULL DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `group_leader`(`group_leader`) USING BTREE,
  CONSTRAINT `group_join_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_join_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_join_ibfk_3` FOREIGN KEY (`group_leader`) REFERENCES `group` (`leader`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_join
-- ----------------------------

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
-- Records of group_tags
-- ----------------------------

-- ----------------------------
-- Table structure for group_task
-- ----------------------------
DROP TABLE IF EXISTS `group_task`;
CREATE TABLE `group_task`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` int UNSIGNED NOT NULL COMMENT '小组id',
  `group_task` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '小组任务',
  `group_task_uploader` int UNSIGNED NULL DEFAULT NULL COMMENT '小组任务发布者',
  `group_task_start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `group_task_last_time` datetime NULL DEFAULT NULL COMMENT '最终完成时间',
  `group_task_finish` int NULL DEFAULT NULL COMMENT '完成人数',
  `group_task_context` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务内容',
  `group_task_unfinished` int NULL DEFAULT NULL COMMENT '未完成人数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  CONSTRAINT `group_task_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_task
-- ----------------------------

-- ----------------------------
-- Table structure for group_task_answer
-- ----------------------------
DROP TABLE IF EXISTS `group_task_answer`;
CREATE TABLE `group_task_answer`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_task_id` int UNSIGNED NULL DEFAULT NULL COMMENT '任务id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `context` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务答案',
  `time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  `have_source` tinyint NULL DEFAULT NULL COMMENT '0 没有，1 有，有没有静态资源',
  `score` int NULL DEFAULT NULL COMMENT '成绩',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_task_id`(`group_task_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `group_task_answer_ibfk_1` FOREIGN KEY (`group_task_id`) REFERENCES `group_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_task_answer_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `group_user` (`group_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_task_answer
-- ----------------------------

-- ----------------------------
-- Table structure for group_user
-- ----------------------------
DROP TABLE IF EXISTS `group_user`;
CREATE TABLE `group_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` int UNSIGNED NOT NULL COMMENT '小组id',
  `group_user` int UNSIGNED NOT NULL COMMENT '用户id',
  `contribution` int NULL DEFAULT NULL COMMENT '贡献积分（小组升级）',
  `add_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `group_user`(`group_user`) USING BTREE,
  CONSTRAINT `group_user_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_user_ibfk_2` FOREIGN KEY (`group_user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_user
-- ----------------------------
INSERT INTO `group_user` VALUES (1, 1, 3, 0, '2025-07-20 03:52:40');

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
-- Records of notice
-- ----------------------------

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
-- Records of report
-- ----------------------------

-- ----------------------------
-- Table structure for source
-- ----------------------------
DROP TABLE IF EXISTS `source`;
CREATE TABLE `source`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '访问路径',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件路径',
  `article_comment_id` int NULL DEFAULT NULL COMMENT '评论表Id',
  `article_id` int UNSIGNED NULL DEFAULT NULL COMMENT '文章表Id',
  `group_task_answer_id` int UNSIGNED NULL DEFAULT NULL COMMENT '小组任务答案id',
  `group_task_id` int UNSIGNED NULL DEFAULT NULL COMMENT '小组任务表ID',
  `achievement_id` int UNSIGNED NULL DEFAULT NULL COMMENT '成就表ID',
  `task_id` int NULL DEFAULT NULL COMMENT '任务表Id',
  `task_answer_id` int NULL DEFAULT NULL COMMENT '任务回答表Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of source
-- ----------------------------

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名称',
  `tag_context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tags
-- ----------------------------

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uploader` int UNSIGNED NULL DEFAULT NULL COMMENT '上传者',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '问题标题',
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
-- Records of task
-- ----------------------------

-- ----------------------------
-- Table structure for task_answer
-- ----------------------------
DROP TABLE IF EXISTS `task_answer`;
CREATE TABLE `task_answer`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` int UNSIGNED NULL DEFAULT NULL COMMENT '任务id',
  `answerer` int UNSIGNED NULL DEFAULT NULL COMMENT '回答者',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '答案文本',
  `is_true` tinyint NULL DEFAULT NULL COMMENT '是否被认可',
  `nice` int NULL DEFAULT NULL COMMENT '点赞量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `answerer`(`answerer`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE,
  CONSTRAINT `task_answer_ibfk_1` FOREIGN KEY (`answerer`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_answer_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_answer
-- ----------------------------

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
-- Records of user_tags
-- ----------------------------

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
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机号码',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '学校',
  `clock_in` int NULL DEFAULT NULL COMMENT '打卡（记录连续打卡日期）',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `last_login` date NULL DEFAULT NULL COMMENT '最后登录日期',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`, `phone`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (3, '姚镇涛', '$2a$12$e0Bk5agXDNUsBSJAStEVe.S6S3fUDPUKyOcX83IkK.lLom2G/6DZm', 'static/head/3/userHeadPhoto.png', '男', '计算机科学与技术', '大二', 1, 100, 0, 0, '13453981285', '天津职业技术师范大学', 1, '157469291@qq.com', '2025-08-04', '2025-07-14');
INSERT INTO `users` VALUES (4, '吴智勇', '$2a$12$svql.esIY7zyoGWnO3LLCugclM5Un7c1VspIJ/x9M4Q0mAto9YKtW', 'static/head/boys.png', '男', '计算机科学与技术', '大二', 1, 0, 0, 0, '15713576510', '天津职业技术师范大学', 1, '', '2025-08-04', '2025-08-03');

SET FOREIGN_KEY_CHECKS = 1;
