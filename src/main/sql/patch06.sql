update node set version = 1;
alter table node change version version int unsigned;
