-- Patch file

-- =====================================================================================================================
-- Issue #65: Seiso/Seyren integration
-- =====================================================================================================================

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
) engine=InnoDB default charset=utf8;

create table service_instance_seyren_check (
  id int(10) unsigned not null auto_increment primary key,
  service_instance_id int(10) unsigned not null,
  seyren_check_id int(10) unsigned not null,
  key service_instance_id (service_instance_id),
  key seyren_check_id (seyren_check_id),
  constraint sisc_service_instance_id foreign key (service_instance_id) references service_instance (id),
  constraint sisc_seyren_check_id foreign key (seyren_check_id) references seyren_check (id)
) engine=InnoDB default charset=utf8;


-- =====================================================================================================================
-- Issue #66: Data sources
-- =====================================================================================================================

-- This is a configuration table, not an item table.
-- So we don't need to have a data_source_id field. Might add it in the future, but not now.
create table data_source (
  id int(10) unsigned not null auto_increment primary key,
  ukey varchar(80) not null,
  base_uri varchar(250) not null,
  unique key ukey (ukey),
  unique key base_uri (base_uri)
) engine=InnoDB default charset=utf8;


-- =====================================================================================================================
-- Issue #67: Update Seyren entity to use data sources
-- =====================================================================================================================

alter table seyren_check add column data_source_id int(10) unsigned not null after source_uri;
alter table seyren_check add key data_source_id (data_source_id);
alter table seyren_check add constraint seyren_check_data_source_id foreign key (data_source_id) references data_source (id);


-- =====================================================================================================================
-- Issue #24: Add per repo endpoints to get all keys for the repo type and source
-- =====================================================================================================================

alter table data_source rename to source;
alter table source add column source_id int(10) unsigned not null after base_uri;
alter table source add key source_id (source_id);
alter table source add constraint source_source_id foreign key (source_id) references source (id);

alter table seyren_check drop foreign key seyren_check_data_source_id;
alter table seyren_check drop key data_source_id;
alter table seyren_check change data_source_id source_id int(10) unsigned not null;
alter table seyren_check add key source_id (source_id);
alter table seyren_check add constraint seyren_check_source_id foreign key (source_id) references source (id);

-- Can't make the rest of these source_ids non-null right now, because none of the importers populates it.
-- But we are going to need non-null columns before the actual release.
alter table data_center drop column source_uri;
alter table data_center add column source_id int(10) unsigned;
alter table data_center add key source_id (source_id);
alter table data_center add constraint data_center_source_id foreign key (source_id) references source(id);

alter table endpoint drop column source_uri;
alter table endpoint add column source_id int(10) unsigned;
alter table endpoint add key source_id (source_id);
alter table endpoint add constraint endpoint_source_id foreign key (source_id) references source(id);

alter table environment drop column source_uri;
alter table environment add column source_id int(10) unsigned;
alter table environment add key source_id (source_id);
alter table environment add constraint environment_source_id foreign key (source_id) references source(id);

alter table health_status drop column source_uri;
alter table health_status add column source_id int(10) unsigned;
alter table health_status add key source_id (source_id);
alter table health_status add constraint health_status_source_id foreign key (source_id) references source(id);

alter table environment drop column source_uri;
alter table environment add column source_id int(10) unsigned;
alter table environment add key source_id (source_id);
alter table environment add constraint environment_source_id foreign key (source_id) references source(id);

alter table infrastructure_provider drop column source_uri;
alter table infrastructure_provider add column source_id int(10) unsigned;
alter table infrastructure_provider add key source_id (source_id);
alter table infrastructure_provider add constraint infrastructure_provider_source_id foreign key (source_id) references source(id);

alter table ip_address_role drop column source_uri;
alter table ip_address_role add column source_id int(10) unsigned;
alter table ip_address_role add key source_id (source_id);
alter table ip_address_role add constraint ip_address_role_source_id foreign key (source_id) references source(id);

alter table load_balancer drop column source_uri;
alter table load_balancer add column source_id int(10) unsigned;
alter table load_balancer add key source_id (source_id);
alter table load_balancer add constraint load_balancer_source_id foreign key (source_id) references source(id);

alter table machine drop column source_uri;
alter table machine add column source_id int(10) unsigned;
alter table machine add key source_id (source_id);
alter table machine add constraint machine_source_id foreign key (source_id) references source(id);

alter table node drop column source_uri;
alter table node add column source_id int(10) unsigned;
alter table node add key source_id (source_id);
alter table node add constraint node_source_id foreign key (source_id) references source(id);

alter table node_ip_address drop column source_uri;
alter table node_ip_address add column source_id int(10) unsigned;
alter table node_ip_address add key source_id (source_id);
alter table node_ip_address add constraint node_ip_address_source_id foreign key (source_id) references source(id);

alter table person drop column source_uri;
alter table person add column source_id int(10) unsigned;
alter table person add key source_id (source_id);
alter table person add constraint person_source_id foreign key (source_id) references source(id);

alter table region drop column source_uri;
alter table region add column source_id int(10) unsigned;
alter table region add key source_id (source_id);
alter table region add constraint region_source_id foreign key (source_id) references source(id);

-- alter table role drop column source_uri;
alter table role add column source_id int(10) unsigned;
alter table role add key source_id (source_id);
alter table role add constraint role_source_id foreign key (source_id) references source(id);

alter table rotation_status drop column source_uri;
alter table rotation_status add column source_id int(10) unsigned;
alter table rotation_status add key source_id (source_id);
alter table rotation_status add constraint rotation_status_source_id foreign key (source_id) references source(id);

alter table service drop column source_uri;
alter table service add column source_id int(10) unsigned;
alter table service add key source_id (source_id);
alter table service add constraint service_source_id foreign key (source_id) references source(id);

alter table service_group drop column source_uri;
alter table service_group add column source_id int(10) unsigned;
alter table service_group add key source_id (source_id);
alter table service_group add constraint service_group_source_id foreign key (source_id) references source(id);

alter table service_instance drop column source_uri;
alter table service_instance add column source_id int(10) unsigned;
alter table service_instance add key source_id (source_id);
alter table service_instance add constraint service_instance_source_id foreign key (source_id) references source(id);

alter table service_instance_port drop column source_uri;
alter table service_instance_port add column source_id int(10) unsigned;
alter table service_instance_port add key source_id (source_id);
alter table service_instance_port add constraint service_instance_port_source_id foreign key (source_id) references source(id);

alter table service_type drop column source_uri;
alter table service_type add column source_id int(10) unsigned;
alter table service_type add key source_id (source_id);
alter table service_type add constraint service_type_source_id foreign key (source_id) references source(id);

alter table status_type drop column source_uri;
alter table status_type add column source_id int(10) unsigned;
alter table status_type add key source_id (source_id);
alter table status_type add constraint status_type_source_id foreign key (source_id) references source(id);

alter table `user` drop column source_uri;
alter table `user` add column source_id int(10) unsigned;
alter table `user` add key source_id (source_id);
alter table `user` add constraint user_source_id foreign key (source_id) references source(id);

-- alter table user_role drop column source_uri;
alter table user_role add column source_id int(10) unsigned;
alter table user_role add key source_id (source_id);
alter table user_role add constraint user_role_source_id foreign key (source_id) references source(id);
