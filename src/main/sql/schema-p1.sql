ALTER TABLE service MODIFY COLUMN ukey varchar(40) NOT NULL;

ALTER TABLE node_ip_address MODIFY COLUMN ip_address varchar(20) NOT NULL;

ALTER TABLE person DROP COLUMN mingle_user_id;

DROP TABLE IF EXISTS `doc_link`;
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

-- Issue #94: NOT NULL constraint on node.service_instance_id
alter table node drop foreign key node_service_instance_id;
alter table node modify column service_instance_id int(10) unsigned not null;
alter table node add constraint node_service_instance_id foreign key (service_instance_id) references service_instance (id);

-- Issue #104: Message of the Day
drop table if exists conf_prop;
create table conf_prop (
  id int(10) unsigned not null auto_increment,
  `pkey` varchar(80) not null,
  `pvalue` text(4096),
  primary key (id),
  unique key `pkey` (`pkey`)
) engine=InnoDB default charset=utf8;

-- Issue #107: Add not null constraint to endpoint.node_ip_address_id
alter table endpoint drop foreign key endpoint_node_ip_address_id;
alter table endpoint modify column node_ip_address_id int unsigned not null;
alter table endpoint add constraint endpoint_node_ip_address_id foreign key (node_ip_address_id) references node_ip_address (id);



-- =====================================================================================================================
-- Status changes
-- =====================================================================================================================

-- Support node alerts, and really just make it easier to query for aggregate rotation status. Instead of computing this
-- dynamically on read, we compute it dynamically at write, store the result in the database, and now we can more simply
-- query for it. Think we have to do something like this to support node alert pagination, because otherwise we would
-- have to embed the complicated aggregation logic in the JPQL or the SQL.
-- 
-- Hm, but I don't want clients setting this stuff. Well I guess they already can't do it? Maybe have a read-only
-- annotation?
-- http://stackoverflow.com/questions/4939985/restful-api-design-should-unchangable-data-in-an-update-put-be-optional
-- http://stackoverflow.com/questions/28322376/exclude-some-fields-of-spring-data-rest-resource
-- http://stackoverflow.com/questions/16019834/ignoring-property-when-deserializing

-- Force an explicit "Unknown" status since we need to render this visually in a certain way, and we don't want to have
-- to hardcode that all over the UI.

-- 1 = unknown health status
-- 6 = unknown rotation status
update node set health_status_id = 1 where health_status_id is null;
update endpoint set rotation_status_id = 6 where rotation_status_id is null;
update node_ip_address set rotation_status_id = 6 where rotation_status_id is null;

alter table endpoint drop foreign key endpoint_rotation_status_id;
alter table endpoint modify column rotation_status_id tinyint unsigned not null;
alter table endpoint add constraint endpoint_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address drop foreign key node_ip_address_rotation_status_id;
alter table node_ip_address modify column rotation_status_id tinyint unsigned not null;
alter table node_ip_address add constraint node_ip_address_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address add column aggregate_rotation_status_id tinyint unsigned not null after rotation_status_id;
alter table node_ip_address add key aggregate_rotation_status_id (aggregate_rotation_status_id);
update node_ip_address set aggregate_rotation_status_id = 6;
alter table node_ip_address add constraint node_ip_address_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node add column aggregate_rotation_status_id tinyint unsigned not null after health_status_id;
alter table node add key aggregate_rotation_status_id (aggregate_rotation_status_id);
update node set aggregate_rotation_status_id = 6;
alter table node add constraint node_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node drop foreign key node_health_status_id;
alter table node modify column health_status_id tinyint unsigned not null;
alter table node add constraint node_health_status_id foreign key (health_status_id) references health_status (id);


-- =====================================================================================================================
-- ADD NOT NULL CONSTRAINTS
-- =====================================================================================================================

alter table endpoint drop foreign key endpoint_rotation_status_id;
alter table endpoint modify column rotation_status_id tinyint unsigned not null;
alter table endpoint add constraint endpoint_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address drop foreign key node_ip_address_rotation_status_id;
alter table node_ip_address modify column rotation_status_id tinyint unsigned not null;
alter table node_ip_address add constraint node_ip_address_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address drop foreign key node_ip_address_aggregate_rotation_status_id;
alter table node_ip_address modify column aggregate_rotation_status_id tinyint unsigned not null;
alter table node_ip_address add constraint node_ip_address_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node drop foreign key node_aggregate_rotation_status_id;
alter table node modify column aggregate_rotation_status_id tinyint unsigned not null;
alter table node add constraint node_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node drop foreign key node_health_status_id;
alter table node modify column health_status_id tinyint unsigned not null;
alter table node add constraint node_health_status_id foreign key (health_status_id) references health_status (id);


-- =====================================================================================================================
-- ROLLBACK
-- =====================================================================================================================

alter table endpoint drop foreign key endpoint_rotation_status_id;
alter table endpoint modify column rotation_status_id tinyint unsigned;
alter table endpoint add constraint endpoint_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address drop foreign key node_ip_address_rotation_status_id;
alter table node_ip_address modify column rotation_status_id tinyint unsigned;
alter table node_ip_address add constraint node_ip_address_rotation_status_id foreign key (rotation_status_id) references rotation_status (id);

alter table node_ip_address drop foreign key node_ip_address_aggregate_rotation_status_id;
alter table node_ip_address modify column aggregate_rotation_status_id tinyint unsigned;
alter table node_ip_address add constraint node_ip_address_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node drop foreign key node_aggregate_rotation_status_id;
alter table node modify column aggregate_rotation_status_id tinyint unsigned;
alter table node add constraint node_aggregate_rotation_status_id foreign key (aggregate_rotation_status_id) references rotation_status (id);

alter table node drop foreign key node_health_status_id;
alter table node drop key health_status_id;
alter table node modify column health_status_id tinyint unsigned;
alter table node add key health_status_id (health_status_id);
alter table node add constraint node_health_status_id foreign key (health_status_id) references health_status (id);
