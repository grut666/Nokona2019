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
-- Temporary view structure for view `ticketview`
--

DROP TABLE IF EXISTS `ticketview`;
/*!50001 DROP VIEW IF EXISTS `ticketview`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `ticketview` AS SELECT 
 1 AS `Key`,
 1 AS `JobID`,
 1 AS `CreatedDate`,
 1 AS `Header_Status`,
 1 AS `Header_Status_Date`,
 1 AS `Quantity`,
 1 AS `Description`,
 1 AS `Sequence`,
 1 AS `OpCode`,
 1 AS `Detail_Status_Date`,
 1 AS `Detail_Status`,
 1 AS `StandardQuantity`,
 1 AS `HourlyRateSAH`,
 1 AS `BarCodeID`,
 1 AS `LaborRate`,
 1 AS `UpdatedSequence`,
 1 AS `ActualQuantity`,
 1 AS `OperationDescription`,
 1 AS `LaborDescription`,
 1 AS `LaborCode`,
 1 AS `LastName`,
 1 AS `FirstName`,
 1 AS `EmpId`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `jobview`
--

DROP TABLE IF EXISTS `jobview`;
/*!50001 DROP VIEW IF EXISTS `jobview`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `jobview` AS SELECT 
 1 AS `JobID`,
 1 AS `JobIdTrimmed`,
 1 AS `OpCode`,
 1 AS `Sequence`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `ticketview`
--

/*!50001 DROP VIEW IF EXISTS `ticketview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ticketview` AS select `th`.`Key` AS `Key`,`th`.`JobID` AS `JobID`,`th`.`CreatedDate` AS `CreatedDate`,`th`.`Status` AS `Header_Status`,`th`.`StatusDate` AS `Header_Status_Date`,`th`.`Quantity` AS `Quantity`,`th`.`Description` AS `Description`,`td`.`Sequence` AS `Sequence`,`td`.`OpCode` AS `OpCode`,`td`.`StatusDate` AS `Detail_Status_Date`,`td`.`Status` AS `Detail_Status`,`td`.`StandardQuantity` AS `StandardQuantity`,`td`.`HourlyRateSAH` AS `HourlyRateSAH`,`td`.`BarCodeID` AS `BarCodeID`,`td`.`LaborRate` AS `LaborRate`,`td`.`UpdatedSequence` AS `UpdatedSequence`,`td`.`ActualQuantity` AS `ActualQuantity`,`td`.`OperationDescription` AS `OperationDescription`,`td`.`LaborDescription` AS `LaborDescription`,`td`.`LaborCode` AS `LaborCode`,`em`.`LastName` AS `LastName`,`em`.`FirstName` AS `FirstName`,`em`.`EmpID` AS `EmpId` from ((`ticketheader` `th` join `ticketdetail` `td` on((`th`.`Key` = `td`.`Key`))) join `employee` `em` on((`td`.`BarCodeID` = `em`.`BarCodeID`))) order by `td`.`Key`,`td`.`Sequence` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `jobview`
--

/*!50001 DROP VIEW IF EXISTS `jobview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `jobview` AS select `jh`.`JobId` AS `JobID`,trim(trim(trailing '-LH' from trim(trailing '-RH' from `jh`.`JobId`))) AS `JobIdTrimmed`,`jd`.`OpCode` AS `OpCode`,`jd`.`Sequence` AS `Sequence` from (`jobheader` `jh` join `jobdetail` `jd` on((trim(trim(trailing '-LH' from trim(trailing '-RH' from `jh`.`JobId`))) = `jd`.`JobId`))) where (`jh`.`JobId` = 'A-1150C-GR-LH') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-28 18:58:35
