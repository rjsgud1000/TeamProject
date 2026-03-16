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
-- Table structure for table `BOARD_POST`
--

DROP TABLE IF EXISTS `BOARD_POST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BOARD_POST` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `member_id` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `nickname` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `category` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_general_ci,
  `viewcount` int DEFAULT '0',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `is_deleted` int DEFAULT '0',
  `accepted_comment_id` int DEFAULT NULL,
  `is_blinded` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `FK` (`member_id`),
  KEY `FK2` (`nickname`),
  CONSTRAINT `FK` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `FK2` FOREIGN KEY (`nickname`) REFERENCES `MEMBER` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BOARD_POST`
--

LOCK TABLES `BOARD_POST` WRITE;
/*!40000 ALTER TABLE `BOARD_POST` DISABLE KEYS */;
INSERT INTO `BOARD_POST` VALUES (24,'test1','테스터1',2,'[질문] 발로란트 입문 질문','요즘 시작했는데 캐릭 추천 좀',20,'2026-03-05 08:30:48',NULL,0,NULL,0),(25,'test2','테스터2',2,'[질문] 로아 뉴비 스펙업','장비 뭐부터 맞추는게 좋아요?',57,'2026-03-05 08:30:48',NULL,0,NULL,0),(26,'test1','테스터1',1,'DEFAULT TITLE','DEFAULT CONTENT',15,'2026-03-06 05:50:14',NULL,0,NULL,0),(27,'admin','관리자',0,'DEFAULT TITLE','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',10,'2026-03-06 08:04:33',NULL,0,NULL,0),(38,'admin','관리자',0,'DEFAULT NOTICE1','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',14,'2026-03-06 08:08:37',NULL,0,NULL,0),(39,'admin','관리자',0,'DEFAULT NOTICE2','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',26,'2026-03-06 08:08:37',NULL,0,NULL,0),(40,'admin','관리자',0,'DEFAULT NOTICE3','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',34,'2026-03-06 08:08:37',NULL,0,NULL,0),(41,'admin','관리자',0,'DEFAULT NOTICE4','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',54,'2026-03-06 08:08:37',NULL,0,NULL,0),(42,'admin','관리자',0,'DEFAULT NOTICE5','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',96,'2026-03-06 08:08:37',NULL,0,NULL,0),(43,'test1','테스터1',1,'DEFAULT TITLE1','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',70,'2026-03-06 08:08:37',NULL,0,NULL,0),(44,'test2','테스터2',2,'DEFAULT TITLE2','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',82,'2026-03-06 08:08:37',NULL,0,NULL,0),(45,'test1','테스터1',3,'DEFAULT TITLE3','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',107,'2026-03-06 08:08:37',NULL,0,NULL,0),(46,'test2','테스터2',1,'DEFAULT TITLE4','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',150,'2026-03-06 08:08:37',NULL,0,NULL,0),(47,'test1','테스터1',2,'DEFAULT TITLE5','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',151,'2026-03-06 08:08:37',NULL,0,NULL,0),(52,'aa','에이에이',1,'테스트','테스트 게시판입니다',52,'2026-03-09 08:21:28',NULL,0,NULL,0),(53,'aa','에이에이',1,'글쓰기','연습',171,'2026-03-09 08:53:17','2026-03-11 03:47:19',0,NULL,0),(54,'admin','관리자',1,'123','123',188,'2026-03-10 01:33:11',NULL,0,NULL,0),(55,'admin','관리자',3,'123','123',91,'2026-03-10 01:33:31',NULL,0,NULL,0),(56,'aa','에이에이',1,'123','123',202,'2026-03-10 04:56:01','2026-03-11 03:46:38',0,NULL,0),(57,'admin','관리자',0,'공지사항)매너채팅??','매너채팅??',106,'2026-03-11 08:20:42','2026-03-12 08:13:51',0,NULL,0),(58,'aa','에이에이',1,'새글 new','new test',9,'2026-03-12 07:11:41',NULL,0,NULL,0),(59,'aa','에이에이',1,'새새글','새새글',9,'2026-03-12 08:26:56',NULL,0,NULL,0),(60,'admin','관리자',1,'새새새글','1234',8,'2026-03-13 05:26:15',NULL,0,NULL,0),(61,'admin','관리자',1,'제발되라','pleasebe',40,'2026-03-13 14:29:25',NULL,0,NULL,1),(62,'devv','개발자01',1,'나도 제발 되라','아 하나남았는데',14,'2026-03-13 06:45:50',NULL,0,NULL,1);
/*!40000 ALTER TABLE `BOARD_POST` ENABLE KEYS */;
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

-- Dump completed on 2026-03-16 15:19:13
