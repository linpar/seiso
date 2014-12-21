alter table person add column source varchar(40) not null;

-- don't forget to set the source. e.g.,
-- 
--   update person set source='ldap-corp'
-- 
-- or whatever key you want.
