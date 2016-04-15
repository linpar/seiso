DROP TABLE IF EXISTS `membership`;

CREATE TABLE `membership` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `person_id` smallint(5) unsigned NOT NULL,
  `group_id` smallint(5) unsigned NOT NULL,
  `role` VARCHAR(80),
  PRIMARY KEY (`id`),
  CONSTRAINT `membership_person_id` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
  CONSTRAINT `membership_group_id` FOREIGN KEY (`group_id`) REFERENCES `person_group` (`id`)
);