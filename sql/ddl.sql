/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.24 : Database - main
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`main` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `main`;

/*Table structure for table `apifox_ai_doc` */

DROP TABLE IF EXISTS `apifox_ai_doc`;

CREATE TABLE `apifox_ai_doc` (
  `id` bigint DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `description` text,
  `icon` varchar(256) DEFAULT NULL,
  `views` bigint DEFAULT NULL,
  `collections` bigint DEFAULT NULL,
  `sysDomain` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `domainName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `blog` */

DROP TABLE IF EXISTS `blog`;

CREATE TABLE `blog` (
  `id` bigint NOT NULL COMMENT '博客id',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '博客标题',
  `description` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '博客描述',
  `build_type` smallint DEFAULT '1' COMMENT '0简单富文本，1富文本，2markdown，3csdn',
  `csdn_link` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'csdn链接',
  `text` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '博客内容',
  `blog_like` int DEFAULT '0' COMMENT '喜欢数',
  `star` int DEFAULT '0' COMMENT '评分',
  `collect` int DEFAULT '0' COMMENT '收藏数',
  `author` bigint DEFAULT '1' COMMENT '作者ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `file_path` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '附件路径',
  `is_comment` smallint DEFAULT '0' COMMENT '0允许评论，1不允许评论',
  `blog_class` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分类，格式 大类ID||小类ID1|小类ID2|小类ID3',
  `is_create_self` smallint DEFAULT '0' COMMENT '0原创，1转载',
  `music_file` varchar(42) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '背景音乐id',
  `image` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '封面图片，格式 图片1||图片2||...',
  `html` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '博客内容html',
  `copy_link` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '转载链接',
  `type` smallint DEFAULT '0' COMMENT '1草稿，0已完成的博客，2受限制的博客',
  `blog_class_name` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分类，格式 大类名称||小类名称|小类名称|小类名称',
  `file_id` varchar(42) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '附件ID',
  `author_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '作者名字',
  `show_num` int DEFAULT '0' COMMENT '访问量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

/*Table structure for table `chat` */

DROP TABLE IF EXISTS `chat`;

CREATE TABLE `chat` (
  `chat_id` varchar(64) CHARACTER SET utf8 NOT NULL,
  `chat_user_id` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `chat_createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `chat_title` blob,
  `chat_answer` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
  `chat_state` int DEFAULT '0' COMMENT '0表示不公开，1表示公开',
  `chat_modal` varchar(128) CHARACTER SET utf8 DEFAULT 'gpt-3.5-turbo',
  `chat_context` int DEFAULT '6',
  `chat_system` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '系统角色',
  `chat_image` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`chat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `chatfuncation` */

DROP TABLE IF EXISTS `chatfuncation`;

CREATE TABLE `chatfuncation` (
  `funcation_id` varchar(64) NOT NULL,
  `message_createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `message` longblob,
  `message_role` varchar(16) DEFAULT NULL,
  `message_index` varchar(8) DEFAULT NULL,
  `chat_id` varchar(64) DEFAULT NULL,
  `funcation_name` varchar(64) DEFAULT NULL,
  `funcation_call` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`funcation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `chatmessage` */

DROP TABLE IF EXISTS `chatmessage`;

CREATE TABLE `chatmessage` (
  `message_id` varchar(64) CHARACTER SET utf8 NOT NULL,
  `message_createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `message` longblob,
  `message_role` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'system,user,assistant',
  `message_index` varchar(8) CHARACTER SET utf8 DEFAULT NULL,
  `chat_id` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `reason_message` longblob,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `csgo_match_copy` */

DROP TABLE IF EXISTS `csgo_match_copy`;

CREATE TABLE `csgo_match_copy` (
  `id` int NOT NULL AUTO_INCREMENT,
  `map` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `map_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `map_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `map_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `duration` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `win` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `score1` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `score2` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `half_score1` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `half_score2` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `player_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `highlights_data` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `kill_num` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `neg_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `hand_gun_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `entry_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `awp_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `death` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `entry_death` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `assist` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `head_shot` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `head_shot_ratio` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rating` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pw_rating` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `item_throw` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flash` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flash_teammate` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flash_success` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `two_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `three_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `four_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `five_kill` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vs1` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vs2` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vs3` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vs4` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vs5` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `head_shot_count` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dmg_armor` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dmg_health` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `adpr` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fire_count` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `hit_count` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rws` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `first_death` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `snipe_num` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mvp` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `match_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_xiuxiu` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_liuyucheng` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `is_zq` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1657 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `embedding` */

DROP TABLE IF EXISTS `embedding`;

CREATE TABLE `embedding` (
  `id` int NOT NULL AUTO_INCREMENT,
  `embedding` longtext,
  `table_name` varchar(255) DEFAULT NULL,
  `table_id` varchar(255) DEFAULT NULL,
  `data` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `gptmodel` */

DROP TABLE IF EXISTS `gptmodel`;

CREATE TABLE `gptmodel` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '模型id',
  `model_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型名称',
  `setting_id` int NOT NULL COMMENT 'gpt设置id',
  `_object` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `owned_by` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `root` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `permission` longtext COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1386 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `gptsetting` */

DROP TABLE IF EXISTS `gptsetting`;

CREATE TABLE `gptsetting` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gpt_url` varchar(256) NOT NULL DEFAULT 'https://api.openai.com/' COMMENT 'chatgpt代理或中转地址',
  `gpt_apikey` varchar(128) NOT NULL COMMENT '调用凭证',
  `user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timeout` int NOT NULL DEFAULT '60' COMMENT '接口超时时长，默认60秒',
  `model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;

/*Table structure for table `knowledge` */

DROP TABLE IF EXISTS `knowledge`;

CREATE TABLE `knowledge` (
  `id` bigint NOT NULL,
  `knowledge_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `knowledge_creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `knowledge_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `knowledge_attr` */

DROP TABLE IF EXISTS `knowledge_attr`;

CREATE TABLE `knowledge_attr` (
  `knowledge_attr_id` bigint NOT NULL,
  `knowledge_id` bigint DEFAULT NULL,
  `knowledge_attr_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `knowledge_attr_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`knowledge_attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `knowledge_attr_label` */

DROP TABLE IF EXISTS `knowledge_attr_label`;

CREATE TABLE `knowledge_attr_label` (
  `knowledge_attr_label_id` bigint NOT NULL,
  `attr_id` bigint DEFAULT NULL,
  `knowledge_attr_label_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`knowledge_attr_label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `knowledge_doc` */

DROP TABLE IF EXISTS `knowledge_doc`;

CREATE TABLE `knowledge_doc` (
  `doc_id` bigint NOT NULL,
  `doc_name` varchar(256) DEFAULT NULL,
  `creator` varchar(64) DEFAULT NULL,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `doc_type` varchar(16) DEFAULT NULL,
  `doc_url` varchar(512) DEFAULT NULL,
  `attr_id` varchar(1024) DEFAULT NULL,
  `label_id` varchar(1024) DEFAULT NULL,
  `knowledge_id` bigint DEFAULT NULL,
  PRIMARY KEY (`doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `knowledge_qa` */

DROP TABLE IF EXISTS `knowledge_qa`;

CREATE TABLE `knowledge_qa` (
  `qa_id` bigint NOT NULL,
  `question` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `answer` blob,
  `creator` varchar(64) DEFAULT NULL,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `attr_id` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `knowledge_id` bigint DEFAULT NULL,
  `label_id` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`qa_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `prompt` */

DROP TABLE IF EXISTS `prompt`;

CREATE TABLE `prompt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(64) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `content` longtext,
  `token` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `public_mcp` */

DROP TABLE IF EXISTS `public_mcp`;

CREATE TABLE `public_mcp` (
  `mcp_id` int NOT NULL AUTO_INCREMENT,
  `type_id` int NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `remark` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `url` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_office` bit(1) DEFAULT NULL,
  PRIMARY KEY (`mcp_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=491 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `public_mcp_type` */

DROP TABLE IF EXISTS `public_mcp_type`;

CREATE TABLE `public_mcp_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type_description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '主键',
  `username` varchar(42) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '用户密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '用户电话',
  `email` varchar(50) DEFAULT NULL COMMENT '用户邮箱',
  `user_right` smallint DEFAULT '0' COMMENT '0普通用户1vip用户',
  `user_image` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像',
  `user_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `user_description` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `user_state` int DEFAULT '0' COMMENT '状态 0不在线 1在线',
  `login_type` varchar(32) DEFAULT 'hoppinzq' COMMENT '第三方登录类型，',
  `extra_message` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '第三方信息',
  `user_extra_id` varchar(256) DEFAULT NULL COMMENT '额外用户ID（因为本表的用户ID是Long类型，万一第三方使用的UUID就存不进来了）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Table structure for table `video` */

DROP TABLE IF EXISTS `video`;

CREATE TABLE `video` (
  `video_id` int NOT NULL AUTO_INCREMENT,
  `video_name_change` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_path_360P` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_path_480P` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_path_720P` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_path_1080P` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_danmu_path` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_bofang` int DEFAULT '0',
  `video_xihuan` int DEFAULT '0',
  `video_fenlei_id` int DEFAULT '0',
  `video_dianzan` int DEFAULT '0',
  `video_miaoshu` longtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `video_createTime` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_param` int DEFAULT NULL,
  `video_time` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_people` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '2',
  `video_biaoqian` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_image_id` int DEFAULT NULL,
  `video_size_360P` bigint DEFAULT NULL,
  `video_size_480P` bigint DEFAULT NULL,
  `video_size_720P` bigint DEFAULT NULL,
  `video_size_1080P` bigint DEFAULT NULL,
  `video_type` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `video_isyc` int DEFAULT '0' COMMENT '0不隐藏1隐藏',
  PRIMARY KEY (`video_id`) USING BTREE,
  KEY `video_fenlei_id` (`video_fenlei_id`) USING BTREE,
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`video_fenlei_id`) REFERENCES `video_fenlei` (`video_fenlei_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=220 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
