-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: nokona
-- ------------------------------------------------------
-- Server version	8.2.0

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

--
-- Table structure for table `efficiency_levels`
--

DROP TABLE IF EXISTS `efficiency_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `efficiency_levels` (
  `name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `percentage` int DEFAULT NULL,
  `range` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `l1` float DEFAULT NULL,
  `l2` float DEFAULT NULL,
  `l3` float DEFAULT NULL,
  `l4` float DEFAULT NULL,
  `l5` float DEFAULT NULL,
  `l6` float DEFAULT NULL,
  `l7` float DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `efficiency_levels`
--

LOCK TABLES `efficiency_levels` WRITE;
/*!40000 ALTER TABLE `efficiency_levels` DISABLE KEYS */;
INSERT INTO `efficiency_levels` VALUES ('BASE',0,'0-10%',12,12,12,12,12,12,13),('E1',10,'10-19%',12.08,12.1,12.2,12.3,12.4,12.5,13.5),('E10',100,'100-109%',12.75,13,14,15,16,17,18),('E11',110,'110-119%',12.83,13.1,14.2,15.3,16.4,17.5,18.5),('E12',120,'120-129%',12.9,13.2,14.4,15.6,16.8,18,19),('E13',130,'130-139%',12.98,13.3,14.6,15.9,17.2,18.5,19.5),('E14',140,'140-149%',13.05,13.4,14.8,16.2,17.6,19,20),('E2',20,'20-29%',12.15,12.2,12.4,12.6,12.8,13,14),('E3',30,'30-39%',12.23,12.3,12.6,12.9,13.2,13.5,14.5),('E4',40,'40-49%',12.3,12.4,12.8,13.2,13.6,14,15),('E5',50,'50-59%',12.38,12.5,13,13.5,14,14.5,15.5),('E6',60,'60-69%',12.45,12.6,13.2,13.8,14.4,15,16),('E7',70,'70-79%',12.53,12.7,13.4,14.1,14.8,15.5,16.5),('E8',80,'80-89%',12.6,12.8,13.6,14.4,15.2,16,17),('E9',90,'90-99%',12.68,12.9,13.8,14.7,15.6,16.5,17.5);
/*!40000 ALTER TABLE `efficiency_levels` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-07  8:41:56