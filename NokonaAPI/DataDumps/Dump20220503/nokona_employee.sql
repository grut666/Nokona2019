-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: nokona
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `employee` (
  `Key` int(11) NOT NULL AUTO_INCREMENT,
  `LastName` varchar(20) DEFAULT NULL,
  `FirstName` varchar(16) DEFAULT NULL,
  `BarCodeID` int(11) DEFAULT NULL,
  `LaborCode` int(11) DEFAULT NULL,
  `EmpID` varchar(6) NOT NULL,
  `Active` int(11) DEFAULT NULL,
  PRIMARY KEY (`Key`),
  UNIQUE KEY `Key_UNIQUE` (`Key`),
  UNIQUE KEY `EmpID_UNIQUE` (`EmpID`),
  UNIQUE KEY `BarCodeID_UNIQUE` (`BarCodeID`),
  KEY `FK_LABOR_CODE_idx` (`LaborCode`),
  CONSTRAINT `FK_LABOR_CODE` FOREIGN KEY (`LaborCode`) REFERENCES `laborcode` (`laborcode`)
) ENGINE=InnoDB AUTO_INCREMENT=821 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (149,'GOMEZ','MARTIN',5002,9,'GOM30',1),(186,'DAVIDSON','JOANNA',473,11,'WAL10',1),(192,'MOLSBEE','SHEILA K.',2772,7,'MOL30',1),(194,'RAYAS','GLORIA P.',3530,5,'RAY10',1),(256,'VILLARREAL','SILVIA R.',1055,11,'VIL20',1),(296,'GONZALES','REYNA',8980,5,'GON20',1),(349,'REA','EUSTOLIA',9400,5,'REA10',1),(385,'MOLSBEE','RANDALL',3586,5,'MOL10',1),(423,'GOMEZ','EUSTOLIA',4993,9,'GOM50',1),(434,'OLVERA','YOLANDA',5118,5,'OLV10',1),(450,'WILBORN','SHIRLEY',6376,7,'WIL10',1),(490,'PRITCHETT','MISTY',2435,11,'PRI10',1),(496,'DYER','SHANNON',6904,11,'DYE10',1),(500,'CONTRERAS','ISAURA',1570,12,'CON20',1),(569,'WATKINS','KENNIE',3362,15,'WAT10',1),(580,'ESTRADA','LIZBETH',9126,12,'EST10',1),(705,'GUERRA ESTRADA','AISHA J.',6754,11,'GUE10',1),(731,'GOMEZ','MARIA',3494,10,'GOM10',1),(740,'HANCOCK','JIMMY',7796,11,'HAN10',1),(757,'GUILLIAMS','SCOTT',489,11,'GUI10',0),(758,'ESTRADA','MARIA',5383,11,'EST20',1),(759,'WAGES','SARAH',5183,11,'WAG10',1),(765,'DIAZ','VALENTINA',7373,11,'DIA60',1),(766,'RUBIO','AUDREY',8099,11,'RUB10',1),(767,'SHELTON','JOHN W.',2351,12,'SHE10',1),(768,'CLAXTON','SAMUEL A.',2736,7,'CLA30',1),(769,'BASALDUA','MARIBEL F.',5220,11,'BAS10',1),(774,'VIILLESCAS','DELORES',1045,11,'VIL30',1),(775,'ROPER','TAMMY',1492,11,'ROP10',1),(777,'CLAXTON','ZACH',5354,7,'CLA40',1),(790,'TYLER','VICKI',8578,11,'TYL10',1),(792,'THOMAS','ASHLEIGH S.',7064,11,'THO10',1),(795,'WASHBURNE','CONNIE',4607,12,'WAS10',1),(802,'WELBORN','CHERYL L.',2005,1,'WEL10',1),(804,'WOODEN','LUCAS R.',778,16,'WOO10',1),(806,'GONZALEZ','NORMA',4576,17,'GON10',1),(807,'MCCLAIN','STEPHANIE',7303,6,'MCC10',1),(809,'TOBEY','JESSE S.',389,9,'TOB10',1),(810,'CERVANTES QUIROZ','RODOLFO',2305,12,'CER10',1),(811,'MENCIA','JUANA MATA',2846,6,'MEN10',1),(813,'CORREA','HAILEY',6164,6,'COR10',1),(816,'DE TIBERIIS','MARYAH',282,10,'DET10',1),(817,'CASTRO','CARLOS',645,11,'CAS10',1),(818,'CASTRO-SUAREZ','PEDRO',6723,12,'CAR20',1),(820,'SANTIAGO','ROSALINDA',9094,6,'SAN10',1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `after_employee_create` AFTER INSERT ON `employee` FOR EACH ROW INSERT INTO employee_log
     (TheKey,LastName, FirstName, BarCodeID, LaborCode, EmpID, Active, TransactionType) values
     (NEW.Key,
      LastName,
      FirstName,
      BarCodeID,
     LaborCode,
     EmpID,
     Active,
     'C'
     ) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `after_employee_update` AFTER UPDATE ON `employee` FOR EACH ROW INSERT INTO employee_log
     (TheKey,LastName, FirstName, BarCodeID, LaborCode, EmpID, Active, TransactionType) values
     (OLD.Key,
      LastName,
      FirstName,
      BarCodeID,
     LaborCode,
     EmpID,
     Active,
     'U'
     ) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `after_employee_delete` AFTER DELETE ON `employee` FOR EACH ROW INSERT INTO employee_log
     (TheKey,LastName, FirstName, BarCodeID, LaborCode, EmpID, Active, TransactionType) values
     (OLD.Key,
      OLD.LastName,
      OLD.FirstName,
     OLD.BarCodeID,
     OLD.LaborCode,
     OLD.EmpID,
     OLD.Active,
     'D'
     ) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-03 15:54:40
