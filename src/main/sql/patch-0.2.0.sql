-- https://github.com/ExpediaDotCom/seiso/issues/44
alter table service_instance modify column load_balanced tinyint(1) unsigned default null;

-- https://github.com/ExpediaDotCom/seiso/issues/15
alter table person drop column source;

alter table data_center add column source_uri varchar(255);
alter table environment add column source_uri varchar(255);
alter table health_status add column source_uri varchar(255);
alter table infrastructure_provider add column source_uri varchar(255);
alter table ip_address_role add column source_uri varchar(255);
alter table load_balancer add column source_uri varchar(255);
alter table machine add column source_uri varchar(255);
alter table node add column source_uri varchar(255);
alter table node_ip_address add column source_uri varchar(255);
alter table person add column source_uri varchar(255);
alter table region add column source_uri varchar(255);
alter table rotation_status add column source_uri varchar(255);
alter table service add column source_uri varchar(255);
alter table service_group add column source_uri varchar(255);
alter table service_instance add column source_uri varchar(255);
alter table service_instance_port add column source_uri varchar(255);
alter table service_type add column source_uri varchar(255);
alter table status_type add column source_uri varchar(255);

alter table data_center add key source_uri (source_uri);
alter table environment add key source_uri (source_uri);
alter table health_status add key source_uri (source_uri);
alter table infrastructure_provider add key source_uri (source_uri);
alter table ip_address_role add key source_uri (source_uri);
alter table load_balancer add key source_uri (source_uri);
alter table machine add key source_uri (source_uri);
alter table node add key source_uri (source_uri);
alter table node_ip_address add key source_uri (source_uri);
alter table person add key source_uri (source_uri);
alter table region add key source_uri (source_uri);
alter table rotation_status add key source_uri (source_uri);
alter table service add key source_uri (source_uri);
alter table service_group add key source_uri (source_uri);
alter table service_instance add key source_uri (source_uri);
alter table service_instance_port add key source_uri (source_uri);
alter table service_type add key source_uri (source_uri);
alter table status_type add key source_uri (source_uri);
