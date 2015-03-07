-- Scheme to generate IP address sample data:
-- 
-- VLANS:
--   Development (includes Integration, Acceptance, Performance):
--     App : 10.13.0.0/16
--     DB  : 10.17.0.0/16
--   Production:
--     App : 10.1.0.0/16
--     DB  : 10.5.0.0/16
--   DR:
--     App : 10.7.0.0/16
--     DB  : 10.11.0.0/16

-- SUBNETS:
--   Integration:
--     App : 10.13.0.0/20
--     DB  : 10.17.0.0/20
--   Acceptance:
--     App : 10.13.16.0/20
--     DB  : 10.17.16.0/20
--   Performance:
--     App : 10.13.32.0/20
--     DB  : 10.17.32.0/20
--   Production:
--     App : 10.1.0.0/16
--     DB  : 10.5.0.0/16
--   DR:
--     App : 10.7.0.0/16
--     DB  : 10.11.0.0/16


-- =====================================================================================================================
-- Les procédures
-- =====================================================================================================================

delimiter $$
drop procedure if exists create_load_balancer $$
create procedure create_load_balancer(
  in data_center_key varchar(40),
  in name varchar(80),
  in type varchar(80),
  in ip_address varchar(20),
  in api_url varchar(250))

begin
  declare data_center_id smallint unsigned;
  
  set data_center_id = (select id from data_center where ukey = data_center_key);
  
  insert into load_balancer (data_center_id, name, type, ip_address, api_url) values
      (data_center_id, name, type, ip_address, api_url);
end $$
delimiter ;

-- http://forums.mysql.com/read.php?98,576167,576175#msg-576175
delimiter $$
drop procedure if exists create_service_instance $$
create procedure create_service_instance(
  in _ukey varchar(40),
  in _service_key varchar(40),
  in _environment_key varchar(40),
  in _data_center_key varchar(40),
  in _load_balanced tinyint(1) unsigned,
  in _load_balancer_name varchar(40),
  in _enable_seyren tinyint(1) unsigned)

begin
  declare _sid int unsigned;
  declare _eid int unsigned;
  declare _dcid int unsigned;
  declare _lbid int unsigned;
  
  set _sid = (select id from service where ukey = _service_key);
  set _eid = (select id from environment where ukey = _environment_key);
  set _dcid = (select id from data_center where ukey = _data_center_key);
  set _lbid = (select id from load_balancer where name = _load_balancer_name);
  
  insert into service_instance (ukey, service_id, environment_id, data_center_id, load_balanced, load_balancer_id, enable_seyren) values
      (_ukey, _sid, _eid, _dcid, _load_balanced, _lbid, _enable_seyren);
end $$
delimiter ;

-- Inserts a range of machines.
delimiter $$
drop procedure if exists create_machines $$
create procedure create_machines(
  in machine_start_index int unsigned,
  in machine_end_index int unsigned,
  in machine_start_id int unsigned,
  in host_prefix varchar(40),
  in domain varchar(40),
  in os varchar(40),
  in data_center_id int unsigned,
  in ip_address_start bigint unsigned)
  
begin
  declare machine_index int unsigned;
  declare machine_id int unsigned;
  declare hostname varchar(40);
  declare machine_num char(3);
  declare ip_address_int int unsigned;
  declare octet1 int unsigned;
  declare octet2 int unsigned;
  declare octet3 int unsigned;
  declare octet4 int unsigned;
  declare ip_address varchar(20);
  declare fqdn varchar(80);
  
  set machine_index = machine_start_index;
  while machine_index <= machine_end_index do
    set machine_id = machine_start_id + machine_index - 1;
    set ip_address_int = ip_address_start + machine_index - 1;
    
    set machine_num = right(concat('000', machine_index), 3);
    set hostname = concat(host_prefix, machine_num);
    set fqdn = concat(hostname, '.', domain);
    
    set octet1 = ((ip_address_int >> 24) & 255);
    set octet2 = ((ip_address_int >> 16) & 255);
    set octet3 = ((ip_address_int >> 8) & 255);
    set octet4 = (ip_address_int & 255);
    set ip_address = concat(octet1, '.', octet2, '.', octet3, '.', octet4);
    
    insert into machine (id, name, hostname, domain, os, data_center_id, ip_address, fqdn) values
      (machine_id, fqdn, hostname, domain, os, data_center_id, ip_address, fqdn);
    set machine_index = machine_index + 1;
  end while;
end $$
delimiter ;

-- Inserts a range of nodes
delimiter $$
drop procedure if exists create_nodes $$
create procedure create_nodes(
  in service_instance_key varchar(40),
  in num_nodes int unsigned,
  in service_key varchar(40),
  in env_key varchar(40),
  in service_version varchar(128),
  in machine_start_id int unsigned)
  
begin
  declare node_index int unsigned;
  declare machine_id int unsigned;
  declare node_name varchar(80);
  declare node_num char(3);
  declare service_instance_id int unsigned;
  
  set service_instance_id = (select id from service_instance where ukey = service_instance_key);
  
  set node_index = 1;
  while node_index <= num_nodes do
    set machine_id = machine_start_id + node_index - 1;
    set node_num = right(concat('000', node_index), 3);
    set node_name = concat(service_key, node_num, '-', env_key);
    insert into node (name, version, service_instance_id, machine_id, health_status_id) values
        (node_name, service_version, service_instance_id, machine_id, 1);
    set node_index = node_index + 1;
  end while;
end $$
delimiter ;

delimiter $$
drop procedure if exists create_dashboard $$
create procedure create_dashboard(
  in _ukey varchar(80),
  in _name varchar(250),
  in _type varchar(80),
  in _description varchar(1000),
  in _ui_uri varchar(255),
  in _api_uri varchar(255),
  in _source_key varchar(80))

begin
  declare _source_id int unsigned;
  
  set _source_id = (select id from source where ukey = _source_key);
  
  insert into dashboard (ukey, name, type, description, ui_uri, api_uri, source_id) values
      (_ukey, _name, _type, _description, _ui_uri, _api_uri, _source_id);
end $$
delimiter ;

delimiter $$
drop procedure if exists create_service_instance_dashboard $$
create procedure create_service_instance_dashboard(
  in _service_instance_key varchar(40),
  in _dashboard_key varchar(80))

begin
  declare _service_instance_id int unsigned;
  declare _dashboard_id int unsigned;
  
  set _service_instance_id = (select id from service_instance where ukey = _service_instance_key);
  set _dashboard_id = (select id from dashboard where ukey = _dashboard_key);
  
  insert into service_instance_dashboard (service_instance_id, dashboard_id) values
      (_service_instance_id, _dashboard_id);
end $$
delimiter ;

delimiter $$
drop procedure if exists create_seyren_check $$
create procedure create_seyren_check(
  in _seyren_id varchar(40),
  in _name varchar(250),
  in _description varchar(1000),
  in _graphite_base_url varchar(250),
  in _target varchar(1000),
  in _warn bigint,
  in _error bigint,
  in _enabled tinyint unsigned,
  in _state varchar(20),
  in _source_key varchar(80))

begin
  declare _source_id int unsigned;
  
  set _source_id = (select id from source where ukey = _source_key);
  
  insert into seyren_check (seyren_id, name, description, graphite_base_url, target, warn, error, enabled, state, source_id) values
      (_seyren_id, _name, _description, _graphite_base_url, _target, _warn, _error, _enabled, _state, _source_id);
end $$
delimiter ;

delimiter $$
drop procedure if exists create_service_instance_seyren_check $$
create procedure create_service_instance_seyren_check(
  in _service_instance_key varchar(40),
  in _seyren_check_key varchar(80))

begin
  declare _service_instance_id int unsigned;
  declare _seyren_check_id int unsigned;
  
  set _service_instance_id = (select id from service_instance where ukey = _service_instance_key);
  set _seyren_check_id = (select id from seyren_check where seyren_id = _seyren_check_key);
  
  insert into service_instance_seyren_check (service_instance_id, seyren_check_id) values
      (_service_instance_id, _seyren_check_id);
end $$
delimiter ;


-- =====================================================================================================================
-- Les données
-- =====================================================================================================================

insert into source (id, ukey, base_uri, source_id) values
  (1, 'seiso-data-common', 'https://github.example.com/seiso-data/common', 1)
, (2, 'seyren-prod', 'http://seyren.example.com', 1)
  ;

insert into `user` (id, username, password, enabled) values
  (1, 'seiso-admin', '$2a$10$FnqVAz2UrFQnQkMxVgVNpOyQj0sFSmF0VD8zsQyG2rhd.Wji7mN9y', 1)
, (2, 'seiso-user', '$2a$10$GkVLYh34PyRd15yaUrltae3gE8uXGhxZqWlKc2ix1v.2LLsibhI6e', 1)
  ;

insert into user_role (id, user_id, role_id) values
  (1, 1, 1)
, (2, 2, 2)
  ;

insert into health_status (id, ukey, name, status_type_id) values
  (1, 'healthy', 'Healthy', 5)
, (2, 'degraded', 'Degraded', 6)
, (3, 'down', 'Down', 1)
  ;

insert into person (id, username, first_name, last_name, title, email) values
  (1, 'rweidler', 'Regine', 'Weidler', 'Director, Software Engineering', 'rweidler@example.com')
, (2, 'ljohnson', 'Laquita', 'Johnson', 'Release Engineer II', 'ljohnson@example.com')
, (3, 'jgoodsell', 'Jerald', 'Goodsell', 'Software Engineer II', 'jgoodsell@example.com')
, (4, 'jable', 'Jannette', 'Able', 'Senior Project Manager', 'jable@example.com')
, (5, 'chalbrook', 'Chiquita', 'Halbrook', 'Manager, Software Engineering', 'chalbrook@example.com')
, (6, 'lcardello', 'Lynn', 'Cardello', 'Software Engineer I', 'lcardello@example.com')
, (7, 'qschuessler', 'Quinn', 'Schuessler', 'Principal Software Architect', 'qschuessler@example.com')
, (8, 'mneider', 'Micaela', 'Neider', 'Business Analyst I', 'mneider@example.com')
, (9, 'varbore', 'Vincenzo', 'Arbore', 'Senior Performance Engineer', 'varbore@example.com')
, (10, 'bpineda', 'Bonita', 'Pineda', 'Software Engineer II', 'bpineda@example.com')
, (11, 'scantrell', 'Sal', 'Cantrell', 'Test Engineer II', 'scantrell@example.com')
, (12, 'afichter', 'Asley', 'Fichter', 'Senior Project Manager', 'afichter@example.com')
, (13, 'crothschild', 'Chong', 'Rothschild', 'Senior Software Engineer', 'crothschild@example.com')
, (14, 'depting', 'Denna', 'Epting', 'VP, Software Engineering', 'depting@example.com')
, (15, 'ereider', 'Edgar', 'Reider', 'Senior Director, Operations', 'ereider@example.com')
, (16, 'hbanton', 'Heidi', 'Banton', 'Manager, Performance Engineering', 'hbanton@example.com')
, (17, 'lyan', 'Lillie', 'Yan', 'Manager, Software Engineering', 'lyan@example.com')
, (18, 'lalred', 'Laurette', 'Alred', 'UX Specialist', 'lalred@example.com')
, (19, 'dlounsbury', 'Dorsey', 'Lounsbury', 'Database Administrator II', 'dlounsbury@example.com')
, (20, 'mrethman', 'Mike', 'Rethman', 'Operations Analyst I', 'mrethman@example.com')
  ;

update person set company = 'Example.com';

update person set manager_id = 14 where id in (1, 7);
update person set manager_id = 5 where id in (3, 6, 10, 18);
update person set manager_id = 1 where id in (5, 16, 17);
update person set manager_id = 16 where id in (9);
update person set manager_id = 7 where id in (8, 13);

insert into infrastructure_provider (id, ukey, name) values
  (1, 'internal', 'Internal')
, (2, 'aws', 'Amazon Web Services')
, (3, 'digital-ocean', 'Digital Ocean')
  ;
  
insert into region (id, ukey, name, region_key, provider_id) values
  (1, 'internal-us-east-1', 'Internal US East 1', 'na', 1)
, (2, 'internal-us-west-1', 'Internal US West 1', 'na', 1)
, (3, 'aws-us-east-1', 'AWS US East 1 (N. Virginia)', 'na', 2)
, (4, 'aws-us-west-1', 'AWS US West 1 (Oregon)', 'na', 2)
, (5, 'aws-us-west-2', 'AWS US West 2 (N. California)', 'na', 2)
, (6, 'aws-eu-1', 'AWS Europe 1 (Ireland)', 'eu', 2)
, (7, 'aws-apac-1', 'AWS APAC 1 (Singapore)', 'apac', 2)
, (8, 'aws-apac-2', 'AWS APAC 2 (Tokyo)', 'apac', 2)
, (9, 'aws-apac-3', 'AWS APAC 3 (Sydney)', 'apac', 2)
, (10, 'aws-sa-1', 'AWS South America 1 (Sao Paulo)', 'sa', 2)
, (11, 'do-us-east-1', 'DO US East 1', 'na', 3)
  ;

insert into data_center (id, ukey, name, region_id) values
  (1, 'internal-us-east-1a', 'Internal US East 1a', 1)
, (2, 'internal-us-west-1a', 'Internal US West 1a', 2)
, (3, 'aws-us-east-1a', 'AWS US East 1a', 3)
, (4, 'aws-us-east-1b', 'AWS US East 1b', 3)
, (5, 'aws-us-east-1c', 'AWS US East 1c', 3)
, (6, 'aws-us-east-1d', 'AWS US East 1d', 3)
, (7, 'aws-us-west-1a', 'AWS US West 1a', 4)
, (8, 'aws-us-west-1b', 'AWS US West 1b', 4)
, (9, 'aws-us-west-1c', 'AWS US West 1c', 4)
, (10, 'do-us-east-1a', 'DO US East 1a', 11)
  ;

call create_load_balancer ('aws-us-east-1a', 'aws-elb-001', 'elb', '1.2.3.4', null);
call create_load_balancer ('aws-us-west-1a', 'aws-elb-002', 'elb', '1.2.3.5', null);

-- TODO Create machines one environment at a time so we can just provide a single IP seed. Easier to manage.

-- Air Shopping
call create_machines(1, 2, 1, 'airshop', 'int.example.com', 'linux', 3, 168624129);
call create_machines(1, 2, 3, 'airshop', 'acc.example.com', 'linux', 3, 168628225);
call create_machines(1, 2, 5, 'airshop', 'perf.example.com', 'linux', 3, 168632321);
call create_machines(1, 4, 7, 'airshop', 'prod.example.com', 'linux', 3, 167837697);
call create_machines(1, 4, 11, 'airshop', 'dr.example.com', 'linux', 3, 168230913);

-- Air Booking
call create_machines(1, 2, 15, 'airbook', 'int.example.com', 'linux', 3, 168624131);
call create_machines(1, 2, 17, 'airbook', 'acc.example.com', 'linux', 3, 168628227);
call create_machines(1, 2, 19, 'airbook', 'perf.example.com', 'linux', 3, 168632323);
call create_machines(1, 4, 21, 'airbook', 'prod.example.com', 'linux', 3, 167837701);
call create_machines(1, 4, 25, 'airbook', 'dr.example.com', 'linux', 3, 168230917);

-- Car Shopping
call create_machines(1, 2, 29, 'carshop', 'int.example.com', 'linux', 3, 168624133);
call create_machines(1, 2, 31, 'carshop', 'acc.example.com', 'linux', 3, 168628229);
call create_machines(1, 2, 33, 'carshop', 'perf.example.com', 'linux', 3, 168632325);
call create_machines(1, 4, 35, 'carshop', 'prod.example.com', 'linux', 3, 167837705);
call create_machines(1, 4, 39, 'carshop', 'dr.example.com', 'linux', 3, 168230921);

-- Car Booking
call create_machines(1, 2, 43, 'carbook', 'int.example.com', 'linux', 3, 168624135);
call create_machines(1, 2, 45, 'carbook', 'acc.example.com', 'linux', 3, 168628231);
call create_machines(1, 2, 47, 'carbook', 'perf.example.com', 'linux', 3, 168632327);
call create_machines(1, 4, 49, 'carbook', 'prod.example.com', 'linux', 3, 167837709);
call create_machines(1, 4, 53, 'carbook', 'dr.example.com', 'linux', 3, 168230925);

-- Cruise Shopping and Booking
-- Two nodes per machine here, each with its own IP address.
call create_machines(1, 2, 57, 'cruise', 'int.example.com', 'linux', 3, 168624137);
call create_machines(1, 2, 59, 'cruise', 'acc.example.com', 'linux', 3, 168628233);
call create_machines(1, 2, 61, 'cruise', 'perf.example.com', 'linux', 3, 168632329);
call create_machines(1, 4, 63, 'cruise', 'prod.example.com', 'linux', 3, 167837713);
call create_machines(1, 4, 67, 'cruise', 'dr.example.com', 'linux', 3, 168230929);

call create_machines(1, 4, 10000, 'hotelshop', 'int.example.com', 'linux', 3, 168624139);
call create_machines(1, 4, 10020, 'hotelshop', 'acc.example.com', 'linux', 3, 168628235);
call create_machines(1, 4, 10040, 'hotelshop', 'perf.example.com', 'linux', 3, 168632331);
call create_machines(1, 300, 10200, 'hotelshop', 'prod.example.com', 'linux', 3, 167837717);
call create_machines(1, 300, 10600, 'hotelshop', 'dr.example.com', 'linux', 3, 168230933);

call create_machines(1, 2, 11000, 'hotelbook', 'int.example.com', 'linux', 7, 168624143);
call create_machines(1, 2, 11020, 'hotelbook', 'acc.example.com', 'linux', 7, 168628239);
call create_machines(1, 2, 11040, 'hotelbook', 'perf.example.com', 'linux', 7, 168632335);
call create_machines(1, 30, 11200, 'hotelbook', 'prod.example.com', 'linux', 7, 167838017);
call create_machines(1, 30, 11600, 'hotelbook', 'dr.example.com', 'linux', 7, 168231233);

-- TODO Use proc
insert into environment (id, ukey, name, aka, description) values
  (1, 'int', 'Integration', null, 'Automated integration testing')
, (2, 'acc', 'Acceptance', null, 'Automated user acceptance testing')
, (3, 'perf', 'Performance', null, 'Automated performance, load and stress testing')
, (4, 'prod', 'Production', 'Live', 'Production')
, (5, 'dr', 'Disaster Recovery', null, 'Disaster recovery')
  ;

-- TODO Use proc
insert into service_group (id, ukey, name, description) values
  (1, 'air', 'Air Services', 'Air shopping, booking and support services.')
, (2, 'cars', 'Car Services', 'Rental car services.')
, (3, 'cruise', 'Cruise Services', 'Cruise shopping and booking services.')
, (4, 'devops', 'Devops Services', 'Service and infrastructure automation.')
, (5, 'hotel', 'Hotel', 'Hotel shopping, booking and support services.')
, (6, 'loyalty', 'Loyalty', 'Loyalty and rewards services.')
, (7, 'platform', 'Platform', 'Travel platform services.')
  ;

-- TODO Use proc
insert into service (id, ukey, name, group_id, type_id, description, owner_id, scm_repository, platform) values
  (1, 'air-shopping', 'Air Shopping Service', 1, 3, 'UI + REST API for air shopping.', 5, 'https://github.example.com/air-shopping', 'Java')
, (2, 'air-booking', 'Air Booking Service', 1, 3, 'UI + REST API for air booking.', 5, 'https://github.example.com/air-booking', 'Java')
, (3, 'car-shopping', 'Car Shopping Service', 2, 3, 'UI + REST API for car shopping.', 17, 'https://github.example.com/car-shopping', 'NodeJS')
, (4, 'car-booking', 'Car Booking Service', 2, 3, 'UI + REST API for car booking.', 17, 'https://github.example.com/car-booking', 'NodeJS')
, (5, 'cruise-shopping', 'Cruise Shopping Service', 3, 3, 'UI + REST API for cruise shopping.', 1, 'https://github.example.com/cruise-shopping', 'Java')
, (6, 'cruise-booking', 'Cruise Booking Service', 3, 3, 'UI + REST API for cruise booking.', 1, 'https://github.example.com/cruise-booking', 'Java')
, (7, 'hotel-shopping', 'Hotel Shopping Service', 5, 3, 'UI + REST API for hotel shopping.', 1, 'http://github.example.com/hotel-shopping', 'Java')
, (8, 'hotel-booking', 'Hotel Booking Service', 5, 3, 'UI + REST API for hotel booking.', 1, 'http://github.example.com/hotel-booking', 'Java')
  ;

call create_service_instance('air-shopping-int', 'air-shopping', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-shopping-acc', 'air-shopping', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-shopping-perf', 'air-shopping', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-shopping-prod', 'air-shopping', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', true); 
call create_service_instance('air-shopping-dr', 'air-shopping', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

call create_service_instance('air-booking-int', 'air-booking', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-booking-acc', 'air-booking', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-booking-perf', 'air-booking', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('air-booking-prod', 'air-booking', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', true); 
call create_service_instance('air-booking-dr', 'air-booking', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

call create_service_instance('car-shopping-int', 'car-shopping', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-shopping-acc', 'car-shopping', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-shopping-perf', 'car-shopping', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-shopping-prod', 'car-shopping', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', false); 
call create_service_instance('car-shopping-dr', 'car-shopping', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

call create_service_instance('car-booking-int', 'car-booking', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-booking-acc', 'car-booking', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-booking-perf', 'car-booking', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('car-booking-prod', 'car-booking', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', false); 
call create_service_instance('car-booking-dr', 'car-booking', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

call create_service_instance('cruise-shopping-int', 'cruise-shopping', 'int', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-shopping-acc', 'cruise-shopping', 'acc', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-shopping-perf', 'cruise-shopping', 'perf', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-shopping-prod', 'cruise-shopping', 'prod', 'internal-us-east-1a', true, null, false); 
call create_service_instance('cruise-shopping-dr', 'cruise-shopping', 'dr', 'internal-us-west-1a', true, null, false); 

call create_service_instance('cruise-booking-int', 'cruise-booking', 'int', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-booking-acc', 'cruise-booking', 'acc', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-booking-perf', 'cruise-booking', 'perf', 'do-us-east-1a', true, null, false); 
call create_service_instance('cruise-booking-prod', 'cruise-booking', 'prod', 'internal-us-east-1a', true, null, false); 
call create_service_instance('cruise-booking-dr', 'cruise-booking', 'dr', 'internal-us-west-1a', true, null, false); 

call create_service_instance('hotel-shopping-int', 'hotel-shopping', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-shopping-acc', 'hotel-shopping', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-shopping-perf', 'hotel-shopping', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-shopping-prod', 'hotel-shopping', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', false); 
call create_service_instance('hotel-shopping-dr', 'hotel-shopping', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

call create_service_instance('hotel-booking-int', 'hotel-booking', 'int', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-booking-acc', 'hotel-booking', 'acc', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-booking-perf', 'hotel-booking', 'perf', 'aws-us-east-1a', true, null, false); 
call create_service_instance('hotel-booking-prod', 'hotel-booking', 'prod', 'aws-us-east-1a', true, 'aws-elb-001', false); 
call create_service_instance('hotel-booking-dr', 'hotel-booking', 'dr', 'aws-us-west-1a', true, 'aws-elb-002', false); 

-- Autogenerate SIPs
insert into
  service_instance_port (service_instance_id, number, protocol, description)
select
  si.id,
  8443,
  'https',
  'UI + REST API port'
from
  service_instance si
  ;

insert into
  service_instance_port (service_instance_id, number, protocol, description)
select
  si.id,
  9443,
  'https',
  'Admin port'
from
  service_instance si
  ;

-- Autogenerate IPRs
insert into
  ip_address_role (id, service_instance_id, name, description)
select
  si.id,
  si.id,
  'default',
  'Default IP address role'
from
  service_instance si
  ;

-- Air Shopping
call create_nodes('air-shopping-int', 2, 'airshop', 'int', '3.15.0', 1);
call create_nodes('air-shopping-acc', 2, 'airshop', 'acc', '3.15.0', 3);
call create_nodes('air-shopping-perf', 2, 'airshop', 'perf', '3.15.0', 5);
call create_nodes('air-shopping-prod', 4, 'airshop', 'prod', '3.14.1', 7);
call create_nodes('air-shopping-dr', 4, 'airshop', 'dr', '3.14.1', 11);

-- Air Booking
call create_nodes('air-booking-int', 2, 'airbook', 'int', '6.0.2', 15);
call create_nodes('air-booking-acc', 2, 'airbook', 'acc', '6.0.2', 17);
call create_nodes('air-booking-perf', 2, 'airbook', 'perf', '6.0.2', 19);
call create_nodes('air-booking-prod', 4, 'airbook', 'prod', '6.0.1', 21);
call create_nodes('air-booking-dr', 4, 'airbook', 'dr', '6.0.1', 25);

-- Car Shopping
call create_nodes('car-shopping-int', 2, 'carshop', 'int', '1.21.0', 29);
call create_nodes('car-shopping-acc', 2, 'carshop', 'acc', '1.21.0', 31);
call create_nodes('car-shopping-perf', 2, 'carshop', 'perf', '1.21.0', 33);
call create_nodes('car-shopping-prod', 4, 'carshop', 'prod', '1.20.3', 35);
call create_nodes('car-shopping-dr', 4, 'carshop', 'dr', '1.20.3', 39);

-- Car Booking
call create_nodes('car-booking-int', 2, 'carbook', 'int', '1.18.0', 43);
call create_nodes('car-booking-acc', 2, 'carbook', 'acc', '1.18.0', 45);
call create_nodes('car-booking-perf', 2, 'carbook', 'perf', '1.18.0', 47);
call create_nodes('car-booking-prod', 4, 'carbook', 'prod', '1.17.3', 49);
call create_nodes('car-booking-dr', 4, 'carbook', 'dr', '1.17.3', 53);

-- Cruise Shopping
call create_nodes('cruise-shopping-int', 2, 'cruiseshop', 'int', '2.1.0', 57);
call create_nodes('cruise-shopping-acc', 2, 'cruiseshop', 'acc', '2.1.0', 59);
call create_nodes('cruise-shopping-perf', 2, 'cruiseshop', 'perf', '2.1.0', 61);
call create_nodes('cruise-shopping-prod', 4, 'cruiseshop', 'prod', '2.0.0', 63);
call create_nodes('cruise-shopping-dr', 4, 'cruiseshop', 'dr', '2.0.3', 67);

-- Cruise Booking
call create_nodes('cruise-booking-int', 2, 'cruisebook', 'int', '1.1.0', 57);
call create_nodes('cruise-booking-acc', 2, 'cruisebook', 'acc', '1.1.0', 59);
call create_nodes('cruise-booking-perf', 2, 'cruisebook', 'perf', '1.1.0', 61);
call create_nodes('cruise-booking-prod', 4, 'cruisebook', 'prod', '1.0.7', 63);
call create_nodes('cruise-booking-dr', 4, 'cruisebook', 'dr', '1.0.7', 67);

-- Hotel Shopping
call create_nodes('hotel-shopping-int', 4, 'hotelshop', 'int', '4.0.0', 10000);
call create_nodes('hotel-shopping-acc', 4, 'hotelshop', 'acc', '4.0.0', 10020);
call create_nodes('hotel-shopping-perf', 4, 'hotelshop', 'perf', '4.0.0', 10040);
call create_nodes('hotel-shopping-prod', 300, 'hotelshop', 'prod', '3.3.0', 10200);
call create_nodes('hotel-shopping-dr', 300, 'hotelshop', 'dr', '3.3.0', 10600);

-- Hotel Booking
call create_nodes('hotel-booking-int', 2, 'hotelbook', 'int', '3.1.2', 11000);
call create_nodes('hotel-booking-acc', 2, 'hotelbook', 'acc', '3.1.2', 11020);
call create_nodes('hotel-booking-perf', 2, 'hotelbook', 'perf', '3.1.2', 11040);
call create_nodes('hotel-booking-prod', 30, 'hotelbook', 'prod', '3.1.1', 11200);
call create_nodes('hotel-booking-dr', 30, 'hotelbook', 'dr', '3.1.1', 11600);

-- Autogenerate NIPs
insert into
  node_ip_address (node_id, ip_address_role_id, ip_address, rotation_status_id)
select
  n.id,
  ipr.id,
  m.ip_address,
  1
from
  node n,
  service_instance si,
  ip_address_role ipr,
  machine m
where
  n.service_instance_id = si.id
  and ipr.service_instance_id = si.id
  and n.machine_id = m.id
  ;

-- Autogenerate endpoints
insert into
  endpoint (service_instance_port_id, node_ip_address_id, rotation_status_id)
select
  sip.id,
  nip.id,
  1
from
  service_instance_port sip,
  node_ip_address nip,
  node n,
  service_instance si
where
  sip.service_instance_id = si.id
  and nip.node_id = n.id
  and n.service_instance_id = si.id
  ;

call create_dashboard('air-shopping-prod-metrics', 'Air Shopping System Metrics', 'Grafana', 'System metrics (CPU, memory, network, disk) for the Air Shopping production service instance.', 'https://dashboards.example.com/#/air-shopping-prod-metrics', 'https://dashboards.example.com/v1/air-shopping-prod-metrics', 'seiso-data-common');

call create_service_instance_dashboard('air-shopping-prod', 'air-shopping-prod-metrics');

call create_seyren_check('548b6c1ee4b05461bb170982', 'Air Shopping Prod - CPU Load', 'Maximum CPU load average across all servers', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshopp.airshop*-prod.load.load.shortterm)', 6, 8, 1, 'OK', 'seyren-prod');
call create_seyren_check('548b7a99e4b05461bb170ecc', 'Air Shopping Prod - Memory Free', 'Minimum free memory across all servers. If this drops too low then you may need to bounce the boxes.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.memory.memory-free)', 150000000, 100000000, 1, 'OK', 'seyren-prod');
call create_seyren_check('548b7ed5e4b05461bb171058', 'Air Shopping Prod - Disk Free', 'Minimum disk free across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.df-mapper_VolGroup01-var--log.df_complex-free)', 10000000000, 5000000000, 1, 'WARN', 'seyren-prod');
call create_seyren_check('548b88c9e4b013e35f320674', 'Air Shopping Prod - Network Rx', 'Maximum network packets received across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.interface-eth0.if_packets.rx)', 4000, 5000, 1, 'OK', 'seyren-prod');
call create_seyren_check('548b8cf8e4b013e35f320816', 'Air Shopping Prod - Network Tx', 'Maximum network packets transmitted across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.interface-eth0.if_packets.tx)', 4000, 5000, 1, 'OK', 'seyren-prod');

call create_service_instance_seyren_check('air-shopping-prod', '548b6c1ee4b05461bb170982');
call create_service_instance_seyren_check('air-shopping-prod', '548b7a99e4b05461bb170ecc');
call create_service_instance_seyren_check('air-shopping-prod', '548b7ed5e4b05461bb171058');
call create_service_instance_seyren_check('air-shopping-prod', '548b88c9e4b013e35f320674');
call create_service_instance_seyren_check('air-shopping-prod', '548b8cf8e4b013e35f320816');
