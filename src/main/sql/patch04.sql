alter table node change column details health_status_link varchar(250) after health_status_id;
alter table node add column health_status_reason varchar(250) after health_status_link;
