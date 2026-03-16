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
-- Table structure for table `SANCTION`
--

DROP TABLE IF EXISTS `SANCTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SANCTION` (
  `action_id` int NOT NULL AUTO_INCREMENT,
  `target_member_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `admin_member_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `TYPE` varchar(4) COLLATE utf8mb4_general_ci DEFAULT 'WARN',
  `REASON` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `start_at` datetime NOT NULL,
  `end_at` datetime NOT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `member_status` varchar(7) COLLATE utf8mb4_general_ci DEFAULT 'BANNED',
  PRIMARY KEY (`action_id`),
  KEY `target_member_id` (`target_member_id`),
  KEY `admin_member_id` (`admin_member_id`),
  CONSTRAINT `SANCTION_ibfk_1` FOREIGN KEY (`target_member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `SANCTION_ibfk_2` FOREIGN KEY (`admin_member_id`) REFERENCES `MEMBER` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SANCTION`
--

LOCK TABLES `SANCTION` WRITE;
/*!40000 ALTER TABLE `SANCTION` DISABLE KEYS */;
INSERT INTO `SANCTION` VALUES (1,'banned','admin','BAN','규정 위반','2026-03-06 00:00:00','2026-03-10 05:42:57','2026-03-06 04:58:02','END'),(2,'banned','admin','BAN','욕함','2026-03-10 14:43:44','2026-04-09 14:43:44','2026-03-10 05:43:44','BANNED'),(3,'rjsgud','admin','WARN','규정위반','2026-03-10 14:44:39','2026-04-09 14:44:39','2026-03-10 05:44:39','END'),(5,'rjsgud','admin','WARN','경고','2026-03-10 15:26:26','2026-03-10 15:26:26','2026-03-10 06:26:26','END'),(6,'rjsgud','admin','BAN','1234','2026-03-10 15:27:04','2026-03-10 06:27:12','2026-03-10 06:27:04','END'),(7,'rjsgud','admin','BAN','1234','2026-03-10 15:27:35','2026-03-10 06:27:45','2026-03-10 06:27:35','END'),(8,'rjsgud','admin','BAN','1234','2026-03-10 15:28:34','2026-03-10 07:11:57','2026-03-10 06:28:34','END'),(9,'rjsgud','admin','BAN','맘에안듬','2026-03-10 16:12:30','2026-03-10 07:49:17','2026-03-10 07:12:29','END'),(10,'rjsgud','admin','BAN','그냥 마음에 안듦','2026-03-11 17:27:52','2026-03-13 05:19:57','2026-03-11 08:27:51','END'),(11,'rjsgud','admin','BAN','11','2026-03-13 14:20:34','2026-03-13 05:20:50','2026-03-13 05:20:34','END'),(12,'rjsgud','admin','BAN','1','2026-03-13 14:29:45','2026-03-13 14:29:49','2026-03-13 14:29:44','END'),(13,'test1','admin','WARN','1','2026-03-13 15:57:22','2026-03-13 15:57:22','2026-03-13 15:57:21','WARNING'),(14,'rjsgud','admin','WARN','1','2026-03-13 15:57:36','2026-03-13 15:57:36','2026-03-13 15:57:36','END'),(15,'rjsgud','admin','WARN','dddd','2026-03-13 17:38:20','2026-03-13 17:38:20','2026-03-13 17:38:20','END');
/*!40000 ALTER TABLE `SANCTION` ENABLE KEYS */;
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

-- Dump completed on 2026-03-16 17:38:23
