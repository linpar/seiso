alter table node add column build_version varchar(128) after version;
update node set build_version = version;