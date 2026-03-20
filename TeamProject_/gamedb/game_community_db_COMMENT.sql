-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: ls-48ebe287859564129511134a6d11233300c07bca.czgiqoag813n.ap-northeast-2.rds.amazonaws.com    Database: game_community_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `COMMENT`
--

DROP TABLE IF EXISTS `COMMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `COMMENT` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `member_id` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `parent_comment_id` int DEFAULT NULL,
  `is_accepted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `COMMENT_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`),
  CONSTRAINT `COMMENT_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMMENT`
--

LOCK TABLES `COMMENT` WRITE;
/*!40000 ALTER TABLE `COMMENT` DISABLE KEYS */;
INSERT INTO `COMMENT` VALUES (12,25,'test2','일단 감도 세팅부터 ㄱㄱ','2026-03-05 08:30:59','2026-03-05 08:30:59',0,NULL,0),(13,55,'admin','12345','2026-03-10 10:52:56','2026-03-10 11:38:03',1,NULL,0),(14,54,'admin','3211','2026-03-10 10:56:48','2026-03-10 14:58:50',1,NULL,0),(15,54,'admin','123','2026-03-10 11:47:32','2026-03-10 14:58:53',1,14,0),(16,54,'admin','1234','2026-03-10 11:47:44','2026-03-10 14:58:47',1,NULL,0),(17,55,'admin','123','2026-03-10 11:55:11','2026-03-10 15:29:29',1,NULL,0),(18,53,'aa','테스트','2026-03-10 12:23:46','2026-03-10 12:29:47',1,NULL,0),(19,55,'admin','123','2026-03-10 12:45:29','2026-03-10 03:45:28',0,NULL,0),(20,55,'admin','123','2026-03-10 12:45:34','2026-03-10 03:45:34',0,NULL,0),(21,55,'aa','123','2026-03-10 14:10:40','2026-03-10 05:10:39',0,NULL,0),(22,55,'aa','123','2026-03-10 14:10:44','2026-03-10 05:10:44',0,NULL,0),(23,54,'admin','1234','2026-03-10 14:21:04','2026-03-10 16:55:36',1,NULL,0),(24,56,'admin','123','2026-03-10 14:21:58','2026-03-10 14:40:27',1,NULL,0),(25,54,'admin','12344','2026-03-10 14:28:59','2026-03-10 15:02:57',1,NULL,0),(26,56,'admin','123','2026-03-10 14:40:33','2026-03-10 14:58:26',1,NULL,0),(27,56,'admin','123','2026-03-10 14:40:37','2026-03-10 14:58:32',1,26,0),(28,56,'admin','123','2026-03-10 14:58:29','2026-03-10 14:58:34',1,27,0),(29,54,'admin','1222','2026-03-10 14:58:57','2026-03-10 16:55:46',1,25,0),(30,54,'admin1','111','2026-03-10 15:01:52','2026-03-10 15:01:55',1,NULL,0),(31,54,'admin1','1111','2026-03-10 15:02:04','2026-03-10 16:55:47',1,29,0),(32,55,'admin','24','2026-03-10 15:11:50','2026-03-10 16:23:59',1,21,0),(33,54,'rjsgud','12','2026-03-10 15:17:28','2026-03-10 16:55:15',1,29,0),(34,56,'admin','1234','2026-03-10 15:33:42','2026-03-10 15:33:54',1,NULL,0),(35,53,'admin','123','2026-03-10 15:37:21','2026-03-10 15:37:31',1,NULL,0),(36,53,'admin','1234','2026-03-10 15:37:29','2026-03-10 17:15:18',1,35,0),(37,56,'admin','123441','2026-03-10 15:43:29','2026-03-10 16:59:32',1,NULL,0),(38,53,'admin','23','2026-03-10 15:48:57','2026-03-10 17:15:19',1,36,0),(39,56,'admin','14','2026-03-10 16:59:27','2026-03-10 16:59:56',1,37,0),(40,56,'admin','123','2026-03-10 16:59:58','2026-03-10 17:00:05',1,NULL,0),(41,56,'admin','123','2026-03-10 17:00:02','2026-03-10 17:00:07',1,40,0),(42,54,'admin','1233','2026-03-10 17:07:31','2026-03-10 17:07:41',1,NULL,0),(43,54,'admin','12312','2026-03-10 17:07:39','2026-03-10 17:07:41',1,42,0),(44,56,'admin','123','2026-03-10 17:11:06','2026-03-10 17:11:10',1,NULL,0),(45,56,'admin','123','2026-03-10 17:11:08','2026-03-10 17:20:33',1,44,0),(46,46,'admin','123','2026-03-10 17:13:10','2026-03-10 17:13:18',1,NULL,0),(47,46,'admin','123','2026-03-10 17:13:13','2026-03-10 17:13:18',1,46,0),(48,53,'admin','123','2026-03-10 17:15:20','2026-03-10 17:15:23',1,NULL,0),(49,53,'admin','1233','2026-03-10 17:15:24','2026-03-10 17:15:36',1,NULL,0),(50,53,'admin','123','2026-03-10 17:15:35','2026-03-10 17:15:41',1,49,0),(51,54,'admin','123','2026-03-10 17:16:15','2026-03-10 17:16:22',1,NULL,0),(52,54,'admin','123','2026-03-10 17:16:18','2026-03-10 17:16:24',1,51,0),(53,54,'admin','1231','2026-03-10 17:17:05','2026-03-10 17:17:10',1,NULL,0),(54,54,'admin','444','2026-03-10 17:17:09','2026-03-10 17:17:11',1,53,0),(55,54,'rjsgud','444','2026-03-10 17:19:32','2026-03-10 17:19:37',1,NULL,0),(56,54,'rjsgud','444','2026-03-10 17:19:35','2026-03-10 17:19:38',1,55,0),(57,56,'admin','123','2026-03-10 17:20:35','2026-03-10 17:20:42',1,NULL,0),(58,56,'admin','444','2026-03-10 17:20:41','2026-03-10 17:20:43',1,57,0),(59,44,'admin','333','2026-03-10 17:22:05','2026-03-10 17:22:09',1,NULL,0),(60,44,'admin','213','2026-03-10 17:22:08','2026-03-10 17:22:10',1,59,0),(61,56,'admin','444421','2026-03-10 17:32:11','2026-03-10 17:32:36',1,NULL,0),(62,56,'admin','2321','2026-03-10 17:32:21','2026-03-10 17:32:36',1,61,0),(63,56,'admin','123123','2026-03-10 17:32:26','2026-03-10 17:32:36',1,62,0),(64,56,'admin','12313','2026-03-10 17:32:29','2026-03-10 17:32:36',1,63,0),(65,56,'admin','123123','2026-03-10 17:32:34','2026-03-10 17:32:36',1,61,0),(66,56,'admin','1231','2026-03-10 17:32:43','2026-03-10 18:07:23',1,NULL,0),(67,56,'admin','3322','2026-03-10 18:07:22','2026-03-10 18:07:23',1,66,0),(68,46,'aa','	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.','2026-03-11 12:06:40','2026-03-11 03:06:40',0,NULL,0),(69,46,'aa','	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.	\r\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.','2026-03-11 12:06:45','2026-03-11 03:06:46',0,NULL,0),(71,25,'aa','오감도','2026-03-12 14:38:15','2026-03-12 14:38:39',1,12,0),(72,53,'aa','댓글조회수 테스트','2026-03-12 16:02:26','2026-03-12 07:02:24',0,NULL,0),(73,53,'aa','최고댓글수 테스트중','2026-03-12 16:02:47','2026-03-12 07:02:46',0,NULL,0),(74,53,'aa','1','2026-03-12 16:02:50','2026-03-12 07:02:48',0,NULL,0),(75,53,'aa','2','2026-03-12 16:02:52','2026-03-12 07:02:50',0,NULL,0),(76,53,'aa','3','2026-03-12 16:02:54','2026-03-12 07:02:53',0,NULL,0),(77,53,'aa','4','2026-03-12 16:02:57','2026-03-12 07:02:55',0,NULL,0),(78,53,'aa','5','2026-03-12 16:02:59','2026-03-12 07:02:58',0,NULL,0),(79,53,'aa','6','2026-03-12 16:03:02','2026-03-12 07:03:01',0,NULL,0),(80,53,'aa','7','2026-03-12 16:03:05','2026-03-12 07:03:04',0,NULL,0),(81,53,'aa','8','2026-03-12 16:03:09','2026-03-12 07:03:07',0,NULL,0),(82,53,'aa','9','2026-03-12 16:03:13','2026-03-12 07:03:12',0,NULL,0),(83,53,'aa','10','2026-03-12 16:03:17','2026-03-12 07:03:15',0,NULL,0),(84,45,'aa','1234','2026-03-13 14:08:32','2026-03-13 05:08:32',0,NULL,0),(85,45,'aa','댓글','2026-03-13 14:08:40','2026-03-13 05:08:40',0,NULL,0),(86,61,'admin','새댓글','2026-03-13 06:13:41','2026-03-13 06:13:41',0,NULL,0),(87,56,'admin','1234','2026-03-16 05:29:31','2026-03-16 05:29:38',1,NULL,0),(88,56,'admin','123','2026-03-16 05:29:36','2026-03-16 05:29:38',1,87,0),(89,60,'aa','댓글 테스트','2026-03-16 08:15:22','2026-03-18 07:17:40',1,NULL,0),(90,60,'aa','테스트','2026-03-16 08:15:29','2026-03-16 08:15:29',0,NULL,0),(91,60,'aa','테스트','2026-03-16 08:15:29','2026-03-16 08:15:29',0,NULL,0),(92,60,'aa','테스트','2026-03-16 08:15:35','2026-03-16 08:15:35',0,NULL,0),(93,60,'aa','테스트','2026-03-16 08:15:35','2026-03-16 08:15:35',0,NULL,0),(94,60,'aa','두번','2026-03-16 08:30:32','2026-03-16 08:30:32',0,NULL,0),(95,60,'aa','한번','2026-03-16 08:30:41','2026-03-16 08:30:41',0,NULL,0),(96,58,'aa','1','2026-03-16 08:30:51','2026-03-18 07:18:21',1,NULL,0),(97,58,'aa','2','2026-03-16 08:30:52','2026-03-16 08:30:52',0,NULL,0),(98,58,'aa','3','2026-03-16 08:30:55','2026-03-16 08:30:54',0,NULL,0),(99,58,'aa','4','2026-03-16 08:30:57','2026-03-16 08:30:57',0,NULL,0),(100,58,'aa','5','2026-03-16 08:30:59','2026-03-16 08:30:59',0,NULL,0),(101,58,'aa','6','2026-03-16 08:31:02','2026-03-16 08:31:02',0,NULL,0),(102,58,'aa','7','2026-03-16 08:31:05','2026-03-16 08:31:05',0,NULL,0),(103,58,'admin','ㅇㅇ','2026-03-17 05:53:47','2026-03-17 05:53:46',0,NULL,0),(104,53,'admin','ㅇㅇ','2026-03-17 06:02:47','2026-03-17 06:02:47',0,72,0),(105,46,'aa','ㅁㄴ아러;ㅣ만핕ㅊㅋ;ㅣ퍼ㅏ팈처파ㅣㅌㅋ처파ㅣ컽ㅊ파ㅣㅜㅌㅊㅋ,.ㅣ풑,.ㅋ춮.,ㅌㅋ추프,ㅌㅋ춮.','2026-03-17 06:03:17','2026-03-17 06:27:18',0,NULL,0),(106,46,'aa','ㅁㅇㄻㄴㅇ','2026-03-17 06:03:20','2026-03-17 06:03:19',0,NULL,0),(107,60,'devv','댓구리테스트','2026-03-17 06:14:01','2026-03-17 06:14:01',0,NULL,0),(108,55,'devv','하나둘셋넷','2026-03-17 06:14:45','2026-03-17 06:14:45',0,19,0),(109,26,'devv','개추 ㅋㅋ','2026-03-17 06:15:15','2026-03-17 06:15:15',0,NULL,0),(110,46,'aa','ㅇ너라ㅣ넘리ㅏㄴ멍ㄹㄴ멍;러;ㅣㅍ처ㅏ;ㅣㅌㅋ챂;ㅣㅌ캋ㅍ;ㅣㅏㅌㅊㅋ;ㅣㅓㄴ망러ㅏㅣㄴㅁ어라ㅣㄴ아','2026-03-17 06:27:28','2026-03-17 06:27:27',0,105,0),(111,68,'aa','1234','2026-03-19 04:43:40','2026-03-19 04:43:41',0,NULL,0),(112,65,'aa','1234','2026-03-19 04:53:42','2026-03-19 04:53:43',0,NULL,0),(113,68,'aa','1234','2026-03-19 05:01:21','2026-03-19 05:01:22',0,NULL,0),(114,64,'aa','저요','2026-03-19 05:38:10','2026-03-19 05:38:12',0,NULL,0),(115,69,'aa','저요','2026-03-19 05:39:44','2026-03-19 07:45:39',1,NULL,0),(116,69,'aa','저요 저요','2026-03-19 06:18:35','2026-03-19 06:18:36',0,NULL,0),(117,70,'devv','저 갈래요','2026-03-19 07:04:10','2026-03-19 07:04:10',0,NULL,0),(118,60,'rjsgud','1','2026-03-19 07:29:56','2026-03-19 07:29:56',0,90,0),(119,70,'rjsgud','나도껴줘잉 끼잉끼잉','2026-03-19 09:11:43','2026-03-19 09:11:43',0,NULL,0),(120,65,'aa','12345','2026-03-20 01:41:52','2026-03-20 01:41:51',0,NULL,0);
/*!40000 ALTER TABLE `COMMENT` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-20 14:00:57
