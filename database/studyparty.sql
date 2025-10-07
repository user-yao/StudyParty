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

 Date: 07/10/2025 13:17:03
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
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (19, 3, '测试', '测试一下', '测试\n\n![图片1](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058173078_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片2](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058176877_Screenshot_2025-09-28-15-02-28-40_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 1, 8, 0, '2025-09-28 19:16:23', 0, 1);
INSERT INTO `article` VALUES (20, 3, '1', '2', '\n![图片1](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058211413_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片2](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058215415_Screenshot_2025-09-28-15-02-28-40_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 1, 11, 0, '2025-09-28 19:16:58', 0, 1);
INSERT INTO `article` VALUES (21, 3, '测试标题', '测试摘要', '\n![图片1](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058339470_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片2](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058343188_Screenshot_2025-09-28-15-02-28-40_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片3](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058346027_Screenshot_2025-09-28-15-00-32-73_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 1, 1, 0, '2025-09-28 19:19:10', 0, 1);
INSERT INTO `article` VALUES (22, 3, '测试', '123', '\n![图片1](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759058431081_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 1, 5, 0, '2025-09-28 19:20:40', 0, 1);
INSERT INTO `article` VALUES (23, 3, '123', '123', '测试\n\n![图片1](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759060233519_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片2](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759060236139_Screenshot_2025-09-28-15-02-28-40_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 1, 7, 0, '2025-09-28 19:50:39', 0, 1);
INSERT INTO `article` VALUES (24, 3, '321', '321', '测试\n\n![图片1](http://192.168.233.136:8080/static/uploads/2025/09/28/558fa767416c46f4ae150d092319ec56.jpg)\n\n![图片2](http://192.168.233.136:8080/static/uploads/2025/09/28/5c53c8a6c2f34d23a091bc6fd40521ed.jpg)\n', 1, 1, 26, 2, '2025-09-28 19:51:49', 0, 1);
INSERT INTO `article` VALUES (25, 3, '2222', '2222', '2222\n![图片1](blob:http://localhost:5173/e258ba90-b059-4af3-bcdb-110de86f183e)\n', 0, 0, 2, 0, '2025-10-04 23:17:03', 0, 2);
INSERT INTO `article` VALUES (26, 3, '测试', '测试', '测试', 1, 0, 5, 0, '2025-10-05 20:04:33', 0, 2);
INSERT INTO `article` VALUES (27, 3, 'LinkedList的好处', 'LinkedList的好处', '宝子们！最近帮学弟改课设，发现好多人在 “选集合” 这步就卡壳 —— 做 “图书管理系统” 存图书列表用了 LinkedList，查书的时候卡得半天出结果；还有人做 “待办 APP” 用 ArrayList，频繁插删任务时程序直接变慢… 其实 ArrayList 和 LinkedList 没那么难选，今天结合我踩过的坑，给大家讲透怎么选！​\n一、先搞懂：它俩 “底层逻辑” 差在哪？（选对的前提！）​\n很多人记 “ArrayList 查快、LinkedList 增删快”，但不知道为啥 —— 其实根本原因是 “数据结构” 不一样，就像两种不同的收纳方式：​\nArrayList：像带页码的笔记本，每一页（元素）都有固定页码（索引）。要找第 20 页，直接翻到页码 20 就行（get (index) 快）；但如果要在第 10 页和 11 页之间插一页，后面所有页的页码都得改（元素后移，增删慢）。而且笔记本有固定页数，写满了要换本大的（扩容机制），这也是它的小特点～​\nLinkedList：像串好的钥匙链，每把钥匙（元素）都只连着前一把和后一把（双向链表）。要找第 20 把钥匙，得从第一把开始挨个数（get (index) 慢）；但要在中间加一把钥匙，只需要把前后两把钥匙的链拆开，接上新钥匙就行（增删快），不用动其他钥匙。​\n二、为啥你用着 “没区别”？可能踩了这 3 个坑！​\n不少人说 “我测了 1000 个元素，俩集合差不多快”，其实是测试方法不对，这 3 个坑我当初全踩过：​\n坑 1：数据量太小，差异没显现​\n1000 个元素太少了！ArrayList 挪元素、LinkedList 数元素的耗时，在小数据量下根本看不出来。你试试用 10 万、100 万条数据 —— 比如存 10 万条用户信息，查第 5 万条时，ArrayList 一秒出结果，LinkedList 可能要等好几秒！​\n坑 2：增删只在 “末尾” 操作​\n如果你只用 add () 方法在集合末尾加元素，ArrayList 不用挪元素，和 LinkedList 速度差不多；但如果在中间插删（比如 add (30000, 元素)），ArrayList 要挪后面几万条数据，速度立马慢下来，这时候 LinkedList 的优势就显出来了～​\n坑 3：遍历方式选错，白白浪费效率​\n用 fori 循环（for (int i=0; i<size; i++)）遍历 LinkedList，其实每次 get (i) 都要从头数元素，相当于 “找第 1 个元素数 1 次，找第 2 个元素数 2 次… 找第 n 个元素数 n 次”，耗时直接翻倍！而用迭代器（Iterator）遍历 LinkedList，不用从头数，速度会快很多 —— 这也是新手容易忽略的点！​\n三、新手直接用的 “场景选择表”（不用记原理也能选对！）​\n不用死记硬背，对照场景选就行，亲测有效：​\n场景 1：主要查数据，很少改（比如存课程表、查成绩）→ 选 ArrayList​\n比如做 “学生成绩系统”，存 500 个学生的成绩，每天主要是查分数、遍历排名，几乎不删改 ——ArrayList 的索引查询太香了！​\n场景 2：频繁在中间插删（比如待办 APP、消息列表）→ 选 LinkedList​\n比如做 “待办清单”，经常在中间插 “紧急任务”、删 “已完成任务”，LinkedList 不用挪元素，操作起来又快又流畅～​\n场景 3：不确定场景，或又查又改但 “查” 更多 → 先选 ArrayList​\n大多数日常开发里，“查数据” 的频率远高于 “改数据”，ArrayList 的扩容、索引查询足够应对，而且它的 API 用起来更顺手，新手不容易出错～​\n最后唠唠：​\n我当初做 “图书管理系统” 时，一开始用 LinkedList 存图书，查书时卡得老师以为程序崩了，后来换成 ArrayList，秒查！其实 Java 集合的选择，不是记结论，而是结合实际场景 —— 你有没有因为选错集合踩过坑？或者想知道某个具体场景该选啥？评论区告诉我，下次咱们针对性讲！另外想要 “不同数据量测试代码模板” 的宝子，扣 1 我私发你～​\n', 1, 0, 6, 1, '2025-10-06 21:54:41', 0, 2);
INSERT INTO `article` VALUES (28, 3, '1', '1', '11', 0, 0, 0, 0, '2025-10-07 00:22:38', 0, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_comment
-- ----------------------------
INSERT INTO `article_comment` VALUES (79, 24, 3, '111', 0, '2025-10-04 22:04:28', 2);
INSERT INTO `article_comment` VALUES (80, 24, 3, '111', 0, '2025-10-04 23:16:03', 2);
INSERT INTO `article_comment` VALUES (81, 27, 3, 'good', 1, '2025-10-06 22:11:43', 2);

-- ----------------------------
-- Table structure for article_comment_user
-- ----------------------------
DROP TABLE IF EXISTS `article_comment_user`;
CREATE TABLE `article_comment_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_comment_id` int UNSIGNED NULL DEFAULT NULL,
  `user_id` int UNSIGNED NULL DEFAULT NULL,
  `is_nice` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_comment_id`(`article_comment_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `article_comment_user_ibfk_1` FOREIGN KEY (`article_comment_id`) REFERENCES `article_comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_comment_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_comment_user
-- ----------------------------
INSERT INTO `article_comment_user` VALUES (3, 81, 3, 1);
INSERT INTO `article_comment_user` VALUES (4, 80, 3, 0);

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
  `is_uploader` tinyint NULL DEFAULT NULL COMMENT '是否是作者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `article_user_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_user
-- ----------------------------
INSERT INTO `article_user` VALUES (84, 19, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (85, 20, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (86, 21, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (87, 22, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (88, 23, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (89, 24, 3, 1, 1, 1, 1);
INSERT INTO `article_user` VALUES (90, 25, 3, 0, 0, 1, 1);
INSERT INTO `article_user` VALUES (91, 26, 3, 1, 0, 1, 1);
INSERT INTO `article_user` VALUES (92, 27, 3, 1, 0, 1, 1);
INSERT INTO `article_user` VALUES (93, 28, 3, 0, 0, 0, 1);

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
INSERT INTO `group` VALUES (3, 4, 4, '2025-08-22 00:00:00', '职引星聚研发部门', 1, 40, 100, 2, 3, '学习就是现在！就是现在！', '需要按时完成小组任务，无私奉献，大家都要乐于帮助他人，有任何不懂的可以直接在群里提问1', 'static/head/3/groupHeadPhoto.png', '2025-08-22 00:00:00', 1, 3, NULL);
INSERT INTO `group` VALUES (19, 3, 3, '2025-08-27 00:00:00', '努力学习，天天向上', 1, 0, 100, 1, 3, '天之骄子要学习，666', '我靠，必须学习好不好，真的', 'static/head/19/groupHeadPhoto.png', '2025-08-27 00:00:00', 1, 3, NULL);
INSERT INTO `group` VALUES (20, 3, 3, '2025-08-27 00:00:00', '测试一下', 1, 0, 100, 1, 3, '测试一下', '测试一下', 'static/head/20/groupHeadPhoto.png', '2025-08-27 00:00:00', 0, 3, NULL);
INSERT INTO `group` VALUES (21, 3, 3, '2025-08-28 00:00:00', '学习springboot', 1, 0, 100, 1, 3, '只学习springboot以及相关的框架和工具。如mybatis，kafka，redis，还有一些非常实用的工具，例如jwt以及国产化的增强jwt工具，欢迎大家加入我们', '积极活跃', 'static/head/21/groupHeadPhoto.png', '2025-08-28 00:00:00', 1, 3, NULL);
INSERT INTO `group` VALUES (23, 3, 3, '2025-08-28 00:00:00', '测试', 1, 0, 100, 2, 3, '测试', '测试', 'static/head/23/groupHeadPhoto.png', '2025-08-28 00:00:00', 1, 3, NULL);

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
INSERT INTO `group_task_answer` VALUES (8, 8, 3, '# 作业标题', 0, 100, '2025-09-13 21:43:53');
INSERT INTO `group_task_answer` VALUES (9, 9, 3, '\n![图片4](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759060503883_Screenshot_2025-09-28-15-04-43-62_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片5](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759060506261_Screenshot_2025-09-28-15-02-28-40_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n\n![图片6](file:///storage/emulated/0/Android/data/io.dcloud.HBuilder/apps/HBuilder/doc/uniapp_temp/compressed/1759060508656_Screenshot_2025-09-28-15-00-32-73_d96ad423f652f1eda0e1fd1536a2dc9b.jpg)\n', 1, 100, '2025-09-28 19:55:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of source
-- ----------------------------
INSERT INTO `source` VALUES (6, 'f4506b8135d94eca808a26640ed13199.png', 'http://192.168.227.136:8080/static/uploads/2025/08/24/f4506b8135d94eca808a26640ed13199.png', 'D:/studyParty/uploads/2025/08/24/f4506b8135d94eca808a26640ed13199.png', NULL, NULL, NULL, 8, NULL, NULL, NULL);
INSERT INTO `source` VALUES (7, 'cc9de24c38004334ac7b99d4896ff453.png', 'http://192.168.227.136:8080/static/uploads/2025/08/25/cc9de24c38004334ac7b99d4896ff453.png', 'D:/studyParty/uploads/2025/08/25/cc9de24c38004334ac7b99d4896ff453.png', NULL, NULL, NULL, 9, NULL, NULL, NULL);
INSERT INTO `source` VALUES (8, '1a9760cbd8024f41ba06a40774c88d16.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/1a9760cbd8024f41ba06a40774c88d16.jpg', 'D:/studyParty/uploads/2025/09/13/1a9760cbd8024f41ba06a40774c88d16.jpg', NULL, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (9, 'daaf5d5de2504084825cde41660e0876.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/daaf5d5de2504084825cde41660e0876.jpg', 'D:/studyParty/uploads/2025/09/13/daaf5d5de2504084825cde41660e0876.jpg', NULL, NULL, 2, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (10, '08e97716198a414cab177894ec840dae.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/13/08e97716198a414cab177894ec840dae.jpg', 'D:/studyParty/uploads/2025/09/13/08e97716198a414cab177894ec840dae.jpg', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (68, '1cb879086d384a45ba394886d07e3125.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/23/1cb879086d384a45ba394886d07e3125.jpg', 'D:/studyParty/uploads/2025/09/23/1cb879086d384a45ba394886d07e3125.jpg', 69, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (69, '1068b589c4724f30b1bb3bc4e377bf56.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/23/1068b589c4724f30b1bb3bc4e377bf56.jpg', 'D:/studyParty/uploads/2025/09/23/1068b589c4724f30b1bb3bc4e377bf56.jpg', 71, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (70, 'f4e3c7ab826345e3896252aa9fc1f76b.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/23/f4e3c7ab826345e3896252aa9fc1f76b.jpg', 'D:/studyParty/uploads/2025/09/23/f4e3c7ab826345e3896252aa9fc1f76b.jpg', 72, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (71, 'e01747d33c984e37965d1b959270e793.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/23/e01747d33c984e37965d1b959270e793.jpg', 'D:/studyParty/uploads/2025/09/23/e01747d33c984e37965d1b959270e793.jpg', 73, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (72, 'e74ecf5eb78b4724a6b5395c9cb59033.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/e74ecf5eb78b4724a6b5395c9cb59033.jpg', 'D:/studyParty/uploads/2025/09/26/e74ecf5eb78b4724a6b5395c9cb59033.jpg', 76, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (73, '30129f609ecb4cf7a433b4e2ae6fac7a.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/30129f609ecb4cf7a433b4e2ae6fac7a.jpg', 'D:/studyParty/uploads/2025/09/26/30129f609ecb4cf7a433b4e2ae6fac7a.jpg', 77, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (74, 'ac42e31cb4c240f082fd01a58b3b0e2d.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/ac42e31cb4c240f082fd01a58b3b0e2d.jpg', 'D:/studyParty/uploads/2025/09/26/ac42e31cb4c240f082fd01a58b3b0e2d.jpg', 77, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (75, '072ac1d530a146008b6276593a51ff38.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/072ac1d530a146008b6276593a51ff38.jpg', 'D:/studyParty/uploads/2025/09/26/072ac1d530a146008b6276593a51ff38.jpg', 77, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (76, 'e546b9094e27439892a38c36ae11c02f.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/e546b9094e27439892a38c36ae11c02f.jpg', 'D:/studyParty/uploads/2025/09/26/e546b9094e27439892a38c36ae11c02f.jpg', 78, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (77, '131c23ffb85d46f3983a439aa827c15f.jpg', 'http://192.168.227.136:8080/static/uploads/2025/09/26/131c23ffb85d46f3983a439aa827c15f.jpg', 'D:/studyParty/uploads/2025/09/26/131c23ffb85d46f3983a439aa827c15f.jpg', 78, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (78, '446192cb6d81417d88c7c43a57524e41.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/27/446192cb6d81417d88c7c43a57524e41.jpg', 'D:/studyParty/uploads/2025/09/27/446192cb6d81417d88c7c43a57524e41.jpg', NULL, 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (79, '0c052a05c40041e19cd6d57e7fa1dd82.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/0c052a05c40041e19cd6d57e7fa1dd82.jpg', 'D:/studyParty/uploads/2025/09/28/0c052a05c40041e19cd6d57e7fa1dd82.jpg', NULL, 5, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (80, '2dbd66f95af841d399d7097b5fbdfd44.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/2dbd66f95af841d399d7097b5fbdfd44.jpg', 'D:/studyParty/uploads/2025/09/28/2dbd66f95af841d399d7097b5fbdfd44.jpg', NULL, 7, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (81, 'a4addcd870e64acdbc042e0661c5787e.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/a4addcd870e64acdbc042e0661c5787e.jpg', 'D:/studyParty/uploads/2025/09/28/a4addcd870e64acdbc042e0661c5787e.jpg', NULL, 6, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (82, '4bdee8db056144b78b24998bae70bd55.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/4bdee8db056144b78b24998bae70bd55.jpg', 'D:/studyParty/uploads/2025/09/28/4bdee8db056144b78b24998bae70bd55.jpg', NULL, 8, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (83, '4ea84f594d574a04a29f3d307ea3a6ac.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/4ea84f594d574a04a29f3d307ea3a6ac.jpg', 'D:/studyParty/uploads/2025/09/28/4ea84f594d574a04a29f3d307ea3a6ac.jpg', NULL, 9, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (84, 'e649c70fdca044aab879d4edb66fa08f.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/e649c70fdca044aab879d4edb66fa08f.jpg', 'D:/studyParty/uploads/2025/09/28/e649c70fdca044aab879d4edb66fa08f.jpg', NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (85, '986385fb93514ae2b8b47c9f64976d96.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/986385fb93514ae2b8b47c9f64976d96.jpg', 'D:/studyParty/uploads/2025/09/28/986385fb93514ae2b8b47c9f64976d96.jpg', NULL, 11, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (86, 'eb3c32bf835743d7ab9a85aff711afa9.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/eb3c32bf835743d7ab9a85aff711afa9.jpg', 'D:/studyParty/uploads/2025/09/28/eb3c32bf835743d7ab9a85aff711afa9.jpg', NULL, 12, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (87, '7bfd72c6b6d344f5a471ae0943e4697d.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/7bfd72c6b6d344f5a471ae0943e4697d.jpg', 'D:/studyParty/uploads/2025/09/28/7bfd72c6b6d344f5a471ae0943e4697d.jpg', NULL, 13, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (88, '678c5f450b4e48ec8edc961d20c1d3cb.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/678c5f450b4e48ec8edc961d20c1d3cb.jpg', 'D:/studyParty/uploads/2025/09/28/678c5f450b4e48ec8edc961d20c1d3cb.jpg', NULL, 14, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (89, 'e642703c2d704b0aab497cbc14dda4b6.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/e642703c2d704b0aab497cbc14dda4b6.jpg', 'D:/studyParty/uploads/2025/09/28/e642703c2d704b0aab497cbc14dda4b6.jpg', NULL, 15, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (90, 'e3a84b44c1204664ab94a7196384c54c.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/e3a84b44c1204664ab94a7196384c54c.jpg', 'D:/studyParty/uploads/2025/09/28/e3a84b44c1204664ab94a7196384c54c.jpg', NULL, 16, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (91, 'd4ba6a093862486a9d5dca5a13fccb87.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/d4ba6a093862486a9d5dca5a13fccb87.jpg', 'D:/studyParty/uploads/2025/09/28/d4ba6a093862486a9d5dca5a13fccb87.jpg', NULL, 17, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (92, '558fa767416c46f4ae150d092319ec56.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/558fa767416c46f4ae150d092319ec56.jpg', 'D:/studyParty/uploads/2025/09/28/558fa767416c46f4ae150d092319ec56.jpg', NULL, 24, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (93, '5c53c8a6c2f34d23a091bc6fd40521ed.jpg', 'http://192.168.233.136:8080/static/uploads/2025/09/28/5c53c8a6c2f34d23a091bc6fd40521ed.jpg', 'D:/studyParty/uploads/2025/09/28/5c53c8a6c2f34d23a091bc6fd40521ed.jpg', NULL, 24, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (94, 'be1d024eb30c4faabcba8ae9f1f4cb7e.png', 'http://192.168.233.136:8080/static/uploads/2025/10/04/be1d024eb30c4faabcba8ae9f1f4cb7e.png', 'D:/studyParty/uploads/2025/10/04/be1d024eb30c4faabcba8ae9f1f4cb7e.png', NULL, NULL, NULL, NULL, NULL, 9, NULL);
INSERT INTO `source` VALUES (95, '8da89caf1fe64dfd84d5ebc13f4bceb1.png', 'http://192.168.233.136:8080/static/uploads/2025/10/04/8da89caf1fe64dfd84d5ebc13f4bceb1.png', 'D:/studyParty/uploads/2025/10/04/8da89caf1fe64dfd84d5ebc13f4bceb1.png', NULL, NULL, NULL, NULL, NULL, 10, NULL);
INSERT INTO `source` VALUES (96, '0cd86130f3fe471bacb246d798215365.png', 'http://192.168.233.136:8080/static/uploads/2025/10/04/0cd86130f3fe471bacb246d798215365.png', 'D:/studyParty/uploads/2025/10/04/0cd86130f3fe471bacb246d798215365.png', NULL, NULL, NULL, NULL, NULL, 11, NULL);
INSERT INTO `source` VALUES (97, '49178e3bcc504afba7b666f199dd7bd0.png', 'http://192.168.233.136:8080/static/uploads/2025/10/04/49178e3bcc504afba7b666f199dd7bd0.png', 'D:/studyParty/uploads/2025/10/04/49178e3bcc504afba7b666f199dd7bd0.png', 80, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `source` VALUES (98, 'd01a8d5bfa044c9eacab31d7ccb96fca.png', 'http://192.168.233.136:8080/static/uploads/2025/10/04/d01a8d5bfa044c9eacab31d7ccb96fca.png', 'D:/studyParty/uploads/2025/10/04/d01a8d5bfa044c9eacab31d7ccb96fca.png', NULL, 25, NULL, NULL, NULL, NULL, NULL);

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
  `context` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '问题描述',
  `is_true_id` int NULL DEFAULT NULL COMMENT '确定的id',
  `is_over` tinyint NULL DEFAULT NULL COMMENT '是否完成',
  `star_coin` int NULL DEFAULT NULL COMMENT '悬赏星币',
  `star_prestige` int NULL DEFAULT NULL COMMENT '星愿',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint NULL DEFAULT NULL COMMENT '身份（1学生，2老师，3企业）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader`(`uploader`) USING BTREE,
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (2, 3, '测试任务', '# 测试', -1, 0, 0, 20, '2025-10-04 16:21:36', 2);
INSERT INTO `task` VALUES (6, 3, '111', '1111', -1, 0, 0, 20, '2025-10-04 22:54:39', 2);
INSERT INTO `task` VALUES (10, 3, '1212', '121212\n![图片1](blob:http://localhost:5173/74f2c7b6-a99b-41e6-b55a-262b084d16f0)\n', -1, 0, 0, 1, '2025-10-04 23:09:48', 2);
INSERT INTO `task` VALUES (11, 3, '222', '2222\n![图片1](blob:http://localhost:5173/65cf418e-5e82-4654-b157-e5ebd4fc6427)\n', 2, 1, 0, 1, '2025-10-04 23:12:28', 2);
INSERT INTO `task` VALUES (12, 3, 'JAVASE读取文件中的问题', '读文本文件用字节流还是字符流啊？我用 FileInputStream 读出来全是乱码…\n今天做 IO 的作业，要求读一个 txt 文件里的内容并打印。我一开始用了 FileInputStream，代码大概是这样：\n```java\nFileInputStream fis = new FileInputStream(\"test.txt\");\nbyte[] buf = new byte[1024];\nint len;\nwhile ((len = fis.read(buf)) != -1) {\n    System.out.print(new String(buf, 0, len));\n}\n```\n结果控制台全是乱码，但文件里明明是中文。后来同学让我换成 FileReader，就正常了。想问下字节流和字符流到底啥区别啊？是不是读文本用字符流，读图片、视频这种用字节流？还有那个 BufferedReader 和 BufferedInputStream，加了 Buffered 之后为啥读得更快啊？新手实在搞不懂 IO 的各种流…', -1, 0, 0, 1, '2025-10-06 19:32:11', 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_answer
-- ----------------------------
INSERT INTO `task_answer` VALUES (1, 2, 3, '什么？', NULL, 0, '2025-10-04 21:54:17', 2);
INSERT INTO `task_answer` VALUES (2, 11, 3, '111111', 1, 0, '2025-10-04 23:29:53', 2);
INSERT INTO `task_answer` VALUES (3, 12, 3, '111', NULL, 0, '2025-10-06 19:46:09', 2);
INSERT INTO `task_answer` VALUES (4, 12, 3, '11111', NULL, 0, '2025-10-06 19:46:14', 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_article
-- ----------------------------
INSERT INTO `user_article` VALUES (1, 3, 26);
INSERT INTO `user_article` VALUES (2, 3, 27);
INSERT INTO `user_article` VALUES (3, 3, 28);

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
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
INSERT INTO `user_task` VALUES (19, 3, 9, 2);
INSERT INTO `user_task` VALUES (20, 3, 9, 2);
INSERT INTO `user_task` VALUES (21, 3, 9, 2);
INSERT INTO `user_task` VALUES (22, 3, 2, 1);

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
  `article_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `last_login` date NULL DEFAULT NULL COMMENT '最后登录日期',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`, `phone`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (3, '姚镇涛', '$2a$12$e0Bk5agXDNUsBSJAStEVe.S6S3fUDPUKyOcX83IkK.lLom2G/6DZm', 'static/head/3/userHeadPhoto.png', '男', '计算机科学与技术22', '大二22', 2, 100, 0, 11, '13453981285', '天津职业技术师范大学222', 8, '157469291@qq.com', 0, NULL, '2025-10-07', '2025-07-14');
INSERT INTO `users` VALUES (4, '吴智勇', '$2a$12$svql.esIY7zyoGWnO3LLCugclM5Un7c1VspIJ/x9M4Q0mAto9YKtW', 'static/head/boys.png', '男', '计算机科学与技术', '大二', 1, 100, 0, 0, '15713576510', '天津职业技术师范大学', 1, '', 0, NULL, '2025-09-03', '2025-08-03');
INSERT INTO `users` VALUES (5, '任少鹏', '$2a$12$JaZq7YD9y1nRyz9Sjt9lKeIzxy.zwDnSPMiTYQEbRrNfEv8Nj1qHC', 'static/head/boys.png', '男', '计算机科学与技术', '大二', 2, 100, 0, 0, '18512290493', '天津职业技术师范大学', 0, '', 0, NULL, NULL, '2025-08-14');
INSERT INTO `users` VALUES (6, '企业A', '$2a$12$3Y50P0n92gfn2Coei7GY/.58RbLHyi77/IwrASnnFsUlwZ08iswAK', 'static/head/boys.png', '男', '天津中铁三局', '人事部主任', 3, 100, 0, 0, '1313131313', '天津职业技术师范大学', 0, '', 0, NULL, NULL, '2025-08-14');
INSERT INTO `users` VALUES (7, '学生A', '$2a$12$y4//67E9uxNjhYTkrsR42OqkBDqX7owHhzm7dCdU5fyg18VFHxfuu', 'static/head/boys.png', '男', '软件工程', '大一', 1, 100, 0, 0, '123123123', '天津职业技术师范大学', 0, '', 0, NULL, NULL, '2025-08-14');
INSERT INTO `users` VALUES (8, '学生B', '$2a$12$WUBxREyyj8vXdm/k/dpeWOXOwEH2y5R29ToYfbPaQxKRL8Swn/gym', 'static/head/8/userHeadPhoto.png', '男', '软件工程', '大一', 1, 100, 0, 0, '12341234', '天津职业技术师范大学', 0, '', 0, NULL, NULL, '2025-08-16');
INSERT INTO `users` VALUES (13, '学生C', '$2a$12$TkmR9LqnGDpzhWsKJhxOnepj6lbkzihUt7HBfEbvWPSbrcIQv3ZAy', 'static/head/13/userHeadPhoto.png', '女', '软件工程', '大一', 1, 100, 0, 0, '1234512345', '天津职业技术师范大学', 1, '', 0, NULL, '2025-08-21', '2025-08-21');
INSERT INTO `users` VALUES (14, '111', '$2a$12$ML2DAKwmtGUGiA9OlVmxKeUfz3m55/6DhbTiSw7oAYSwRgGCPBHZG', 'static/head/14/userHeadPhoto.png', '男', '111', '111', 1, 100, 0, 0, '15151515151', '111', 1, '1111@qq.com', 0, NULL, '2025-10-05', '2025-10-05');

SET FOREIGN_KEY_CHECKS = 1;
