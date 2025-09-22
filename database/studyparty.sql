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

 Date: 22/09/2025 14:07:52
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
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, 3, '测试标题', '测试概要', '# 测试内容', 0, 0, 2, 0, '2025-09-21 22:45:15', 0, 1);
INSERT INTO `article` VALUES (2, 3, '测试标题', '测试概要', '# 测试内容', 0, 0, 0, 0, '2025-09-21 23:29:05', 0, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_comment
-- ----------------------------
INSERT INTO `article_comment` VALUES (1, 1, 3, '测试内容', 0, '2025-09-22 13:53:18', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_tags
-- ----------------------------

-- ----------------------------
-- Table structure for article_user
-- ----------------------------
DROP TABLE IF EXISTS `article_user`;
CREATE TABLE `article_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_id` int UNSIGNED NULL DEFAULT NULL,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `is_nice` tinyint NULL DEFAULT NULL COMMENT '是否点赞0否 1是',
  `is_collect` tinyint NULL DEFAULT NULL COMMENT '是否收藏',
  `is_view` tinyint NULL DEFAULT NULL COMMENT '是否查看过',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_user
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bill
-- ----------------------------

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `friend_id` int UNSIGNED NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES (3, 3, 4, '2025-08-21 21:44:49', '吴小勇');
INSERT INTO `friend` VALUES (4, 4, 3, '2025-08-21 21:44:49', NULL);

-- ----------------------------
-- Table structure for friend_request
-- ----------------------------
DROP TABLE IF EXISTS `friend_request`;
CREATE TABLE `friend_request`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `friend_id` int UNSIGNED NULL DEFAULT NULL,
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `is_consent` tinyint NULL DEFAULT NULL COMMENT '0，未处理，1同意，2不同意',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend_request
-- ----------------------------
INSERT INTO `friend_request` VALUES (1, 3, 5, '我是咱们学校的', '2025-08-14 19:25:09', 0);
INSERT INTO `friend_request` VALUES (2, 3, 6, '你好，我跟喜欢你的博客', '2025-08-14 19:25:12', 0);
INSERT INTO `friend_request` VALUES (3, 3, 7, '你好，我是姚镇涛，想添加你为好友,11', '2025-08-21 22:59:06', 0);
INSERT INTO `friend_request` VALUES (4, 4, 3, '哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇哥们，我是吴智勇', '2025-08-20 23:28:18', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (3, 4, 4, '2025-08-22 00:00:00', '职引星聚研发部门', 1, 40, 100, 2, 3, '学习就是现在！就是现在！', '需要按时完成小组任务，无私奉献，大家都要乐于帮助他人，有任何不懂的可以直接在群里提问1', 'static/head/3/groupHeadPhoto.png', '2025-08-22 00:00:00', 1, NULL, NULL);
INSERT INTO `group` VALUES (19, 3, 3, '2025-08-27 00:00:00', '努力学习，天天向上', 1, 0, 100, 1, 3, '天之骄子要学习，666', '我靠，必须学习好不好，真的', 'static/head/19/groupHeadPhoto.png', '2025-08-27 00:00:00', 1, NULL, NULL);
INSERT INTO `group` VALUES (20, 3, 3, '2025-08-27 00:00:00', '测试一下', 1, 0, 100, 1, 3, '测试一下', '测试一下', 'static/head/20/groupHeadPhoto.png', '2025-08-27 00:00:00', 0, NULL, NULL);
INSERT INTO `group` VALUES (21, 3, 3, '2025-08-28 00:00:00', '学习springboot', 1, 0, 100, 1, 3, '只学习springboot以及相关的框架和工具。如mybatis，kafka，redis，还有一些非常实用的工具，例如jwt以及国产化的增强jwt工具，欢迎大家加入我们', '积极活跃', 'static/head/21/groupHeadPhoto.png', '2025-08-28 00:00:00', 1, NULL, NULL);
INSERT INTO `group` VALUES (23, 3, 3, '2025-08-28 00:00:00', '测试', 1, 0, 100, 2, 3, '测试', '测试', 'static/head/23/groupHeadPhoto.png', '2025-08-28 00:00:00', 1, NULL, NULL);

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
  `is_invited` tinyint NULL DEFAULT NULL COMMENT '是否被邀请，0否，1是',
  `is_pass` tinyint NULL DEFAULT NULL COMMENT '0.未判断 1.通过 2.拒绝',
  `join_time` timestamp NULL DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `group_leader`(`group_leader`) USING BTREE,
  CONSTRAINT `group_join_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_join_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_join
-- ----------------------------
INSERT INTO `group_join` VALUES (3, 3, 3, 4, '邀请加入', 1, 1, '2025-08-31 17:46:09');
INSERT INTO `group_join` VALUES (4, 23, 3, 4, '我想加入啊', 0, 1, '2025-08-31 17:47:40');
INSERT INTO `group_join` VALUES (5, 3, 4, 3, '我要加入！', 0, 1, '2025-09-01 21:32:02');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  CONSTRAINT `group_task_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_task
-- ----------------------------
INSERT INTO `group_task` VALUES (8, 3, '学习security', 3, '2025-08-25 19:33:43', '2025-10-25 19:33:43', 1, '# 基于反向驱动式学习的产教融合平台架构设计文档（核心功能部分）\n\n## 一、功能概述 \n\n本平台以“**企业需求反向驱动教学供给**”为核心逻辑，构建“企业需求输入-课程动态适配-实践场景验证-效果反馈优化”的闭环产教融合体系。聚焦解决传统产教融合中“企业需求传递滞后、课程与岗位脱节、实践环节形式化”三大痛点，通过**需求反向传导机制**、**校企协同设计工具**、**场景化实践引擎**三大核心模块，实现企业需求与教育供给的实时匹配，推动“岗位-课程-实践-就业”的精准对接。 \n\n## 二、详细功能设计（核心模块） \n\n### （一）企业需求管理模块（反向驱动源点） \n\n![security](http://192.168.1.13:8080/static/uploads/2025/08/24/f4506b8135d94eca808a26640ed13199.png)', 2, '2025-08-24 23:25:57');
INSERT INTO `group_task` VALUES (9, 3, '学习security', 3, '2025-08-25 19:33:43', '2025-10-25 19:33:43', 1, '# 基于反向驱动式学习的产教融合平台架构设计文档（核心功能部分）\n\n## 一、功能概述 \n\n本平台以“**企业需求反向驱动教学供给**”为核心逻辑，构建“企业需求输入-课程动态适配-实践场景验证-效果反馈优化”的闭环产教融合体系。聚焦解决传统产教融合中“企业需求传递滞后、课程与岗位脱节、实践环节形式化”三大痛点，通过**需求反向传导机制**、**校企协同设计工具**、**场景化实践引擎**三大核心模块，实现企业需求与教育供给的实时匹配，推动“岗位-课程-实践-就业”的精准对接。 \n\n## 二、详细功能设计（核心模块） \n\n### （一）企业需求管理模块（反向驱动源点）', 2, '2025-08-25 00:13:57');
INSERT INTO `group_task` VALUES (10, 3, '测试内容', 4, '2025-10-02 21:22:22', '2025-10-29 21:22:22', 0, '# 基于反向驱动式学习的产教融合平台架构设计文档（核心功能部分）\n\n## 一、功能概述 \n\n本平台以“**企业需求反向驱动教学供给**”为核心逻辑，构建“企业需求输入-课程动态适配-实践场景验证-效果反馈优化”的闭环产教融合体系。聚焦解决传统产教融合中“企业需求传递滞后、课程与岗位脱节、实践环节形式化”三大痛点，通过**需求反向传导机制**、**校企协同设计工具**、**场景化实践引擎**三大核心模块，实现企业需求与教育供给的实时匹配，推动“岗位-课程-实践-就业”的精准对接。 \n\n## 二、详细功能设计（核心模块） \n\n### （一）企业需求管理模块（反向驱动源点） \n\n```java\nSystem.out.println(\"Hello World!\");\n```\n\n', 2, '2025-09-03 00:32:30');

-- ----------------------------
-- Table structure for group_task_answer
-- ----------------------------
DROP TABLE IF EXISTS `group_task_answer`;
CREATE TABLE `group_task_answer`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_task_id` int UNSIGNED NULL DEFAULT NULL COMMENT '任务id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `context` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务答案',
  `have_source` tinyint NULL DEFAULT NULL COMMENT '0 没有，1 有，有没有静态资源',
  `score` int NULL DEFAULT NULL COMMENT '成绩',
  `time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_task_id`(`group_task_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `group_task_answer_ibfk_1` FOREIGN KEY (`group_task_id`) REFERENCES `group_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_task_answer_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `group_user` (`group_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_task_answer
-- ----------------------------
INSERT INTO `group_task_answer` VALUES (8, 8, 3, '# 作业标题', 0, -1, '2025-09-13 21:43:53');
INSERT INTO `group_task_answer` VALUES (9, 9, 3, '# 作业标题\n\n![图片1](http://192.168.227.136:8080/static/uploads/2025/09/13/f7e751f4ae4c4897b67f8fd7d366c244.jpg)\n', 1, -1, '2025-09-13 21:54:15');

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_user
-- ----------------------------
INSERT INTO `group_user` VALUES (10, 19, 3, 0, '2025-08-27 00:00:00');
INSERT INTO `group_user` VALUES (11, 20, 3, 0, '2025-08-27 00:00:00');
INSERT INTO `group_user` VALUES (12, 21, 3, 0, '2025-08-28 00:00:00');
INSERT INTO `group_user` VALUES (14, 23, 3, 0, '2025-08-28 00:00:00');
INSERT INTO `group_user` VALUES (15, 3, 4, 0, '2025-08-31 00:00:00');
INSERT INTO `group_user` VALUES (16, 23, 4, 0, '2025-08-31 00:00:00');
INSERT INTO `group_user` VALUES (17, 3, 3, 40, '2025-09-01 00:00:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of source
-- ----------------------------
INSERT INTO `source` VALUES (6, 'f4506b8135d94eca808a26640ed13199.png', 'http://localhost:8080/static/uploads/2025/08/24/f4506b8135d94eca808a26640ed13199.png', 'D:/studyParty/uploads/2025/08/24/f4506b8135d94eca808a26640ed13199.png', NULL, NULL, NULL, 8, NULL, NULL, NULL);
INSERT INTO `source` VALUES (7, 'cc9de24c38004334ac7b99d4896ff453.png', 'http://localhost:8080/static/uploads/2025/08/25/cc9de24c38004334ac7b99d4896ff453.png', 'D:/studyParty/uploads/2025/08/25/cc9de24c38004334ac7b99d4896ff453.png', NULL, NULL, NULL, 9, NULL, NULL, NULL);
INSERT INTO `source` VALUES (8, '1a9760cbd8024f41ba06a40774c88d16.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/1a9760cbd8024f41ba06a40774c88d16.jpg', 'D:/studyParty/uploads/2025/09/13/1a9760cbd8024f41ba06a40774c88d16.jpg', NULL, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (9, 'daaf5d5de2504084825cde41660e0876.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/daaf5d5de2504084825cde41660e0876.jpg', 'D:/studyParty/uploads/2025/09/13/daaf5d5de2504084825cde41660e0876.jpg', NULL, NULL, 2, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (10, '08e97716198a414cab177894ec840dae.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/08e97716198a414cab177894ec840dae.jpg', 'D:/studyParty/uploads/2025/09/13/08e97716198a414cab177894ec840dae.jpg', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
  `is_true_id` int NULL DEFAULT NULL COMMENT '确定的id',
  `is_over` tinyint NULL DEFAULT NULL COMMENT '是否完成',
  `star_coin` int NULL DEFAULT NULL COMMENT '悬赏星币',
  `star_prestige` int NULL DEFAULT NULL COMMENT '星愿',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
  `create_time` timestamp NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `answerer`(`answerer`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE,
  CONSTRAINT `task_answer_ibfk_1` FOREIGN KEY (`answerer`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_answer_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_answer
-- ----------------------------

-- ----------------------------
-- Table structure for user_article
-- ----------------------------
DROP TABLE IF EXISTS `user_article`;
CREATE TABLE `user_article`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `article_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  CONSTRAINT `user_article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_article_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_article
-- ----------------------------

-- ----------------------------
-- Table structure for user_create_task
-- ----------------------------
DROP TABLE IF EXISTS `user_create_task`;
CREATE TABLE `user_create_task`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_id` int UNSIGNED NULL DEFAULT NULL,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `user_create_task_ibfk_1`(`article_id`) USING BTREE,
  CONSTRAINT `user_create_task_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_create_task_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_create_task
-- ----------------------------

-- ----------------------------
-- Table structure for user_plan
-- ----------------------------
DROP TABLE IF EXISTS `user_plan`;
CREATE TABLE `user_plan`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `plan_context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务列表（自律）',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `is_start` tinyint NULL DEFAULT NULL COMMENT '是否开始（1开始，0未开始）',
  `is_end` tinyint NULL DEFAULT NULL COMMENT '是否结束（1，结束，0，未结束）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_plan
-- ----------------------------
INSERT INTO `user_plan` VALUES (1, 3, '学习JavaSE 基本数据结构', '2025-08-12 17:00:07', NULL, 0, 0);
INSERT INTO `user_plan` VALUES (2, 3, '学习JavaSE 基本数据结构2', '2025-08-12 17:00:07', NULL, 0, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_tags
-- ----------------------------

-- ----------------------------
-- Table structure for user_task
-- ----------------------------
DROP TABLE IF EXISTS `user_task`;
CREATE TABLE `user_task`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `task_id` int UNSIGNED NULL DEFAULT NULL,
  `task_type` tinyint NULL DEFAULT NULL COMMENT '1.任务所 2.小组任务',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_task
-- ----------------------------
INSERT INTO `user_task` VALUES (1, 3, 9, 2);
INSERT INTO `user_task` VALUES (2, 3, 9, 2);
INSERT INTO `user_task` VALUES (3, 3, 9, 2);
INSERT INTO `user_task` VALUES (4, 3, 9, 2);
INSERT INTO `user_task` VALUES (5, 3, 9, 2);
INSERT INTO `user_task` VALUES (6, 3, 9, 2);
INSERT INTO `user_task` VALUES (7, 3, 9, 2);
INSERT INTO `user_task` VALUES (8, 3, 9, 2);
INSERT INTO `user_task` VALUES (9, 3, 8, 2);
INSERT INTO `user_task` VALUES (10, 3, 8, 2);
INSERT INTO `user_task` VALUES (11, 3, 8, 2);
INSERT INTO `user_task` VALUES (12, 3, 8, 2);
INSERT INTO `user_task` VALUES (13, 3, 9, 2);
INSERT INTO `user_task` VALUES (14, 3, 8, 2);
INSERT INTO `user_task` VALUES (15, 3, 8, 2);
INSERT INTO `user_task` VALUES (16, 3, 9, 2);
INSERT INTO `user_task` VALUES (17, 3, 8, 2);
INSERT INTO `user_task` VALUES (18, 3, 9, 2);

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
  `finish_task` int NULL DEFAULT NULL COMMENT '完成的任务数量',
  `last_login` date NULL DEFAULT NULL COMMENT '最后登录日期',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`, `phone`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (3, '姚镇涛', '$2a$12$e0Bk5agXDNUsBSJAStEVe.S6S3fUDPUKyOcX83IkK.lLom2G/6DZm', 'static/head/3/userHeadPhoto.png', '男', '计算机科学与技术', '大二', 1, 100, 0, 0, '13453981285', '天津职业技术师范大学', 3, '157469291@qq.com', 0, '2025-09-22', '2025-07-14');
INSERT INTO `users` VALUES (4, '吴智勇', '$2a$12$svql.esIY7zyoGWnO3LLCugclM5Un7c1VspIJ/x9M4Q0mAto9YKtW', 'static/head/boys.png', '男', '计算机科学与技术', '大二', 1, 100, 0, 0, '15713576510', '天津职业技术师范大学', 1, '', 0, '2025-09-03', '2025-08-03');
INSERT INTO `users` VALUES (5, '任少鹏', '$2a$12$JaZq7YD9y1nRyz9Sjt9lKeIzxy.zwDnSPMiTYQEbRrNfEv8Nj1qHC', 'static/head/boys.png', '男', '计算机科学与技术', '大二', 2, 100, 0, 0, '18512290493', '天津职业技术师范大学', 0, '', 0, NULL, '2025-08-14');
INSERT INTO `users` VALUES (6, '企业A', '$2a$12$3Y50P0n92gfn2Coei7GY/.58RbLHyi77/IwrASnnFsUlwZ08iswAK', 'static/head/boys.png', '男', '天津中铁三局', '人事部主任', 3, 100, 0, 0, '1313131313', '天津职业技术师范大学', 0, '', 0, NULL, '2025-08-14');
INSERT INTO `users` VALUES (7, '学生A', '$2a$12$y4//67E9uxNjhYTkrsR42OqkBDqX7owHhzm7dCdU5fyg18VFHxfuu', 'static/head/boys.png', '男', '软件工程', '大一', 1, 100, 0, 0, '123123123', '天津职业技术师范大学', 0, '', 0, NULL, '2025-08-14');
INSERT INTO `users` VALUES (8, '学生B', '$2a$12$WUBxREyyj8vXdm/k/dpeWOXOwEH2y5R29ToYfbPaQxKRL8Swn/gym', 'static/head/8/userHeadPhoto.png', '男', '软件工程', '大一', 1, 100, 0, 0, '12341234', '天津职业技术师范大学', 0, '', 0, NULL, '2025-08-16');
INSERT INTO `users` VALUES (13, '学生C', '$2a$12$TkmR9LqnGDpzhWsKJhxOnepj6lbkzihUt7HBfEbvWPSbrcIQv3ZAy', 'static/head/13/userHeadPhoto.png', '女', '软件工程', '大一', 1, 100, 0, 0, '1234512345', '天津职业技术师范大学', 1, '', 0, '2025-08-21', '2025-08-21');

SET FOREIGN_KEY_CHECKS = 1;
