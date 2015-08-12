# Support seiso-import_seyren
ALTER TABLE seyren_check ADD COLUMN seyren_base_url varchar(250) not null AFTER id;
ALTER TABLE seyren_check DROP KEY seyren_id;
ALTER TABLE seyren_check ADD UNIQUE KEY seyren_base_url_seyren_id (seyren_base_url, seyren_id);
ALTER TABLE seyren_check DROP FOREIGN KEY seyren_check_source_id;
ALTER TABLE seyren_check MODIFY COLUMN source_id int(10) unsigned;
ALTER TABLE seyren_check ADD CONSTRAINT seyren_check_source_id FOREIGN KEY (source_id) REFERENCES source (id);

# Support for MB
ALTER TABLE person ADD COLUMN mb_type char(4) AFTER ldap_dn;

# Issues #110, #111, #112
alter table service_type add column description varchar(250) after name;
alter table status_type add column description varchar(250) after name;
alter table health_status add column description varchar(250) after name;
alter table rotation_status add column description varchar(250) after name;

# Issue #113: Service instance descriptions
alter table service_instance add column description varchar(250) after ukey;

# Allow null.
alter table dashboard modify column source_id int(10) unsigned;

# Part of issue #9.
create table service_instance_dependency (
  id int unsigned not null auto_increment primary key,
  dependent_id int unsigned not null,
  dependency_id int unsigned not null,
  description varchar(250),
  key `dependent_id` (dependent_id),
  key `dependency_id` (dependency_id),
  unique key `dependent_id_dependency_id` (dependent_id, dependency_id),
  constraint `service_instance_dependency_dependent_id` foreign key (dependent_id) references service_instance (id),
  constraint `service_instance_dependency_dependency_id` foreign key (dependency_id) references service_instance (id)
) engine=InnoDB default charset=utf8;

# Group support - issue #133
create table `person_group` (
  id smallint unsigned not null auto_increment primary key,
  name varchar(80) not null,
  alias varchar(80),
  unique key name (name),
  unique key alias (alias)
) engine=InnoDB default charset=utf8;

create table group_owner (
  id int unsigned not null auto_increment primary key,
  group_id smallint unsigned not null,
  person_id smallint unsigned not null,
  unique key group_id_person_id (group_id, person_id),
  key `person_id` (person_id),
  constraint group_owner_group_id foreign key (group_id) references `person_group` (id),
  constraint group_owner_person_id foreign key (person_id) references person (id)
) engine=InnoDB default charset=utf8;

create table group_member (
  id int unsigned not null auto_increment primary key,
  group_id smallint unsigned not null,
  person_id smallint unsigned not null,
  unique key group_id_person_id (group_id, person_id),
  key `person_id` (person_id),
  constraint group_member_group_id foreign key (group_id) references `person_group` (id),
  constraint group_member_person_id foreign key (person_id) references person (id)
) engine=InnoDB default charset=utf8;

# Support for person display name - issue #147
alter table person add column display_name varchar(160) after last_name;
