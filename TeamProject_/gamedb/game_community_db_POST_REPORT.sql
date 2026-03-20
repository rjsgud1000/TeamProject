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
-- Table structure for table `POST_REPORT`
--

DROP TABLE IF EXISTS `POST_REPORT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `POST_REPORT` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `member_id` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `reason` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'PENDING',
  PRIMARY KEY (`report_id`),
  UNIQUE KEY `uq_post_report` (`post_id`,`member_id`),
  KEY `fk_post_report_member` (`member_id`),
  CONSTRAINT `fk_post_report_member` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `fk_post_report_post` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POST_REPORT`
--

LOCK TABLES `POST_REPORT` WRITE;
/*!40000 ALTER TABLE `POST_REPORT` DISABLE KEYS */;
INSERT INTO `POST_REPORT` VALUES (1,56,'aa','욕설/비방','2026-03-13 05:16:52','REJECTED'),(2,46,'aa','도배/스팸','2026-03-13 05:21:59','RESOLVED'),(3,60,'admin','욕설/비방','2026-03-13 05:50:29','RESOLVED'),(4,46,'admin','욕설/비방','2026-03-13 05:50:56','RESOLVED'),(5,43,'aa','욕설/비방','2026-03-13 06:08:46','REJECTED'),(6,25,'admin','도배/스팸','2026-03-13 06:09:43','REJECTED'),(7,56,'admin','도배/스팸','2026-03-13 06:17:52','REJECTED'),(8,61,'admin','욕설/비방','2026-03-13 06:18:10','RESOLVED'),(9,58,'aa','욕설/비방','2026-03-13 06:18:38','RESOLVED'),(10,47,'aa','도배/스팸','2026-03-13 06:19:03','REJECTED'),(11,62,'admin','기타','2026-03-13 08:34:03','RESOLVED'),(12,59,'admin','음란/부적절','2026-03-13 08:36:20','PENDING'),(13,54,'admin','욕설/비방','2026-03-13 08:36:28','PENDING'),(14,61,'aa','도배/스팸','2026-03-13 08:46:53','RESOLVED'),(15,26,'admin','욕설/비방','2026-03-16 06:21:15','PENDING'),(16,63,'devv','욕설/비방','2026-03-16 06:35:33','RESOLVED');
/*!40000 ALTER TABLE `POST_REPORT` ENABLE KEYS */;
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
