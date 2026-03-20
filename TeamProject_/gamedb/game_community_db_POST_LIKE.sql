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
-- Table structure for table `POST_LIKE`
--

DROP TABLE IF EXISTS `POST_LIKE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `POST_LIKE` (
  `post_id` int NOT NULL,
  `member_id` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`,`member_id`),
  KEY `idx_like_member` (`member_id`),
  CONSTRAINT `fk_like_member` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_post` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_like_member` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_like_post` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POST_LIKE`
--

LOCK TABLES `POST_LIKE` WRITE;
/*!40000 ALTER TABLE `POST_LIKE` DISABLE KEYS */;
INSERT INTO `POST_LIKE` VALUES (24,'admin','2026-03-11 07:17:55'),(24,'rjsgud','2026-03-06 08:15:38'),(24,'rjsgud1000','2026-03-06 08:15:38'),(24,'test1','2026-03-06 08:15:38'),(24,'test2','2026-03-06 08:15:38'),(25,'admin','2026-03-13 06:09:40'),(25,'banned','2026-03-06 08:37:47'),(25,'rjsgud','2026-03-06 08:17:32'),(25,'rjsgud1000','2026-03-06 08:17:32'),(25,'test1','2026-03-06 08:17:32'),(25,'test2','2026-03-06 08:17:32'),(26,'aa','2026-03-06 08:37:47'),(26,'admin','2026-03-16 06:21:11'),(26,'banned','2026-03-06 08:37:47'),(26,'devv','2026-03-17 06:15:08'),(26,'rjsgud','2026-03-06 08:17:32'),(26,'rjsgud1000','2026-03-06 08:17:32'),(26,'test1','2026-03-06 08:17:32'),(26,'test2','2026-03-06 08:17:32'),(45,'admin','2026-03-06 08:17:32'),(45,'rjsgud','2026-03-06 08:17:32'),(45,'rjsgud1000','2026-03-06 08:17:32'),(45,'test1','2026-03-06 08:17:32'),(45,'test2','2026-03-06 08:17:32'),(46,'aa','2026-03-13 05:21:51'),(47,'admin','2026-03-06 08:17:32'),(47,'rjsgud','2026-03-06 08:17:32'),(47,'rjsgud1000','2026-03-06 08:17:32'),(47,'test1','2026-03-06 08:17:32'),(47,'test2','2026-03-06 08:17:32'),(52,'aa','2026-03-10 08:40:16'),(53,'aa','2026-03-13 05:09:54'),(54,'aa','2026-03-11 06:01:40'),(56,'aa','2026-03-13 05:53:34'),(56,'admin','2026-03-13 06:17:50'),(58,'aa','2026-03-13 06:18:36'),(60,'aa','2026-03-19 06:12:46'),(60,'devv','2026-03-17 06:14:04'),(61,'admin','2026-03-13 08:36:44'),(70,'aa','2026-03-19 07:30:30'),(70,'rjsgud','2026-03-19 07:05:20');
/*!40000 ALTER TABLE `POST_LIKE` ENABLE KEYS */;
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

-- Dump completed on 2026-03-20 14:00:56
