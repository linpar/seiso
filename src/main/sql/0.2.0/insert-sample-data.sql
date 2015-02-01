-- MySQL dump 10.13  Distrib 5.6.22, for Linux (x86_64)
--
-- Host: localhost    Database: seiso
-- ------------------------------------------------------
-- Server version	5.6.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `data_center`
--

LOCK TABLES `data_center` WRITE;
/*!40000 ALTER TABLE `data_center` DISABLE KEYS */;
INSERT INTO `data_center` VALUES (1,'amazon-us-west-1a','Amazon US West 1a',10,NULL),(2,'amazon-us-west-1b','Amazon US West 1b',10,NULL),(3,'amazon-us-east-1a','Amazon US East 1a',2,NULL),(4,'amazon-us-east-1b','Amazon US East 1b',2,NULL),(5,'internal-us-west-1a','Internal US West 1a',1,NULL);
/*!40000 ALTER TABLE `data_center` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `endpoint`
--

LOCK TABLES `endpoint` WRITE;
/*!40000 ALTER TABLE `endpoint` DISABLE KEYS */;
INSERT INTO `endpoint` VALUES (1,45,1,1,NULL),(2,45,2,1,NULL),(3,46,3,1,NULL),(4,46,4,1,NULL),(5,47,5,1,NULL),(6,47,6,1,NULL),(7,48,7,1,NULL),(8,48,8,1,NULL),(9,48,9,1,NULL),(10,48,10,1,NULL),(31,49,51,1,NULL),(32,49,52,1,NULL),(33,50,53,1,NULL),(34,50,54,1,NULL),(35,51,55,1,NULL),(36,51,56,1,NULL),(37,52,57,1,NULL),(38,52,58,1,NULL),(39,52,59,1,NULL),(40,52,60,1,NULL);
/*!40000 ALTER TABLE `endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `environment`
--

LOCK TABLES `environment` WRITE;
/*!40000 ALTER TABLE `environment` DISABLE KEYS */;
INSERT INTO `environment` VALUES (1,'development','Development','Continuous Integration','Handles continuous integration builds (compile, unit test)',NULL),(2,'integration','Integration',NULL,'Integration testing environment',NULL),(3,'acceptance','Acceptance',NULL,'Acceptance testing environment',NULL),(4,'production','Production','Live','Production environment',NULL);
/*!40000 ALTER TABLE `environment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `health_status`
--

LOCK TABLES `health_status` WRITE;
/*!40000 ALTER TABLE `health_status` DISABLE KEYS */;
INSERT INTO `health_status` VALUES (1,'dead','Dead',1,NULL),(2,'deploy','Deploy',3,NULL),(3,'deployment-failed','Deployment Failed',6,NULL),(4,'deployment-canceled','Deployment Canceled',6,NULL),(5,'discovery','Discovery',2,NULL),(6,'downgrade-needed','Downgrade Needed',3,NULL),(7,'healthy','Healthy',5,NULL),(8,'manual-intervention','Manual Intervention',6,NULL),(9,'new','New',3,NULL),(10,'patch-needed','Patch Needed',3,NULL),(11,'perpetrator','Perpetrator',1,NULL),(12,'purgatory','Purgatory',1,NULL),(13,'retribution','Retribution',6,NULL),(14,'upgrade-needed','Upgrade Needed',3,NULL),(15,'victim','Victim',6,NULL),(16,'inactive','Inactive',3,NULL);
/*!40000 ALTER TABLE `health_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `infrastructure_provider`
--

LOCK TABLES `infrastructure_provider` WRITE;
/*!40000 ALTER TABLE `infrastructure_provider` DISABLE KEYS */;
INSERT INTO `infrastructure_provider` VALUES (1,'amazon','Amazon Web Services',NULL),(2,'internal','Internal Infrastructure Services',NULL);
/*!40000 ALTER TABLE `infrastructure_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ip_address_role`
--

LOCK TABLES `ip_address_role` WRITE;
/*!40000 ALTER TABLE `ip_address_role` DISABLE KEYS */;
INSERT INTO `ip_address_role` VALUES (5,5,'default','Default role',NULL),(6,6,'default','Default role',NULL),(7,7,'default','Default role',NULL),(8,8,'default','Default role',NULL),(9,9,'default','Default role',NULL),(10,10,'default','Default role',NULL),(11,11,'default','Default role',NULL),(12,12,'default','Default role',NULL),(13,13,'default','Default role',NULL),(14,14,'default','Default role',NULL),(15,15,'default','Default role',NULL),(16,16,'default','Default role',NULL),(17,17,'default','Default role',NULL),(18,18,'default','Default role',NULL),(19,19,'default','Default role',NULL),(20,20,'default','Default role',NULL),(21,21,'default','Default role',NULL),(22,22,'default','Default role',NULL),(23,23,'default','Default role',NULL),(24,24,'default','Default role',NULL),(25,25,'default','Default role',NULL),(26,26,'default','Default role',NULL),(27,27,'default','Default role',NULL),(28,28,'default','Default role',NULL),(29,29,'default','Default role',NULL),(30,30,'default','Default role',NULL),(31,31,'default','Default role',NULL),(32,32,'default','Default role',NULL),(33,33,'default','Default role',NULL),(34,34,'default','Default role',NULL),(35,35,'default','Default role',NULL),(36,36,'default','Default role',NULL),(37,37,'default','Default role',NULL),(38,38,'default','Default role',NULL),(39,39,'default','Default role',NULL),(40,40,'default','Default role',NULL),(41,41,'default','Default role',NULL),(42,42,'default','Default role',NULL),(43,43,'default','Default role',NULL),(44,44,'default','Default role',NULL),(45,45,'default','Default role',NULL),(46,46,'default','Default role',NULL),(47,47,'default','Default role',NULL),(48,48,'default','Default role',NULL),(49,49,'default','Default role',NULL),(50,50,'default','Default role',NULL),(51,51,'default','Default role',NULL),(52,52,'default','Default role',NULL);
/*!40000 ALTER TABLE `ip_address_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `load_balancer`
--

LOCK TABLES `load_balancer` WRITE;
/*!40000 ALTER TABLE `load_balancer` DISABLE KEYS */;
INSERT INTO `load_balancer` VALUES (1,5,'NS-10-10-10-10','NetScaler','10.10.10.10','https://10.10.10.10/nitro/v1',NULL),(2,5,'NS-10-10-20-10','NetScaler','10.10.20.10','https://10.10.20.10/nitro/v1',NULL),(3,5,'NS-10-10-30-10','NetScaler','10.10.30.10','https://10.10.30.10/nitro/v1',NULL),(4,5,'NS-10-10-40-10','NetScaler','10.10.40.10','https://10.10.40.10/nitro/v1',NULL);
/*!40000 ALTER TABLE `load_balancer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `machine`
--

LOCK TABLES `machine` WRITE;
/*!40000 ALTER TABLE `machine` DISABLE KEYS */;
INSERT INTO `machine` VALUES (1,'seiso001.dev.example.com','seiso001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.11','seiso001.dev.example.com',NULL,NULL),(2,'seiso002.dev.example.com','seiso002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.12','seiso002.dev.example.com',NULL,NULL),(3,'seiso001.itest.example.com','seiso001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.11','seiso001.itest.example.com',NULL,NULL),(4,'seiso002.itest.example.com','seiso002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.12','seiso001.itest.example.com',NULL,NULL),(5,'seiso001.atest.example.com','seiso001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.11','seiso001.atest.example.com',NULL,NULL),(6,'seiso002.atest.example.com','seiso001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.11','seiso001.atest.example.com',NULL,NULL),(7,'seiso001.prod.example.com','seiso001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.11','seiso001.prod.example.com',NULL,NULL),(8,'seiso002.prod.example.com','seiso002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.12','seiso001.prod.example.com',NULL,NULL),(9,'seiso003.prod.example.com','seiso003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.13','seiso001.prod.example.com',NULL,NULL),(10,'seiso004.prod.example.com','seiso004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.14','seiso001.prod.example.com',NULL,NULL),(11,'airanc001.dev.example.com','airanc001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.21','airanc001.dev.example.com',NULL,NULL),(12,'airanc002.dev.example.com','airanc002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.22','airanc002.dev.example.com',NULL,NULL),(13,'airanc001.itest.example.com','airanc001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.21','airanc001.itest.example.com',NULL,NULL),(14,'airanc002.itest.example.com','airanc002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.22','airanc001.itest.example.com',NULL,NULL),(15,'airanc001.atest.example.com','airanc001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.21','airanc001.atest.example.com',NULL,NULL),(16,'airanc002.atest.example.com','airanc001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.21','airanc001.atest.example.com',NULL,NULL),(17,'airanc001.prod.example.com','airanc001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.21','airanc001.prod.example.com',NULL,NULL),(18,'airanc002.prod.example.com','airanc002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.22','airanc001.prod.example.com',NULL,NULL),(19,'airanc003.prod.example.com','airanc003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.23','airanc001.prod.example.com',NULL,NULL),(20,'airanc004.prod.example.com','airanc004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.24','airanc001.prod.example.com',NULL,NULL),(21,'airinv001.dev.example.com','airinv001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.31','airinv001.dev.example.com',NULL,NULL),(22,'airinv002.dev.example.com','airinv002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.32','airinv002.dev.example.com',NULL,NULL),(23,'airinv001.itest.example.com','airinv001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.31','airinv001.itest.example.com',NULL,NULL),(24,'airinv002.itest.example.com','airinv002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.32','airinv001.itest.example.com',NULL,NULL),(25,'airinv001.atest.example.com','airinv001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.31','airinv001.atest.example.com',NULL,NULL),(26,'airinv002.atest.example.com','airinv001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.31','airinv001.atest.example.com',NULL,NULL),(27,'airinv001.prod.example.com','airinv001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.31','airinv001.prod.example.com',NULL,NULL),(28,'airinv002.prod.example.com','airinv002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.32','airinv001.prod.example.com',NULL,NULL),(29,'airinv003.prod.example.com','airinv003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.33','airinv001.prod.example.com',NULL,NULL),(30,'airinv004.prod.example.com','airinv004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.34','airinv001.prod.example.com',NULL,NULL);
/*!40000 ALTER TABLE `machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `node`
--

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
INSERT INTO `node` VALUES (1,'seiso001-development',NULL,'0.2.0',45,1,7,NULL),(2,'seiso002-development',NULL,'0.2.0',45,2,7,NULL),(3,'seiso001-integration',NULL,'0.2.0',46,3,7,NULL),(4,'seiso002-integration',NULL,'0.2.0',46,4,7,NULL),(5,'seiso001-acceptance',NULL,'0.2.0',47,5,7,NULL),(6,'seiso002-acceptance',NULL,'0.2.0',47,6,7,NULL),(7,'seiso001-production',NULL,'0.2.0',48,7,7,NULL),(8,'seiso002-production',NULL,'0.2.0',48,8,7,NULL),(9,'seiso003-production',NULL,'0.2.0',48,9,7,NULL),(10,'seiso004-production',NULL,'0.2.0',48,10,NULL,NULL),(21,'airinv001-development',NULL,'1.0',5,21,7,NULL),(22,'airinv002-development',NULL,'1.0',5,22,7,NULL),(23,'airinv001-integration',NULL,'1.0',6,23,7,NULL),(24,'airinv002-integration',NULL,'1.0',6,24,7,NULL),(25,'airinv001-acceptance',NULL,'1.0',7,25,7,NULL),(26,'airinv002-acceptance',NULL,'1.0',7,26,7,NULL),(27,'airinv001-production',NULL,'1.0',8,27,7,NULL),(28,'airinv002-production',NULL,'1.0',8,28,7,NULL),(29,'airinv003-production',NULL,'1.0',8,29,7,NULL),(30,'airinv004-production',NULL,'1.0',8,30,NULL,NULL),(31,'airanc001-development',NULL,'1.0',49,11,7,NULL),(32,'airanc002-development',NULL,'1.0',49,12,7,NULL),(33,'airanc001-integration',NULL,'1.0',50,13,7,NULL),(34,'airanc002-integration',NULL,'1.0',50,14,7,NULL),(35,'airanc001-acceptance',NULL,'1.0',51,15,7,NULL),(36,'airanc002-acceptance',NULL,'1.0',51,16,7,NULL),(37,'airanc001-production',NULL,'1.0',52,17,7,NULL),(38,'airanc002-production',NULL,'1.0',52,18,7,NULL),(39,'airanc003-production',NULL,'1.0',52,19,7,NULL),(40,'airanc004-production',NULL,'1.0',52,20,NULL,NULL);
/*!40000 ALTER TABLE `node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `node_ip_address`
--

LOCK TABLES `node_ip_address` WRITE;
/*!40000 ALTER TABLE `node_ip_address` DISABLE KEYS */;
INSERT INTO `node_ip_address` VALUES (1,1,45,'10.10.10.11',1,NULL),(2,2,45,'10.10.10.12',1,NULL),(3,3,46,'10.10.20.11',1,NULL),(4,4,46,'10.10.20.12',1,NULL),(5,5,47,'10.10.30.11',1,NULL),(6,6,47,'10.10.30.12',1,NULL),(7,7,48,'10.10.40.11',1,NULL),(8,8,48,'10.10.40.12',1,NULL),(9,9,48,'10.10.40.13',1,NULL),(10,10,48,'10.10.40.14',1,NULL),(31,21,5,'10.10.10.31',1,NULL),(32,22,5,'10.10.10.32',1,NULL),(33,23,6,'10.10.20.31',1,NULL),(34,24,6,'10.10.20.32',1,NULL),(35,25,7,'10.10.30.31',1,NULL),(36,26,7,'10.10.30.32',1,NULL),(37,27,8,'10.10.40.31',1,NULL),(38,28,8,'10.10.40.32',1,NULL),(39,29,8,'10.10.40.33',1,NULL),(40,30,8,'10.10.40.34',1,NULL),(51,31,49,'10.10.10.21',1,NULL),(52,32,49,'10.10.10.22',1,NULL),(53,33,50,'10.10.20.21',1,NULL),(54,34,50,'10.10.20.22',1,NULL),(55,35,51,'10.10.30.21',1,NULL),(56,36,51,'10.10.30.22',1,NULL),(57,37,52,'10.10.40.21',1,NULL),(58,38,52,'10.10.40.22',1,NULL),(59,39,52,'10.10.40.23',1,NULL),(60,40,52,'10.10.40.24',1,NULL);
/*!40000 ALTER TABLE `node_ip_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'sammy','Sammy','Seiso','Data Custodian','A Travel Co','Engineering',NULL,NULL,'Moreno Valley, CA 92557','123 G St.','951-555-1234','sammy@example.com',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,'internal-us-west-1','US West (Oregon)','na',2,NULL),(2,'amazon-us-east-1','US East (N. Virginia)','na',1,NULL),(4,'amazon-us-west-2','US West (N. California)','na',1,NULL),(5,'amazon-eu-1','Europe (Ireland)','eu',1,NULL),(6,'amazon-apac-1','Asia Pacific (Singapore)','apac',1,NULL),(7,'amazon-apac-2','Asia Pacific (Tokyo)','apac',1,NULL),(8,'amazon-apac-3','Asia Pacific (Sydney)','apac',1,NULL),(9,'amazon-sa-1','South America (Sao Paulo)','sa',1,NULL),(10,'amazon-us-west-1','US West (Oregon)','na',1,NULL);
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin'),(2,'user');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rotation_status`
--

LOCK TABLES `rotation_status` WRITE;
/*!40000 ALTER TABLE `rotation_status` DISABLE KEYS */;
INSERT INTO `rotation_status` VALUES (1,'enabled','Enabled',5,NULL),(2,'disabled','Disabled',6,NULL),(3,'excluded','Excluded',3,NULL),(4,'no-endpoints','No Endpoints',3,NULL),(5,'partial','Partial',6,NULL),(6,'unknown','Unknown',6,NULL);
/*!40000 ALTER TABLE `rotation_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,'air-inventory','Air Inventory',1,2,'Air inventory service',1,'https://github.com/example/air-inventory','Java',NULL),(2,'air-shopping','Air Shopping',1,2,'Air shopping service',1,'https://github.com/example/air-shopping','Java',NULL),(4,'car-inventory','Car Inventory',2,2,'Car inventory service',1,'https://github.com/example/car-inventory','Java',NULL),(5,'car-shopping','Car Shopping',2,2,'Car shopping service',1,'https://github.com/example/car-shopping','Java',NULL),(6,'hotel-inventory','Hotel Inventory',4,2,'Hotel inventory service',1,'https://github.com/example/hotel-inventory','Java',NULL),(7,'hotel-shopping','Hotel Shopping',4,2,'Hotel shopping service',1,'https://github.com/example/hotel-shopping','Java',NULL),(8,'package-inventory','Package Inventory',6,2,'Package inventory service',1,'https://github.com/example/package-inventory','Java',NULL),(9,'package-shopping','Package Shopping',6,2,'Package shopping service',1,'https://github.com/example/package-shopping','Java',NULL),(10,'eos','Eos',3,3,'Event and incident response orchestration system',1,'https://github.com/example/eos','.NET',NULL),(11,'splunk6','Splunk 6',3,3,'Log integration, search and visualization tool',1,'https://github.com/example/splunk6','Java',NULL),(12,'seiso','Seiso',3,3,'Devops data integration repository',1,'https://github.com/example/seiso','Java',NULL),(13,'air-ancillaries','Air Ancillaries',1,2,'Air ancillaries service',1,'https://github.com/example/air-ancillaries','Java',NULL);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_group`
--

LOCK TABLES `service_group` WRITE;
/*!40000 ALTER TABLE `service_group` DISABLE KEYS */;
INSERT INTO `service_group` VALUES (1,'air-services','Air Services',NULL,NULL),(2,'car-services','Car Services',NULL,NULL),(3,'devops','Devops',NULL,NULL),(4,'hotel-services','Hotel Services',NULL,NULL),(5,'loyalty-services','Loyalty Services',NULL,NULL),(6,'package-services','Package Services',NULL,NULL),(7,'travel-web','Travel Web',NULL,NULL);
/*!40000 ALTER TABLE `service_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_instance`
--

LOCK TABLES `service_instance` WRITE;
/*!40000 ALTER TABLE `service_instance` DISABLE KEYS */;
INSERT INTO `service_instance` VALUES (5,'air-inventory-development',1,1,5,1,1,NULL,75,50,NULL),(6,'air-inventory-integration',1,2,5,1,2,NULL,75,50,NULL),(7,'air-inventory-acceptance',1,3,5,1,3,NULL,75,50,NULL),(8,'air-inventory-production',1,4,5,1,4,NULL,75,50,NULL),(9,'air-shopping-development',2,1,5,1,1,NULL,75,50,NULL),(10,'air-shopping-integration',2,2,5,1,2,NULL,75,50,NULL),(11,'air-shopping-acceptance',2,3,5,1,3,NULL,75,50,NULL),(12,'air-shopping-production',2,4,5,1,4,NULL,75,50,NULL),(13,'car-inventory-development',4,1,5,1,1,NULL,75,50,NULL),(14,'car-inventory-integration',4,2,5,1,2,NULL,75,50,NULL),(15,'car-inventory-acceptance',4,3,5,1,3,NULL,75,50,NULL),(16,'car-inventory-production',4,4,5,1,4,NULL,75,50,NULL),(17,'car-shopping-development',5,1,5,1,1,NULL,75,50,NULL),(18,'car-shopping-integration',5,2,5,1,2,NULL,75,50,NULL),(19,'car-shopping-acceptance',5,3,5,1,3,NULL,75,50,NULL),(20,'car-shopping-production',5,4,5,1,4,NULL,75,50,NULL),(21,'hotel-inventory-development',6,1,5,1,1,NULL,75,50,NULL),(22,'hotel-inventory-integration',6,2,5,1,2,NULL,75,50,NULL),(23,'hotel-inventory-acceptance',6,3,5,1,3,NULL,75,50,NULL),(24,'hotel-inventory-production',6,4,5,1,4,NULL,75,50,NULL),(25,'hotel-shopping-development',7,1,5,1,1,NULL,75,50,NULL),(26,'hotel-shopping-integration',7,2,5,1,2,NULL,75,50,NULL),(27,'hotel-shopping-acceptance',7,3,5,1,3,NULL,75,50,NULL),(28,'hotel-shopping-production',7,4,5,1,4,NULL,75,50,NULL),(29,'package-inventory-development',8,1,5,1,1,NULL,75,50,NULL),(30,'package-inventory-integration',8,2,5,1,2,NULL,75,50,NULL),(31,'package-inventory-acceptance',8,3,5,1,3,NULL,75,50,NULL),(32,'package-inventory-production',8,4,5,1,4,NULL,75,50,NULL),(33,'package-shopping-development',9,1,5,1,1,NULL,75,50,NULL),(34,'package-shopping-integration',9,2,5,1,2,NULL,75,50,NULL),(35,'package-shopping-acceptance',9,3,5,1,3,NULL,75,50,NULL),(36,'package-shopping-production',9,4,5,1,4,NULL,75,50,NULL),(37,'eos-development',10,1,5,1,1,NULL,75,50,NULL),(38,'eos-integration',10,2,5,1,2,NULL,75,50,NULL),(39,'eos-acceptance',10,3,5,1,3,NULL,75,50,NULL),(40,'eos-production',10,4,5,1,4,NULL,75,50,NULL),(41,'splunk6-development',11,1,5,1,1,NULL,75,50,NULL),(42,'splunk6-integration',11,2,5,1,2,NULL,75,50,NULL),(43,'splunk6-acceptance',11,3,5,1,3,NULL,75,50,NULL),(44,'splunk6-production',11,4,5,1,4,NULL,75,50,NULL),(45,'seiso-development',12,1,5,1,1,NULL,75,50,NULL),(46,'seiso-integration',12,2,5,1,2,NULL,75,50,NULL),(47,'seiso-acceptance',12,3,5,1,3,NULL,75,50,NULL),(48,'seiso-production',12,4,5,1,4,NULL,75,50,NULL),(49,'air-ancillaries-development',13,1,5,1,1,NULL,75,50,NULL),(50,'air-ancillaries-integration',13,2,5,1,2,NULL,75,50,NULL),(51,'air-ancillaries-acceptance',13,3,5,1,3,NULL,75,50,NULL),(52,'air-ancillaries-production',13,4,5,1,4,NULL,75,50,NULL);
/*!40000 ALTER TABLE `service_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_instance_port`
--

LOCK TABLES `service_instance_port` WRITE;
/*!40000 ALTER TABLE `service_instance_port` DISABLE KEYS */;
INSERT INTO `service_instance_port` VALUES (5,5,8443,'https','REST API',NULL),(6,6,8443,'https','REST API',NULL),(7,7,8443,'https','REST API',NULL),(8,8,8443,'https','REST API',NULL),(9,9,8443,'https','REST API',NULL),(10,10,8443,'https','REST API',NULL),(11,11,8443,'https','REST API',NULL),(12,12,8443,'https','REST API',NULL),(13,13,8443,'https','REST API',NULL),(14,14,8443,'https','REST API',NULL),(15,15,8443,'https','REST API',NULL),(16,16,8443,'https','REST API',NULL),(17,17,8443,'https','REST API',NULL),(18,18,8443,'https','REST API',NULL),(19,19,8443,'https','REST API',NULL),(20,20,8443,'https','REST API',NULL),(21,21,8443,'https','REST API',NULL),(22,22,8443,'https','REST API',NULL),(23,23,8443,'https','REST API',NULL),(24,24,8443,'https','REST API',NULL),(25,25,8443,'https','REST API',NULL),(26,26,8443,'https','REST API',NULL),(27,27,8443,'https','REST API',NULL),(28,28,8443,'https','REST API',NULL),(29,29,8443,'https','REST API',NULL),(30,30,8443,'https','REST API',NULL),(31,31,8443,'https','REST API',NULL),(32,32,8443,'https','REST API',NULL),(33,33,8443,'https','REST API',NULL),(34,34,8443,'https','REST API',NULL),(35,35,8443,'https','REST API',NULL),(36,36,8443,'https','REST API',NULL),(37,37,8443,'https','REST API',NULL),(38,38,8443,'https','REST API',NULL),(39,39,8443,'https','REST API',NULL),(40,40,8443,'https','REST API',NULL),(41,41,8443,'https','REST API',NULL),(42,42,8443,'https','REST API',NULL),(43,43,8443,'https','REST API',NULL),(44,44,8443,'https','REST API',NULL),(45,45,8443,'https','REST API',NULL),(46,46,8443,'https','REST API',NULL),(47,47,8443,'https','REST API',NULL),(48,48,8443,'https','REST API',NULL),(49,49,8443,'https','REST API',NULL),(50,50,8443,'https','REST API',NULL),(51,51,8443,'https','REST API',NULL),(52,52,8443,'https','REST API',NULL);
/*!40000 ALTER TABLE `service_instance_port` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_type`
--

LOCK TABLES `service_type` WRITE;
/*!40000 ALTER TABLE `service_type` DISABLE KEYS */;
INSERT INTO `service_type` VALUES (1,'application','User Application',NULL),(2,'web-service','Web Service',NULL),(3,'app-web-service','User Application + Web Service',NULL),(4,'job','Job',NULL),(5,'database','Database',NULL),(6,'agent','Agent',NULL),(7,'service','Service',NULL);
/*!40000 ALTER TABLE `service_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `status_type`
--

LOCK TABLES `status_type` WRITE;
/*!40000 ALTER TABLE `status_type` DISABLE KEYS */;
INSERT INTO `status_type` VALUES (1,'danger','Danger',NULL),(2,'default','Default',NULL),(3,'info','Info',NULL),(4,'primary','Primary',NULL),(5,'success','Success',NULL),(6,'warning','Warning',NULL);
/*!40000 ALTER TABLE `status_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'seiso-admin','$2a$10$FnqVAz2UrFQnQkMxVgVNpOyQj0sFSmF0VD8zsQyG2rhd.Wji7mN9y',1),(2,'seiso-user','$2a$10$GkVLYh34PyRd15yaUrltae3gE8uXGhxZqWlKc2ix1v.2LLsibhI6e',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1),(2,2,2);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-02-01  3:58:01
