-- Run this after running create-tables.sql
alter table machine add column serial_number varchar(80) after name;
