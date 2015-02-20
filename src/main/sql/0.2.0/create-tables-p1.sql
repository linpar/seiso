-- Patch file

alter table service_instance add column enable_seyren tinyint(1) unsigned not null default false after min_capacity_ops;

-- Was originally going to include Seyren subscriptions too, but seems better just to provide an API link to the check
-- details so the client can just navigate to the source.

create table seyren_check (
  id int(10) unsigned not null auto_increment primary key,
  seyren_id varchar(40) not null,
  name varchar(250) not null,
  description varchar(1000),
  graphite_base_url varchar(250) not null,
  target varchar(1000) not null,
  warn bigint not null,
  error bigint not null,
  enabled tinyint(1) unsigned not null,
  state varchar(20),
  source_uri varchar(255),
  unique key seyren_id (seyren_id)
) engine=InnoDB;

create table service_instance_seyren_check (
  id int(10) unsigned not null auto_increment primary key,
  service_instance_id int(10) unsigned not null,
  seyren_check_id int(10) unsigned not null,
  key service_instance_id (service_instance_id),
  key seyren_check_id (seyren_check_id),
  constraint sisc_service_instance_id foreign key (service_instance_id) references service_instance (id),
  constraint sisc_seyren_check_id foreign key (seyren_check_id) references seyren_check (id)
) engine=InnoDB;
