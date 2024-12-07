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
-- Temporary view structure for view `operationscompletedview`
--

DROP TABLE IF EXISTS `operationscompletedview`;
/*!50001 DROP VIEW IF EXISTS `operationscompletedview`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `operationscompletedview` AS SELECT 
 1 AS `key`,
 1 AS `completedcount`,
 1 AS `cost`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `laborcostview`
--

DROP TABLE IF EXISTS `laborcostview`;
/*!50001 DROP VIEW IF EXISTS `laborcostview`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `laborcostview` AS SELECT 
 1 AS `JobID`,
 1 AS `JobIdTrimmed`,
 1 AS `OpCode`,
 1 AS `Description`,
 1 AS `Sequence`,
 1 AS `levelcode`,
 1 AS `HourlyRateSAHCalc`,
 1 AS `op.hourlyratesah`,
 1 AS `levelrate`,
 1 AS `total`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `ticketdetailview`
--

DROP TABLE IF EXISTS `ticketdetailview`;
/*!50001 DROP VIEW IF EXISTS `ticketdetailview`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ticketdetailview` AS SELECT 
 1 AS `Key`,
 1 AS `Ticket`,
 1 AS `Operation`,
 1 AS `StatusDate`,
 1 AS `Status`,
 1 AS `StdQty`,
 1 AS `SAH`,
 1 AS `levelRate`,
 1 AS `updatedSequence`,
 1 AS `OperationDescription`,
 1 AS `levelCode`,
 1 AS `laborCode`,
 1 AS `BarCode`,
 1 AS `Qty`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `totaloperationsview`
--

DROP TABLE IF EXISTS `totaloperationsview`;
/*!50001 DROP VIEW IF EXISTS `totaloperationsview`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `totaloperationsview` AS SELECT 
 1 AS `key`,
 1 AS `totalcount`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `operationscompletedview`
--

/*!50001 DROP VIEW IF EXISTS `operationscompletedview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `operationscompletedview` AS select `t1`.`Key` AS `key`,count(`t1`.`UpdatedSequence`) AS `completedcount`,sum(((`t1`.`HourlyRateSAH` * `t1`.`LevelRate`) * (coalesce(`t1`.`ActualQuantity1`,0) + coalesce(`t1`.`ActualQuantity2`,0)))) AS `cost` from `ticketdetail` `t1` where ((`t1`.`Status` = 'C') and (`t1`.`HourlyRateSAH` > 0)) group by `t1`.`Key` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `laborcostview`
--

/*!50001 DROP VIEW IF EXISTS `laborcostview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `laborcostview` AS select `jh`.`JobId` AS `JobID`,trim(trim(trailing '-LH' from trim(trailing '-RH' from `jh`.`JobId`))) AS `JobIdTrimmed`,`jd`.`OpCode` AS `OpCode`,`op`.`Description` AS `Description`,`jd`.`Sequence` AS `Sequence`,`op`.`LevelCode` AS `levelcode`,(case substr(`jh`.`JobId`,-(3)) when '-RH' then (`op`.`HourlyRateSAH` * 1.1) else `op`.`HourlyRateSAH` end) AS `HourlyRateSAHCalc`,`op`.`HourlyRateSAH` AS `op.hourlyratesah`,`lc`.`LevelRate` AS `levelrate`,(`lc`.`LevelRate` * (case substr(`jh`.`JobId`,-(3)) when '-RH' then (`op`.`HourlyRateSAH` * 1.1) else `op`.`HourlyRateSAH` end)) AS `total` from (((`jobheader` `jh` join `jobdetail` `jd` on((trim(trim(trailing '-LH' from trim(trailing '-RH' from `jh`.`JobId`))) = `jd`.`JobId`))) join `operation` `op` on((`jd`.`OpCode` = `op`.`OpCode`))) join `levelcode` `lc` on((`op`.`LevelCode` = `lc`.`LevelCode`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ticketdetailview`
--

/*!50001 DROP VIEW IF EXISTS `ticketdetailview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ticketdetailview` AS select `td`.`Key` AS `Key`,concat(`td`.`Key`,lpad(`td`.`Sequence`,2,'0')) AS `Ticket`,`td`.`OpCode` AS `Operation`,`td`.`StatusDate` AS `StatusDate`,`td`.`Status` AS `Status`,`td`.`StandardQuantity` AS `StdQty`,`td`.`HourlyRateSAH` AS `SAH`,`td`.`LevelRate` AS `levelRate`,`td`.`UpdatedSequence` AS `updatedSequence`,`td`.`OperationDescription` AS `OperationDescription`,`td`.`LevelCode` AS `levelCode`,`td`.`LaborCode` AS `laborCode`,`td`.`BarCodeID1` AS `BarCode`,`td`.`ActualQuantity1` AS `Qty` from `ticketdetail` `td` union select `td`.`Key` AS `Key`,concat(`td`.`Key`,`td`.`Sequence`) AS `Ticket`,`td`.`OpCode` AS `Operation`,`td`.`StatusDate` AS `StatusDate`,`td`.`Status` AS `Status`,`td`.`StandardQuantity` AS `StdQty`,`td`.`HourlyRateSAH` AS `SAH`,`td`.`LevelRate` AS `levelRate`,`td`.`UpdatedSequence` AS `updatedSequence`,`td`.`OperationDescription` AS `OperationDescription`,`td`.`LevelCode` AS `levelCode`,`td`.`LaborCode` AS `laborCode`,`td`.`BarCodeID2` AS `BarCode`,`td`.`ActualQuantity2` AS `Qty` from `ticketdetail` `td` where (`td`.`BarCodeID2` <> 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `totaloperationsview`
--

/*!50001 DROP VIEW IF EXISTS `totaloperationsview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `totaloperationsview` AS select `t1`.`Key` AS `key`,count(`t1`.`UpdatedSequence`) AS `totalcount` from `ticketdetail` `t1` where (`t1`.`HourlyRateSAH` > 0) group by `t1`.`Key` */;
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

-- Dump completed on 2024-12-07  8:41:58
