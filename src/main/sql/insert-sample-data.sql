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

-- insert into load_balancer (id, data_center_id, name, type, ip_address, api_uri) values
--   ;

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

insert into environment (id, ukey, name, aka, description) values
  (1, 'int', 'Integration', null, 'Automated integration testing')
, (2, 'acc', 'Acceptance', null, 'Automated user acceptance testing')
, (3, 'perf', 'Performance', null, 'Automated performance, load and stress testing')
, (4, 'prod', 'Production', 'Live', 'Production')
, (5, 'dr', 'Disaster Recovery', null, 'Disaster recovery')
  ;

insert into service_group (id, ukey, name, description) values
  (1, 'air', 'Air Services', 'Air shopping, booking and support services.')
, (2, 'cars', 'Car Services', 'Rental car services.')
, (3, 'cruise', 'Cruise Services', 'Cruise shopping and booking services.')
, (4, 'devops', 'Devops Services', 'Service and infrastructure automation.')
, (5, 'hotel', 'Hotel', 'Hotel shopping, booking and support services.')
, (6, 'loyalty', 'Loyalty', 'Loyalty and rewards services.')
, (7, 'platform', 'Platform', 'Travel platform services.')
  ;

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

insert into service_instance (id, ukey, service_id, environment_id, data_center_id, load_balanced, enable_seyren) values
  (1, 'air-shopping-int', 1, 1, 3, true, false)
, (2, 'air-shopping-acc', 1, 2, 3, true, false)
, (3, 'air-shopping-perf', 1, 3, 3, true, false)
, (4, 'air-shopping-prod', 1, 4, 3, true, true)
, (5, 'air-shopping-dr', 1, 5, 7, true, false)
 
, (6, 'air-booking-int', 2, 1, 3, true, false)
, (7, 'air-booking-acc', 2, 2, 3, true, false)
, (8, 'air-booking-perf', 2, 3, 3, true, false)
, (9, 'air-booking-prod', 2, 4, 3, true, false)
, (10, 'air-booking-dr', 2, 5, 7, true, false)
 
, (11, 'car-shopping-int', 3, 1, 3, true, false)
, (12, 'car-shopping-acc', 3, 2, 3, true, false)
, (13, 'car-shopping-perf', 3, 3, 3, true, false)
, (14, 'car-shopping-prod', 3, 4, 3, true, false)
, (15, 'car-shopping-dr', 3, 5, 7, true, false)
 
, (16, 'car-booking-int', 4, 1, 3, true, false)
, (17, 'car-booking-acc', 4, 2, 3, true, false)
, (18, 'car-booking-perf', 4, 3, 3, true, false)
, (19, 'car-booking-prod', 4, 4, 3, true, false)
, (20, 'car-booking-dr', 4, 5, 7, true, false)
 
, (21, 'cruise-shopping-int', 5, 1, 10, true, false)
, (22, 'cruise-shopping-acc', 5, 2, 10, true, false)
, (23, 'cruise-shopping-perf', 5, 3, 10, true, false)
, (24, 'cruise-shopping-prod', 5, 4, 1, true, false)
, (25, 'cruise-shopping-dr', 5, 5, 2, true, false)
 
, (26, 'cruise-booking-int', 6, 1, 10, true, false)
, (27, 'cruise-booking-acc', 6, 2, 10, true, false)
, (28, 'cruise-booking-perf', 6, 3, 10, true, false)
, (29, 'cruise-booking-prod', 6, 4, 1, true, false)
, (30, 'cruise-booking-dr', 6, 5, 2, true, false)
 
, (31, 'hotel-shopping-int', 7, 1, 3, true, false)
, (32, 'hotel-shopping-acc', 7, 2, 3, true, false)
, (33, 'hotel-shopping-perf', 7, 3, 3, true, false)
, (34, 'hotel-shopping-prod', 7, 4, 3, true, false)
, (35, 'hotel-shopping-dr', 7, 5, 7, true, false)
 
, (36, 'hotel-booking-int', 8, 1, 3, true, false)
, (37, 'hotel-booking-acc', 8, 2, 3, true, false)
, (38, 'hotel-booking-perf', 8, 3, 3, true, false)
, (39, 'hotel-booking-prod', 8, 4, 3, true, false)
, (40, 'hotel-booking-dr', 8, 5, 7, true, false)
  ;

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

insert into dashboard (id, ukey, name, type, description, ui_uri, api_uri, source_id) values
  (1, 'air-shopping-prod-metrics', 'Air Shopping System Metrics', 'Grafana', 'System metrics (CPU, memory, network, disk) for the Air Shopping production service instance.', 'https://dashboards.example.com/#/air-shopping-prod-metrics', 'https://dashboards.example.com/v1/air-shopping-prod-metrics', 1)
  ;

insert into service_instance_dashboard values
  (1, 4, 1)
  ;

insert into seyren_check (id, seyren_id, name, description, graphite_base_url, target, warn, error, enabled, state, source_id) values
  (1, '548b6c1ee4b05461bb170982', 'Air Shopping Prod - CPU Load', 'Maximum CPU load average across all servers', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshopp.airshop*-prod.load.load.shortterm)', 6, 8, 1, 'OK', 2)
, (2, '548b7a99e4b05461bb170ecc', 'Air Shopping Prod - Memory Free', 'Minimum free memory across all servers. If this drops too low then you may need to bounce the boxes.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.memory.memory-free)', 150000000, 100000000, 1, 'OK', 2)
, (3, '548b7ed5e4b05461bb171058', 'Air Shopping Prod - Disk Free', 'Minimum disk free across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.df-mapper_VolGroup01-var--log.df_complex-free)', 10000000000, 5000000000, 1, 'WARN', 2)
, (4, '548b88c9e4b013e35f320674', 'Air Shopping Prod - Network Rx', 'Maximum network packets received across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.interface-eth0.if_packets.rx)', 4000, 5000, 1, 'OK', 2)
, (5, '548b8cf8e4b013e35f320816', 'Air Shopping Prod - Network Tx', 'Maximum network packets transmitted across all servers.', 'https://graphite.example.com', 'maxSeries(collectd_metrics.airshop.airshop*-prod.interface-eth0.if_packets.tx)', 4000, 5000, 1, 'OK', 2)
  ;

insert into service_instance_seyren_check values
  (1, 4, 1)
, (2, 4, 2)
, (3, 4, 3)
, (4, 4, 4)
, (5, 4, 5)
  ;
