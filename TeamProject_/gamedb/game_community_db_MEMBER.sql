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
-- Table structure for table `MEMBER`
--

DROP TABLE IF EXISTS `MEMBER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MEMBER` (
  `member_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `nickname` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `zipcode` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `addr1` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `addr2` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `addr3` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `addr4` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER',
  `status` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE',
  `updated_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `nickname` (`nickname`),
  UNIQUE KEY `uq_member_email` (`email`),
  UNIQUE KEY `uq_member_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEMBER`
--

LOCK TABLES `MEMBER` WRITE;
/*!40000 ALTER TABLE `MEMBER` DISABLE KEYS */;
INSERT INTO `MEMBER` VALUES ('aa','김에이','pbkdf2$120000$GohW+NwwLOiLSnNVS4OQ7A==$kkviczncFcV3j9nV3U1uPQtAh5mtvQceE7Gw6dItDnQ=','에이에이','06158','서울 강남구 테헤란로79길 11','서울 강남구 삼성동 144-33','에이타워 3층','(삼성동)','woman','a@a.com','01012345676','USER','ACTIVE','2026-03-09 03:40:13','2026-03-06 08:21:46'),('admin','관리자','pbkdf2$120000$1KoR91EE9K7xO9mOdywCEg==$LbtIBtV/R7vUvpFMRplN0xJNxxqVXO4arUBILUVMXeI=','관리자','13536','경기 성남시 분당구 판교역로2번길 1','경기 성남시 분당구 백현동 582-6','HD빌딩 3층','(백현동)','man','121331@nave.com','01045564123','ADMIN','ACTIVE','2026-03-05 08:10:20','2026-03-05 08:10:20'),('admin1','관리자1','pbkdf2$120000$HbukVnZ70JShSXfAv0yJ8w==$3pF3M9gIVBcgEg5ppF2KivtIBINXNfpOYxP45xJBkS0=','관리자1','13490','경기 성남시 분당구 동판교로266번길 20','경기 성남시 분당구 삼평동 715','효산빌딩','(삼평동)','man','11222@vics.com','01032648754','ADMIN','ACTIVE','2026-03-09 05:22:23','2026-03-09 03:37:48'),('banned','김건형','pbkdf2$120000$xPgq1fuQryeiU4z9RwHrTQ==$IQ2gmL00yEotfnaIFLns2AXwKYNssPtwx03SnohGJF4=','욕한놈','62404','광주 광산구 가마길 2-21',NULL,'어디어디','(명도동)','man','11@11.com','1111111112','USER','BANNED','2026-03-06 04:51:59','2026-03-06 04:51:59'),('devv','개발자1','pbkdf2$120000$k8D/sX1D+lh5V8I9pzK9Zw==$MBaHhPsRIU1XIGbiznvpkjksGhPuxRx0pSxa3qzYteM=','개발자01','13529','경기 성남시 분당구 판교역로 166','경기 성남시 분당구 백현동 532','master','(백현동)','man','devv01@test.com','01012345677','USER','ACTIVE','2026-03-06 13:38:24','2026-03-06 11:08:26'),('rjsgud','김인수','pbkdf2$120000$wSD5UL9epQNMsrVvMRjhWA==$vXv1UljYmAIM94eQpJXE29yJlTmrS7anmhtAGBggTA4=','하이','36996','경북 문경시 가은읍 양산개1길 2','경북 문경시 가은읍 왕능리 294-12','효산빌딩',NULL,'man','112tkwk@naver.com','1111111111','USER','BANNED','2026-03-09 05:36:56','2026-03-06 08:06:28'),('rjsgud1000','김건형','pbkdf2$120000$u/LVQiCA/JnViGZUgwDJkA==$81TLFul6D+7inxfVhM+CCHAysTvII/niDFXLIUKCJzw=','파랑새','25642','강원특별자치도 강릉시 왕산면 안반데기길 41-14','강원특별자치도 강릉시 왕산면 대기리 1142','왕산빌딩 4층',NULL,'man','112tkwk@naver.co','01012345678','USER','WITHDRAWN','2026-03-06 08:01:19','2026-03-05 08:02:59'),('test1','test1','hash_test1','테스터1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'USER','ACTIVE','2026-03-05 08:30:32','2026-03-05 08:30:32'),('test2','test2','hash_test2','테스터2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'USER','ACTIVE','2026-03-05 08:30:32','2026-03-05 08:30:32');
/*!40000 ALTER TABLE `MEMBER` ENABLE KEYS */;
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

-- Dump completed on 2026-03-09 15:13:46
