-- MySQL dump 10.13  Distrib 5.6.21, for osx10.9 (x86_64)
--
-- Host: localhost    Database: seiso_0_2_0
-- ------------------------------------------------------
-- Server version	5.6.21

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
INSERT INTO `data_center` VALUES (1,'amazon-us-west-1a','Amazon US West 1a',10),(2,'amazon-us-west-1b','Amazon US West 1b',10),(3,'amazon-us-east-1a','Amazon US East 1a',2),(4,'amazon-us-east-1b','Amazon US East 1b',2),(5,'internal-us-west-1a','Internal US West 1a',1);
/*!40000 ALTER TABLE `data_center` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `endpoint`
--

LOCK TABLES `endpoint` WRITE;
/*!40000 ALTER TABLE `endpoint` DISABLE KEYS */;
INSERT INTO `endpoint` VALUES (1,45,1,1),(2,45,2,1),(3,46,3,1),(4,46,4,1),(5,47,5,1),(6,47,6,1),(7,48,7,1),(8,48,8,1),(9,48,9,1),(10,48,10,1),(31,49,51,1),(32,49,52,1),(33,50,53,1),(34,50,54,1),(35,51,55,1),(36,51,56,1),(37,52,57,1),(38,52,58,1),(39,52,59,1),(40,52,60,1);
/*!40000 ALTER TABLE `endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `environment`
--

LOCK TABLES `environment` WRITE;
/*!40000 ALTER TABLE `environment` DISABLE KEYS */;
INSERT INTO `environment` VALUES (1,'development','Development','Continuous Integration','Handles continuous integration builds (compile, unit test)'),(2,'integration','Integration',NULL,'Integration testing environment'),(3,'acceptance','Acceptance',NULL,'Acceptance testing environment'),(4,'production','Production','Live','Production environment');
/*!40000 ALTER TABLE `environment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `health_status`
--

LOCK TABLES `health_status` WRITE;
/*!40000 ALTER TABLE `health_status` DISABLE KEYS */;
INSERT INTO `health_status` VALUES (1,'dead','Dead',1),(2,'deploy','Deploy',3),(3,'deployment-failed','Deployment Failed',6),(4,'deployment-canceled','Deployment Canceled',6),(5,'discovery','Discovery',2),(6,'downgrade-needed','Downgrade Needed',3),(7,'healthy','Healthy',5),(8,'manual-intervention','Manual Intervention',6),(9,'new','New',3),(10,'patch-needed','Patch Needed',3),(11,'perpetrator','Perpetrator',1),(12,'purgatory','Purgatory',1),(13,'retribution','Retribution',6),(14,'upgrade-needed','Upgrade Needed',3),(15,'victim','Victim',6),(16,'inactive','Inactive',3);
/*!40000 ALTER TABLE `health_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `infrastructure_provider`
--

LOCK TABLES `infrastructure_provider` WRITE;
/*!40000 ALTER TABLE `infrastructure_provider` DISABLE KEYS */;
INSERT INTO `infrastructure_provider` VALUES (1,'amazon','Amazon Web Services'),(2,'internal','Internal Infrastructure Services');
/*!40000 ALTER TABLE `infrastructure_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ip_address_role`
--

LOCK TABLES `ip_address_role` WRITE;
/*!40000 ALTER TABLE `ip_address_role` DISABLE KEYS */;
INSERT INTO `ip_address_role` VALUES (5,5,'default','Default role'),(6,6,'default','Default role'),(7,7,'default','Default role'),(8,8,'default','Default role'),(9,9,'default','Default role'),(10,10,'default','Default role'),(11,11,'default','Default role'),(12,12,'default','Default role'),(13,13,'default','Default role'),(14,14,'default','Default role'),(15,15,'default','Default role'),(16,16,'default','Default role'),(17,17,'default','Default role'),(18,18,'default','Default role'),(19,19,'default','Default role'),(20,20,'default','Default role'),(21,21,'default','Default role'),(22,22,'default','Default role'),(23,23,'default','Default role'),(24,24,'default','Default role'),(25,25,'default','Default role'),(26,26,'default','Default role'),(27,27,'default','Default role'),(28,28,'default','Default role'),(29,29,'default','Default role'),(30,30,'default','Default role'),(31,31,'default','Default role'),(32,32,'default','Default role'),(33,33,'default','Default role'),(34,34,'default','Default role'),(35,35,'default','Default role'),(36,36,'default','Default role'),(37,37,'default','Default role'),(38,38,'default','Default role'),(39,39,'default','Default role'),(40,40,'default','Default role'),(41,41,'default','Default role'),(42,42,'default','Default role'),(43,43,'default','Default role'),(44,44,'default','Default role'),(45,45,'default','Default role'),(46,46,'default','Default role'),(47,47,'default','Default role'),(48,48,'default','Default role'),(49,49,'default','Default role'),(50,50,'default','Default role'),(51,51,'default','Default role'),(52,52,'default','Default role');
/*!40000 ALTER TABLE `ip_address_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `load_balancer`
--

LOCK TABLES `load_balancer` WRITE;
/*!40000 ALTER TABLE `load_balancer` DISABLE KEYS */;
INSERT INTO `load_balancer` VALUES (1,5,'NS-10-10-10-10','NetScaler','10.10.10.10','https://10.10.10.10/nitro/v1'),(2,5,'NS-10-10-20-10','NetScaler','10.10.20.10','https://10.10.20.10/nitro/v1'),(3,5,'NS-10-10-30-10','NetScaler','10.10.30.10','https://10.10.30.10/nitro/v1'),(4,5,'NS-10-10-40-10','NetScaler','10.10.40.10','https://10.10.40.10/nitro/v1');
/*!40000 ALTER TABLE `load_balancer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `machine`
--

LOCK TABLES `machine` WRITE;
/*!40000 ALTER TABLE `machine` DISABLE KEYS */;
INSERT INTO `machine` VALUES (1,'seiso001.dev.example.com','seiso001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.11','seiso001.dev.example.com',NULL),(2,'seiso002.dev.example.com','seiso002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.12','seiso002.dev.example.com',NULL),(3,'seiso001.itest.example.com','seiso001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.11','seiso001.itest.example.com',NULL),(4,'seiso002.itest.example.com','seiso002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.12','seiso001.itest.example.com',NULL),(5,'seiso001.atest.example.com','seiso001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.11','seiso001.atest.example.com',NULL),(6,'seiso002.atest.example.com','seiso001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.11','seiso001.atest.example.com',NULL),(7,'seiso001.prod.example.com','seiso001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.11','seiso001.prod.example.com',NULL),(8,'seiso002.prod.example.com','seiso002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.12','seiso001.prod.example.com',NULL),(9,'seiso003.prod.example.com','seiso003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.13','seiso001.prod.example.com',NULL),(10,'seiso004.prod.example.com','seiso004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.14','seiso001.prod.example.com',NULL),(11,'airanc001.dev.example.com','airanc001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.21','airanc001.dev.example.com',NULL),(12,'airanc002.dev.example.com','airanc002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.22','airanc002.dev.example.com',NULL),(13,'airanc001.itest.example.com','airanc001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.21','airanc001.itest.example.com',NULL),(14,'airanc002.itest.example.com','airanc002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.22','airanc001.itest.example.com',NULL),(15,'airanc001.atest.example.com','airanc001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.21','airanc001.atest.example.com',NULL),(16,'airanc002.atest.example.com','airanc001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.21','airanc001.atest.example.com',NULL),(17,'airanc001.prod.example.com','airanc001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.21','airanc001.prod.example.com',NULL),(18,'airanc002.prod.example.com','airanc002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.22','airanc001.prod.example.com',NULL),(19,'airanc003.prod.example.com','airanc003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.23','airanc001.prod.example.com',NULL),(20,'airanc004.prod.example.com','airanc004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.24','airanc001.prod.example.com',NULL),(21,'airinv001.dev.example.com','airinv001','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.31','airinv001.dev.example.com',NULL),(22,'airinv002.dev.example.com','airinv002','dev.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.10.32','airinv002.dev.example.com',NULL),(23,'airinv001.itest.example.com','airinv001','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.31','airinv001.itest.example.com',NULL),(24,'airinv002.itest.example.com','airinv002','itest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.20.32','airinv001.itest.example.com',NULL),(25,'airinv001.atest.example.com','airinv001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.31','airinv001.atest.example.com',NULL),(26,'airinv002.atest.example.com','airinv001','atest.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.30.31','airinv001.atest.example.com',NULL),(27,'airinv001.prod.example.com','airinv001','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.31','airinv001.prod.example.com',NULL),(28,'airinv002.prod.example.com','airinv002','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.32','airinv001.prod.example.com',NULL),(29,'airinv003.prod.example.com','airinv003','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.33','airinv001.prod.example.com',NULL),(30,'airinv004.prod.example.com','airinv004','prod.example.com','linux',NULL,'VMWare',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'10.10.40.34','airinv001.prod.example.com',NULL);
/*!40000 ALTER TABLE `machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `node`
--

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
INSERT INTO `node` VALUES (1,'seiso001-development',NULL,'0.2.0',45,1,7),(2,'seiso002-development',NULL,'0.2.0',45,2,7),(3,'seiso001-integration',NULL,'0.2.0',46,3,7),(4,'seiso002-integration',NULL,'0.2.0',46,4,7),(5,'seiso001-acceptance',NULL,'0.2.0',47,5,7),(6,'seiso002-acceptance',NULL,'0.2.0',47,6,7),(7,'seiso001-production',NULL,'0.2.0',48,7,7),(8,'seiso002-production',NULL,'0.2.0',48,8,7),(9,'seiso003-production',NULL,'0.2.0',48,9,7),(10,'seiso004-production',NULL,'0.2.0',48,10,NULL),(21,'airinv001-development',NULL,'1.0',5,21,7),(22,'airinv002-development',NULL,'1.0',5,22,7),(23,'airinv001-integration',NULL,'1.0',6,23,7),(24,'airinv002-integration',NULL,'1.0',6,24,7),(25,'airinv001-acceptance',NULL,'1.0',7,25,7),(26,'airinv002-acceptance',NULL,'1.0',7,26,7),(27,'airinv001-production',NULL,'1.0',8,27,7),(28,'airinv002-production',NULL,'1.0',8,28,7),(29,'airinv003-production',NULL,'1.0',8,29,7),(30,'airinv004-production',NULL,'1.0',8,30,NULL),(31,'airanc001-development',NULL,'1.0',49,11,7),(32,'airanc002-development',NULL,'1.0',49,12,7),(33,'airanc001-integration',NULL,'1.0',50,13,7),(34,'airanc002-integration',NULL,'1.0',50,14,7),(35,'airanc001-acceptance',NULL,'1.0',51,15,7),(36,'airanc002-acceptance',NULL,'1.0',51,16,7),(37,'airanc001-production',NULL,'1.0',52,17,7),(38,'airanc002-production',NULL,'1.0',52,18,7),(39,'airanc003-production',NULL,'1.0',52,19,7),(40,'airanc004-production',NULL,'1.0',52,20,NULL);
/*!40000 ALTER TABLE `node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `node_ip_address`
--

LOCK TABLES `node_ip_address` WRITE;
/*!40000 ALTER TABLE `node_ip_address` DISABLE KEYS */;
INSERT INTO `node_ip_address` VALUES (1,1,45,'10.10.10.11',1),(2,2,45,'10.10.10.12',1),(3,3,46,'10.10.20.11',1),(4,4,46,'10.10.20.12',1),(5,5,47,'10.10.30.11',1),(6,6,47,'10.10.30.12',1),(7,7,48,'10.10.40.11',1),(8,8,48,'10.10.40.12',1),(9,9,48,'10.10.40.13',1),(10,10,48,'10.10.40.14',1),(31,21,5,'10.10.10.31',1),(32,22,5,'10.10.10.32',1),(33,23,6,'10.10.20.31',1),(34,24,6,'10.10.20.32',1),(35,25,7,'10.10.30.31',1),(36,26,7,'10.10.30.32',1),(37,27,8,'10.10.40.31',1),(38,28,8,'10.10.40.32',1),(39,29,8,'10.10.40.33',1),(40,30,8,'10.10.40.34',1),(51,31,49,'10.10.10.21',1),(52,32,49,'10.10.10.22',1),(53,33,50,'10.10.20.21',1),(54,34,50,'10.10.20.22',1),(55,35,51,'10.10.30.21',1),(56,36,51,'10.10.30.22',1),(57,37,52,'10.10.40.21',1),(58,38,52,'10.10.40.22',1),(59,39,52,'10.10.40.23',1),(60,40,52,'10.10.40.24',1);
/*!40000 ALTER TABLE `node_ip_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'sammy','Sammy','Seiso','Data Custodian','A Travel Co','Engineering',NULL,NULL,'Moreno Valley, CA 92557','123 G St.','951-555-1234','sammy@example.com',NULL,NULL,NULL);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,'internal-us-west-1','US West (Oregon)','na',2),(2,'amazon-us-east-1','US East (N. Virginia)','na',1),(4,'amazon-us-west-2','US West (N. California)','na',1),(5,'amazon-eu-1','Europe (Ireland)','eu',1),(6,'amazon-apac-1','Asia Pacific (Singapore)','apac',1),(7,'amazon-apac-2','Asia Pacific (Tokyo)','apac',1),(8,'amazon-apac-3','Asia Pacific (Sydney)','apac',1),(9,'amazon-sa-1','South America (Sao Paulo)','sa',1),(10,'amazon-us-west-1','US West (Oregon)','na',1);
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,'air-inventory','Air Inventory',1,2,'Air inventory service',1,'https://github.com/example/air-inventory','Java'),(2,'air-shopping','Air Shopping',1,2,'Air shopping service',1,'https://github.com/example/air-shopping','Java'),(4,'car-inventory','Car Inventory',2,2,'Car inventory service',1,'https://github.com/example/car-inventory','Java'),(5,'car-shopping','Car Shopping',2,2,'Car shopping service',1,'https://github.com/example/car-shopping','Java'),(6,'hotel-inventory','Hotel Inventory',4,2,'Hotel inventory service',1,'https://github.com/example/hotel-inventory','Java'),(7,'hotel-shopping','Hotel Shopping',4,2,'Hotel shopping service',1,'https://github.com/example/hotel-shopping','Java'),(8,'package-inventory','Package Inventory',6,2,'Package inventory service',1,'https://github.com/example/package-inventory','Java'),(9,'package-shopping','Package Shopping',6,2,'Package shopping service',1,'https://github.com/example/package-shopping','Java'),(10,'eos','Eos',3,3,'Event and incident response orchestration system',1,'https://github.com/example/eos','.NET'),(11,'splunk6','Splunk 6',3,3,'Log integration, search and visualization tool',1,'https://github.com/example/splunk6','Java'),(12,'seiso','Seiso',3,3,'Devops data integration repository',1,'https://github.com/example/seiso','Java'),(13,'air-ancillaries','Air Ancillaries',1,2,'Air ancillaries service',1,'https://github.com/example/air-ancillaries','Java');
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_group`
--

LOCK TABLES `service_group` WRITE;
/*!40000 ALTER TABLE `service_group` DISABLE KEYS */;
INSERT INTO `service_group` VALUES (1,'air-services','Air Services',NULL),(2,'car-services','Car Services',NULL),(3,'devops','Devops',NULL),(4,'hotel-services','Hotel Services',NULL),(5,'loyalty-services','Loyalty Services',NULL),(6,'package-services','Package Services',NULL),(7,'travel-web','Travel Web',NULL);
/*!40000 ALTER TABLE `service_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_instance`
--

LOCK TABLES `service_instance` WRITE;
/*!40000 ALTER TABLE `service_instance` DISABLE KEYS */;
INSERT INTO `service_instance` VALUES (5,'air-inventory-development',1,1,5,1,1,NULL,75,50),(6,'air-inventory-integration',1,2,5,1,2,NULL,75,50),(7,'air-inventory-acceptance',1,3,5,1,3,NULL,75,50),(8,'air-inventory-production',1,4,5,1,4,NULL,75,50),(9,'air-shopping-development',2,1,5,1,1,NULL,75,50),(10,'air-shopping-integration',2,2,5,1,2,NULL,75,50),(11,'air-shopping-acceptance',2,3,5,1,3,NULL,75,50),(12,'air-shopping-production',2,4,5,1,4,NULL,75,50),(13,'car-inventory-development',4,1,5,1,1,NULL,75,50),(14,'car-inventory-integration',4,2,5,1,2,NULL,75,50),(15,'car-inventory-acceptance',4,3,5,1,3,NULL,75,50),(16,'car-inventory-production',4,4,5,1,4,NULL,75,50),(17,'car-shopping-development',5,1,5,1,1,NULL,75,50),(18,'car-shopping-integration',5,2,5,1,2,NULL,75,50),(19,'car-shopping-acceptance',5,3,5,1,3,NULL,75,50),(20,'car-shopping-production',5,4,5,1,4,NULL,75,50),(21,'hotel-inventory-development',6,1,5,1,1,NULL,75,50),(22,'hotel-inventory-integration',6,2,5,1,2,NULL,75,50),(23,'hotel-inventory-acceptance',6,3,5,1,3,NULL,75,50),(24,'hotel-inventory-production',6,4,5,1,4,NULL,75,50),(25,'hotel-shopping-development',7,1,5,1,1,NULL,75,50),(26,'hotel-shopping-integration',7,2,5,1,2,NULL,75,50),(27,'hotel-shopping-acceptance',7,3,5,1,3,NULL,75,50),(28,'hotel-shopping-production',7,4,5,1,4,NULL,75,50),(29,'package-inventory-development',8,1,5,1,1,NULL,75,50),(30,'package-inventory-integration',8,2,5,1,2,NULL,75,50),(31,'package-inventory-acceptance',8,3,5,1,3,NULL,75,50),(32,'package-inventory-production',8,4,5,1,4,NULL,75,50),(33,'package-shopping-development',9,1,5,1,1,NULL,75,50),(34,'package-shopping-integration',9,2,5,1,2,NULL,75,50),(35,'package-shopping-acceptance',9,3,5,1,3,NULL,75,50),(36,'package-shopping-production',9,4,5,1,4,NULL,75,50),(37,'eos-development',10,1,5,1,1,NULL,75,50),(38,'eos-integration',10,2,5,1,2,NULL,75,50),(39,'eos-acceptance',10,3,5,1,3,NULL,75,50),(40,'eos-production',10,4,5,1,4,NULL,75,50),(41,'splunk6-development',11,1,5,1,1,NULL,75,50),(42,'splunk6-integration',11,2,5,1,2,NULL,75,50),(43,'splunk6-acceptance',11,3,5,1,3,NULL,75,50),(44,'splunk6-production',11,4,5,1,4,NULL,75,50),(45,'seiso-development',12,1,5,1,1,NULL,75,50),(46,'seiso-integration',12,2,5,1,2,NULL,75,50),(47,'seiso-acceptance',12,3,5,1,3,NULL,75,50),(48,'seiso-production',12,4,5,1,4,NULL,75,50),(49,'air-ancillaries-development',13,1,5,1,1,NULL,75,50),(50,'air-ancillaries-integration',13,2,5,1,2,NULL,75,50),(51,'air-ancillaries-acceptance',13,3,5,1,3,NULL,75,50),(52,'air-ancillaries-production',13,4,5,1,4,NULL,75,50);
/*!40000 ALTER TABLE `service_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_instance_port`
--

LOCK TABLES `service_instance_port` WRITE;
/*!40000 ALTER TABLE `service_instance_port` DISABLE KEYS */;
INSERT INTO `service_instance_port` VALUES (5,5,8443,'https','REST API'),(6,6,8443,'https','REST API'),(7,7,8443,'https','REST API'),(8,8,8443,'https','REST API'),(9,9,8443,'https','REST API'),(10,10,8443,'https','REST API'),(11,11,8443,'https','REST API'),(12,12,8443,'https','REST API'),(13,13,8443,'https','REST API'),(14,14,8443,'https','REST API'),(15,15,8443,'https','REST API'),(16,16,8443,'https','REST API'),(17,17,8443,'https','REST API'),(18,18,8443,'https','REST API'),(19,19,8443,'https','REST API'),(20,20,8443,'https','REST API'),(21,21,8443,'https','REST API'),(22,22,8443,'https','REST API'),(23,23,8443,'https','REST API'),(24,24,8443,'https','REST API'),(25,25,8443,'https','REST API'),(26,26,8443,'https','REST API'),(27,27,8443,'https','REST API'),(28,28,8443,'https','REST API'),(29,29,8443,'https','REST API'),(30,30,8443,'https','REST API'),(31,31,8443,'https','REST API'),(32,32,8443,'https','REST API'),(33,33,8443,'https','REST API'),(34,34,8443,'https','REST API'),(35,35,8443,'https','REST API'),(36,36,8443,'https','REST API'),(37,37,8443,'https','REST API'),(38,38,8443,'https','REST API'),(39,39,8443,'https','REST API'),(40,40,8443,'https','REST API'),(41,41,8443,'https','REST API'),(42,42,8443,'https','REST API'),(43,43,8443,'https','REST API'),(44,44,8443,'https','REST API'),(45,45,8443,'https','REST API'),(46,46,8443,'https','REST API'),(47,47,8443,'https','REST API'),(48,48,8443,'https','REST API'),(49,49,8443,'https','REST API'),(50,50,8443,'https','REST API'),(51,51,8443,'https','REST API'),(52,52,8443,'https','REST API');
/*!40000 ALTER TABLE `service_instance_port` ENABLE KEYS */;
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

-- Dump completed on 2014-11-19  4:20:18
