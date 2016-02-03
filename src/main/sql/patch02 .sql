-- Run this after running create-tables.sql
alter table node add column details varchar(250) after health_status;
