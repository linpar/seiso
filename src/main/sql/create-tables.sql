-- MySQL dump 10.13  Distrib 5.6.26, for osx10.10 (x86_64)
--
-- Host: 127.0.0.1    Database: seiso
-- ------------------------------------------------------
-- Server version	5.6.26

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
-- Table structure for table `conf_prop`
--

DROP TABLE IF EXISTS `conf_prop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conf_prop` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pkey` varchar(80) NOT NULL,
  `pvalue` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pkey` (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dashboard`
--

DROP TABLE IF EXISTS `dashboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dashboard` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(80) NOT NULL,
  `name` varchar(250) NOT NULL,
  `type` varchar(80) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `ui_uri` varchar(255) DEFAULT NULL,
  `api_uri` varchar(255) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `dashboard_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_center`
--

DROP TABLE IF EXISTS `data_center`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_center` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `region_id` smallint(5) unsigned NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `data_center_region_id` (`region_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `data_center_region_id` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`),
  CONSTRAINT `data_center_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_link`
--

DROP TABLE IF EXISTS `doc_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_link` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_id` int(10) unsigned NOT NULL,
  `title` varchar(250) NOT NULL,
  `href` varchar(250) NOT NULL,
  `description` varchar(250) NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_id_title` (`service_id`,`title`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `doc_link_service_id` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `doc_link_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `endpoint`
--

DROP TABLE IF EXISTS `endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `endpoint` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_port_id` int(10) unsigned NOT NULL,
  `node_ip_address_id` int(10) unsigned NOT NULL,
  `rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nip_id_sip_id` (`node_ip_address_id`,`service_instance_port_id`),
  KEY `service_instance_port_id` (`service_instance_port_id`),
  KEY `rotation_status_id` (`rotation_status_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `endpoint_node_ip_address_id` FOREIGN KEY (`node_ip_address_id`) REFERENCES `node_ip_address` (`id`),
  CONSTRAINT `endpoint_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `endpoint_service_instance_port_id` FOREIGN KEY (`service_instance_port_id`) REFERENCES `service_instance_port` (`id`),
  CONSTRAINT `endpoint_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `environment`
--

DROP TABLE IF EXISTS `environment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `environment` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `aka` varchar(250) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `environment_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_member`
--

DROP TABLE IF EXISTS `group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` smallint(5) unsigned NOT NULL,
  `person_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_id_person_id` (`group_id`,`person_id`),
  KEY `person_id` (`person_id`),
  CONSTRAINT `group_member_group_id` FOREIGN KEY (`group_id`) REFERENCES `person_group` (`id`),
  CONSTRAINT `group_member_person_id` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_owner`
--

DROP TABLE IF EXISTS `group_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_owner` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` smallint(5) unsigned NOT NULL,
  `person_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_id_person_id` (`group_id`,`person_id`),
  KEY `person_id` (`person_id`),
  CONSTRAINT `group_owner_group_id` FOREIGN KEY (`group_id`) REFERENCES `person_group` (`id`),
  CONSTRAINT `group_owner_person_id` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `health_status`
--

DROP TABLE IF EXISTS `health_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `health_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `status_type_id` tinyint(3) unsigned NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `status_type_id` (`status_type_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `health_status_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `health_status_status_type` FOREIGN KEY (`status_type_id`) REFERENCES `status_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infrastructure_provider`
--

DROP TABLE IF EXISTS `infrastructure_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `infrastructure_provider` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `infrastructure_provider_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ip_address_role`
--

DROP TABLE IF EXISTS `ip_address_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_address_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_instance_id_name` (`service_instance_id`,`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `ip_address_role_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`),
  CONSTRAINT `ip_address_role_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `load_balancer`
--

DROP TABLE IF EXISTS `load_balancer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_balancer` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `data_center_id` smallint(5) unsigned DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  `type` varchar(80) NOT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `api_url` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `data_center_id` (`data_center_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `load_balancer_data_center_id` FOREIGN KEY (`data_center_id`) REFERENCES `data_center` (`id`),
  CONSTRAINT `load_balancer_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `machine`
--

DROP TABLE IF EXISTS `machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `machine` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `hostname` varchar(250) DEFAULT NULL,
  `domain` varchar(250) DEFAULT NULL,
  `os` varchar(80) DEFAULT NULL,
  `os_version` varchar(80) DEFAULT NULL,
  `platform` varchar(80) DEFAULT NULL,
  `platform_version` varchar(80) DEFAULT NULL,
  `ip6_address` varchar(80) DEFAULT NULL,
  `mac_address` varchar(80) DEFAULT NULL,
  `native_machine_id` varchar(250) DEFAULT NULL,
  `machine_type` varchar(250) DEFAULT NULL,
  `data_center_id` smallint(5) unsigned DEFAULT NULL,
  `chef_role` varchar(250) DEFAULT NULL,
  `virtual_system` varchar(250) DEFAULT NULL,
  `virtual_role` varchar(250) DEFAULT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `fqdn` varchar(250) DEFAULT NULL,
  `rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `rotation_status_id` (`rotation_status_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `machine_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `machine_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `node`
--

DROP TABLE IF EXISTS `node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `version` varchar(128) DEFAULT NULL,
  `service_instance_id` int(10) unsigned NOT NULL,
  `machine_id` int(10) unsigned DEFAULT NULL,
  `health_status_id` tinyint(3) unsigned DEFAULT NULL,
  `aggregate_rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `service_instance_id` (`service_instance_id`),
  KEY `machine_id` (`machine_id`),
  KEY `source_id` (`source_id`),
  KEY `aggregate_rotation_status_id` (`aggregate_rotation_status_id`),
  KEY `health_status_id` (`health_status_id`),
  CONSTRAINT `node_aggregate_rotation_status_id` FOREIGN KEY (`aggregate_rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `node_health_status_id` FOREIGN KEY (`health_status_id`) REFERENCES `health_status` (`id`),
  CONSTRAINT `node_machine_id` FOREIGN KEY (`machine_id`) REFERENCES `machine` (`id`),
  CONSTRAINT `node_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`),
  CONSTRAINT `node_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `node_ip_address`
--

DROP TABLE IF EXISTS `node_ip_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node_ip_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `node_id` int(10) unsigned NOT NULL,
  `ip_address_role_id` int(10) unsigned NOT NULL,
  `ip_address` varchar(20) NOT NULL,
  `rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  `aggregate_rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `node_id_ip_address_role_id` (`node_id`,`ip_address_role_id`),
  KEY `rotation_status_id` (`rotation_status_id`),
  KEY `ip_address_role_id` (`ip_address_role_id`),
  KEY `source_id` (`source_id`),
  KEY `aggregate_rotation_status_id` (`aggregate_rotation_status_id`),
  CONSTRAINT `node_ip_address_aggregate_rotation_status_id` FOREIGN KEY (`aggregate_rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `node_ip_address_ip_address_role_id` FOREIGN KEY (`ip_address_role_id`) REFERENCES `ip_address_role` (`id`),
  CONSTRAINT `node_ip_address_node_id` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`),
  CONSTRAINT `node_ip_address_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `node_ip_address_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `first_name` varchar(40) DEFAULT NULL,
  `last_name` varchar(40) DEFAULT NULL,
  `display_name` varchar(160) DEFAULT NULL,
  `title` varchar(80) DEFAULT NULL,
  `company` varchar(80) DEFAULT NULL,
  `department` varchar(80) DEFAULT NULL,
  `division` varchar(80) DEFAULT NULL,
  `subdivision` varchar(80) DEFAULT NULL,
  `location` varchar(80) DEFAULT NULL,
  `street_address` varchar(160) DEFAULT NULL,
  `work_phone` varchar(40) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `manager_id` smallint(5) unsigned DEFAULT NULL,
  `ldap_dn` varchar(240) DEFAULT NULL,
  `mb_type` char(4) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `manager_id` (`manager_id`),
  KEY `first_name_last_name` (`first_name`,`last_name`),
  KEY `last_name_first_name` (`last_name`,`first_name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `person_manager_id` FOREIGN KEY (`manager_id`) REFERENCES `person` (`id`),
  CONSTRAINT `person_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_group`
--

DROP TABLE IF EXISTS `person_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_group` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `alias` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `region_key` char(4) DEFAULT NULL,
  `provider_id` smallint(5) unsigned NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `provider_id` (`provider_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `region_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `infrastructure_provider` (`id`),
  CONSTRAINT `region_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `role_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rotation_status`
--

DROP TABLE IF EXISTS `rotation_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rotation_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `status_type_id` tinyint(3) unsigned NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `status_type_id` (`status_type_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `rotation_status_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(200) NOT NULL,
  `group_id` smallint(5) unsigned DEFAULT NULL,
  `type_id` tinyint(3) unsigned DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `owner_id` smallint(5) unsigned DEFAULT NULL,
  `scm_repository` varchar(250) DEFAULT NULL,
  `platform` varchar(80) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `group_id` (`group_id`),
  KEY `owner_id` (`owner_id`),
  KEY `type_id` (`type_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `service_group_id` FOREIGN KEY (`group_id`) REFERENCES `service_group` (`id`),
  CONSTRAINT `service_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `person` (`id`),
  CONSTRAINT `service_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `service_type_id` FOREIGN KEY (`type_id`) REFERENCES `service_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_group`
--

DROP TABLE IF EXISTS `service_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_group` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `service_group_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_instance`
--

DROP TABLE IF EXISTS `service_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_instance` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `service_id` int(10) unsigned NOT NULL,
  `environment_id` smallint(5) unsigned NOT NULL,
  `data_center_id` smallint(5) unsigned DEFAULT NULL,
  `load_balanced` tinyint(1) unsigned DEFAULT NULL,
  `load_balancer_id` smallint(5) unsigned DEFAULT NULL,
  `eos_managed` tinyint(1) unsigned DEFAULT '0',
  `min_capacity_deploy` smallint(5) unsigned DEFAULT NULL,
  `min_capacity_ops` smallint(5) unsigned DEFAULT NULL,
  `enable_seyren` tinyint(1) unsigned DEFAULT '0',
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `service_id` (`service_id`),
  KEY `environment_id` (`environment_id`),
  KEY `load_balancer_id` (`load_balancer_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `service_instance_environment_id` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `service_instance_load_balancer_id` FOREIGN KEY (`load_balancer_id`) REFERENCES `load_balancer` (`id`),
  CONSTRAINT `service_instance_service_id` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `service_instance_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_instance_dashboard`
--

DROP TABLE IF EXISTS `service_instance_dashboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_instance_dashboard` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `dashboard_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `service_instance_id` (`service_instance_id`),
  KEY `dashbaord_id` (`dashboard_id`),
  CONSTRAINT `sid_dashboard_id` FOREIGN KEY (`dashboard_id`) REFERENCES `dashboard` (`id`),
  CONSTRAINT `sid_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_instance_dependency`
--

DROP TABLE IF EXISTS `service_instance_dependency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_instance_dependency` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dependent_id` int(10) unsigned NOT NULL,
  `dependency_id` int(10) unsigned NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dependent_id_dependency_id` (`dependent_id`,`dependency_id`),
  KEY `dependent_id` (`dependent_id`),
  KEY `dependency_id` (`dependency_id`),
  CONSTRAINT `service_instance_dependency_dependency_id` FOREIGN KEY (`dependency_id`) REFERENCES `service_instance` (`id`),
  CONSTRAINT `service_instance_dependency_dependent_id` FOREIGN KEY (`dependent_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_instance_port`
--

DROP TABLE IF EXISTS `service_instance_port`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_instance_port` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `number` smallint(5) unsigned NOT NULL,
  `protocol` varchar(40) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_service_instance_port_1` (`service_instance_id`,`number`),
  KEY `service_instance_id` (`service_instance_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `service_instance_port_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `sip_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_instance_seyren_check`
--

DROP TABLE IF EXISTS `service_instance_seyren_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_instance_seyren_check` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `seyren_check_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `service_instance_id` (`service_instance_id`),
  KEY `seyren_check_id` (`seyren_check_id`),
  CONSTRAINT `sisc_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`),
  CONSTRAINT `sisc_seyren_check_id` FOREIGN KEY (`seyren_check_id`) REFERENCES `seyren_check` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_type`
--

DROP TABLE IF EXISTS `service_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_type` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `service_type_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seyren_check`
--

DROP TABLE IF EXISTS `seyren_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seyren_check` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `seyren_base_url` varchar(250) NOT NULL,
  `seyren_id` varchar(40) NOT NULL,
  `name` varchar(250) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `graphite_base_url` varchar(250) NOT NULL,
  `target` varchar(1000) NOT NULL,
  `warn` bigint(20) NOT NULL,
  `error` bigint(20) NOT NULL,
  `enabled` tinyint(1) unsigned NOT NULL,
  `state` varchar(20) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `seyren_base_url_seyren_id` (`seyren_base_url`,`seyren_id`),
  KEY `data_source_id` (`source_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `seyren_check_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `source`
--

DROP TABLE IF EXISTS `source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `source` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(80) NOT NULL,
  `base_uri` varchar(250) NOT NULL,
  `source_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `base_uri` (`base_uri`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `source_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status_type`
--

DROP TABLE IF EXISTS `status_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status_type` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `status_type_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `password` varchar(64) NOT NULL,
  `enabled` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `user_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` smallint(5) unsigned NOT NULL,
  `role_id` smallint(5) unsigned NOT NULL,
  `source_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_role_id` (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  KEY `source_id` (`source_id`),
  CONSTRAINT `user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `user_role_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-21 13:08:13
