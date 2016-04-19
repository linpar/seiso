INSERT into `status_type` (id, ukey, name) values (7, 'UNKNOWN', 'UNKNOWN');

INSERT into `health_status` (id, ukey, name, status_type_id) values (4, 'UNKNOWN', 'UNKNOWN', 7)
ALTER TABLE `node` MODIFY COLUMN `health_status_id` tinyint(3) unsigned DEFAULT 4;

ALTER TABLE `node_ip_address` MODIFY COLUMN `rotation_status_id` tinyint(3) unsigned DEFAULT 6;
ALTER TABLE `endpoint` MODIFY COLUMN `rotation_status_id` tinyint(3) unsigned DEFAULT 6;
ALTER TABLE `machine` MODIFY COLUMN `rotation_status_id` tinyint(3) unsigned DEFAULT 6;
ALTER TABLE `node` MODIFY COLUMN `rotation_status_id` tinyint(3) unsigned DEFAULT 6;

ALTER TABLE `node` MODIFY COLUMN `aggregate_rotation_status_id` tinyint(3) unsigned DEFAULT 6;
ALTER TABLE `node_ip_address` MODIFY COLUMN `aggregate_rotation_status_id` tinyint(3) unsigned DEFAULT 6;
