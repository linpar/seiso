# Support seiso-import_seyren
ALTER TABLE seyren_check ADD COLUMN seyren_base_url varchar(250) not null AFTER id;
ALTER TABLE seyren_check DROP KEY seyren_id;
ALTER TABLE seyren_check ADD UNIQUE KEY seyren_base_url_seyren_id (seyren_base_url, seyren_id);
ALTER TABLE seyren_check DROP FOREIGN KEY seyren_check_source_id;
ALTER TABLE seyren_check MODIFY COLUMN source_id int(10) unsigned;
ALTER TABLE seyren_check ADD CONSTRAINT seyren_check_source_id FOREIGN KEY (source_id) REFERENCES source (id);

# Support for MB
ALTER TABLE person ADD COLUMN mb_type char(4) AFTER ldap_dn;
