SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `data_center` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `region_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `data_center_region_id` (`region_id`),
  CONSTRAINT `data_center_region_id` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `endpoint` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_port_id` int(10) unsigned NOT NULL,
  `node_ip_address_id` int(10) unsigned DEFAULT NULL,
  `rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nip_id_sip_id` (`node_ip_address_id`,`service_instance_port_id`),
  KEY `service_instance_port_id` (`service_instance_port_id`),
  KEY `rotation_status_id` (`rotation_status_id`),
  CONSTRAINT `endpoint_node_ip_address_id` FOREIGN KEY (`node_ip_address_id`) REFERENCES `node_ip_address` (`id`),
  CONSTRAINT `endpoint_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`),
  CONSTRAINT `endpoint_service_instance_port_id` FOREIGN KEY (`service_instance_port_id`) REFERENCES `service_instance_port` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `environment` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `aka` varchar(250) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `health_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  `status_type_id` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `status_type_id` (`status_type_id`),
  CONSTRAINT `health_status_status_type` FOREIGN KEY (`status_type_id`) REFERENCES `status_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `infrastructure_provider` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `ip_address_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_instance_id_name` (`service_instance_id`,`name`),
  CONSTRAINT `ip_address_role_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `load_balancer` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `data_center_id` smallint(5) unsigned DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  `type` varchar(80) NOT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `api_url` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `data_center_id` (`data_center_id`),
  CONSTRAINT `load_balancer_data_center_id` FOREIGN KEY (`data_center_id`) REFERENCES `data_center` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `rotation_status_id` (`rotation_status_id`),
  CONSTRAINT `machine_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `node` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `version` varchar(128) DEFAULT NULL,
  `service_instance_id` int(10) unsigned DEFAULT NULL,
  `machine_id` int(10) unsigned DEFAULT NULL,
  `health_status_id` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `service_instance_id` (`service_instance_id`),
  KEY `machine_id` (`machine_id`),
  KEY `health_status_id` (`health_status_id`),
  CONSTRAINT `node_health_status_id` FOREIGN KEY (`health_status_id`) REFERENCES `health_status` (`id`),
  CONSTRAINT `node_machine_id` FOREIGN KEY (`machine_id`) REFERENCES `machine` (`id`),
  CONSTRAINT `node_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `node_ip_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `node_id` int(10) unsigned NOT NULL,
  `ip_address_role_id` int(10) unsigned NOT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `rotation_status_id` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `node_id_ip_address_role_id` (`node_id`,`ip_address_role_id`),
  KEY `rotation_status_id` (`rotation_status_id`),
  KEY `ip_address_role_id` (`ip_address_role_id`),
  CONSTRAINT `node_ip_address_ip_address_role_id` FOREIGN KEY (`ip_address_role_id`) REFERENCES `ip_address_role` (`id`),
  CONSTRAINT `node_ip_address_node_id` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`),
  CONSTRAINT `node_ip_address_rotation_status_id` FOREIGN KEY (`rotation_status_id`) REFERENCES `rotation_status` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `person` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `first_name` varchar(40) DEFAULT NULL,
  `last_name` varchar(40) DEFAULT NULL,
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
  `mingle_user_id` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `manager_id` (`manager_id`),
  KEY `first_name_last_name` (`first_name`,`last_name`),
  KEY `last_name_first_name` (`last_name`,`first_name`),
  CONSTRAINT `person_manager_id` FOREIGN KEY (`manager_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

-- Region names aren't necessarily unique, since two providers can have the same region.
CREATE TABLE `region` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  `region_key` char(4) DEFAULT NULL,
  `provider_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `provider_id` (`provider_id`),
  CONSTRAINT `region_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `infrastructure_provider` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `role` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `rotation_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  `status_type_id` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`),
  KEY `status_type_id` (`status_type_id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `service` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `group_id` smallint(5) unsigned DEFAULT NULL,
  `type_id` tinyint(3) unsigned DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `owner_id` smallint(5) unsigned DEFAULT NULL,
  `scm_repository` varchar(250) DEFAULT NULL,
  `platform` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `group_id` (`group_id`),
  KEY `owner_id` (`owner_id`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `service_group_id` FOREIGN KEY (`group_id`) REFERENCES `service_group` (`id`),
  CONSTRAINT `service_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `person` (`id`),
  CONSTRAINT `service_type_id` FOREIGN KEY (`type_id`) REFERENCES `service_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `service_group` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `service_instance` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `service_id` int(10) unsigned NOT NULL,
  `environment_id` smallint(5) unsigned NOT NULL,
  `data_center_id` smallint(5) unsigned DEFAULT NULL,
  `load_balanced` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `load_balancer_id` smallint(5) unsigned DEFAULT NULL,
  `eos_managed` tinyint(1) unsigned DEFAULT '0',
  `min_capacity_deploy` smallint(5) unsigned DEFAULT NULL,
  `min_capacity_ops` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  KEY `service_id` (`service_id`),
  KEY `environment_id` (`environment_id`),
  KEY `load_balancer_id` (`load_balancer_id`),
  CONSTRAINT `service_instance_environment_id` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `service_instance_load_balancer_id` FOREIGN KEY (`load_balancer_id`) REFERENCES `load_balancer` (`id`),
  CONSTRAINT `service_instance_service_id` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `service_instance_port` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `service_instance_id` int(10) unsigned NOT NULL,
  `number` smallint(5) unsigned NOT NULL,
  `protocol` varchar(40) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_service_instance_port_1` (`service_instance_id`,`number`),
  KEY `service_instance_id` (`service_instance_id`),
  CONSTRAINT `sip_service_instance_id` FOREIGN KEY (`service_instance_id`) REFERENCES `service_instance` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `service_type` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(40) NOT NULL,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `status_type` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `ukey` varchar(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey` (`ukey`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `user` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `password` varchar(64) NOT NULL,
  `enabled` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_username` (`username`)
) ENGINE=InnoDB CHARSET=utf8;

CREATE TABLE `user_role` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` smallint(5) unsigned NOT NULL,
  `role_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_role_id` (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1;
