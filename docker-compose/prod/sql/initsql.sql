-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: my_db
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
                      `id` int unsigned NOT NULL AUTO_INCREMENT,
                      `user_id` int unsigned NOT NULL,
                      `file_key` varchar(150) NOT NULL,
                      `file_name` varchar(45) NOT NULL,
                      `file_type` varchar(15) NOT NULL,
                      `file_size` bigint unsigned NOT NULL,
                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (`id`),
                      KEY `file_member_idx_idx` (`user_id`),
                      CONSTRAINT `file_member_idx` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
INSERT INTO `file` VALUES (3,280,'files/wc6zxy52c7w6dz4kvqh3tmtu6eylhr81_1744793335194.jpg','� �T�','jpg',47801,'2025-04-16 08:48:55');
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memo`
--

DROP TABLE IF EXISTS `memo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memo` (
                      `id` int unsigned NOT NULL AUTO_INCREMENT,
                      `user_id` int unsigned NOT NULL,
                      `content` varchar(1000) NOT NULL,
                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (`id`),
                      KEY `memo_member_idx_idx` (`user_id`),
                      CONSTRAINT `memo_member_idx` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memo`
--

LOCK TABLES `memo` WRITE;
/*!40000 ALTER TABLE `memo` DISABLE KEYS */;
INSERT INTO `memo` VALUES (1,280,'Hello World','2025-04-05 10:04:12','2025-04-05 10:04:12'),(2,280,'수정 완료~','2025-04-05 10:44:42','2025-04-05 11:03:38'),(4,280,'메모 내용','2025-04-16 05:41:45','2025-04-16 05:41:45'),(5,280,'new Memo','2025-04-30 01:40:42','2025-04-30 01:40:42');
/*!40000 ALTER TABLE `memo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                      `id` int unsigned NOT NULL AUTO_INCREMENT,
                      `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                      `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
                      `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (280,'qa1','4a4b555e21cf12bf70f0539e8f43f2e1f9528c2e6422191dfb7db6dfa07ac3c296589fc0e5be571fab2438039e78e91229ba2ece738fa462674a6ee699cfe28e7ce129926e891c1d4de0c959b476ca0e','USER','2025-04-05 10:04:12','2025-04-05 10:04:12'),(281,'qa2','9cb032759dee329a500e1ab6cd2f791a55a6b2923d624d3dc37d588f17a4dc239ed5e35015fcb2282c7e29e9a3c8a943743d81d8e4ad13f0c76161a195c5be8fc348baa716d3ac64279aaca274c52688','USER','2025-04-29 05:08:03','2025-04-29 05:08:03'),(282,'qa3','83a2053356d0a62677f558802d09ace27e323c352fd98423de04be9cef4be41d21983799237d8c6e65b65ddcb7a005196eebc9712e326a341a176d3ac63ea78760c4a173ab595d24d749eed0357a8d43','USER','2025-04-29 05:27:47','2025-04-29 05:27:47');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'my_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-30 10:52:44
