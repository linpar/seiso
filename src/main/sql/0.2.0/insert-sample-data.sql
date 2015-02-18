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

insert into machine (id, name, hostname, domain, os, data_center_id, ip_address, fqdn) values

  -- Air Shopping
  (1, 'airshop001.int.example.com', 'airshop001', 'int.example.com', 'linux', 3, '10.13.0.1', 'airshop001.int.example.com')
, (2, 'airshop002.int.example.com', 'airshop002', 'int.example.com', 'linux', 3, '10.13.0.2', 'airshop002.int.example.com')
, (3, 'airshop001.acc.example.com', 'airshop001', 'acc.example.com', 'linux', 3, '10.13.16.1', 'airshop001.acc.example.com')
, (4, 'airshop002.acc.example.com', 'airshop002', 'acc.example.com', 'linux', 3, '10.13.16.2', 'airshop002.acc.example.com')
, (5, 'airshop001.perf.example.com', 'airshop001', 'perf.example.com', 'linux', 3, '10.13.32.1', 'airshop001.perf.example.com')
, (6, 'airshop002.perf.example.com', 'airshop002', 'perf.example.com', 'linux', 3, '10.13.32.2', 'airshop002.perf.example.com')
, (7, 'airshop001.prod.example.com', 'airshop001', 'prod.example.com', 'linux', 3, '10.1.0.1', 'airshop001.prod.example.com')
, (8, 'airshop002.prod.example.com', 'airshop002', 'prod.example.com', 'linux', 3, '10.1.0.2', 'airshop002.prod.example.com')
, (9, 'airshop003.prod.example.com', 'airshop003', 'prod.example.com', 'linux', 3, '10.1.0.3', 'airshop003.prod.example.com')
, (10, 'airshop004.prod.example.com', 'airshop004', 'prod.example.com', 'linux', 3, '10.1.0.4', 'airshop004.prod.example.com')
, (11, 'airshop001.dr.example.com', 'airshop001', 'dr.example.com', 'linux', 7, '10.7.0.1', 'airshop001.dr.example.com')
, (12, 'airshop002.dr.example.com', 'airshop002', 'dr.example.com', 'linux', 7, '10.7.0.2', 'airshop002.dr.example.com')
, (13, 'airshop003.dr.example.com', 'airshop003', 'dr.example.com', 'linux', 7, '10.7.0.3', 'airshop003.dr.example.com')
, (14, 'airshop004.dr.example.com', 'airshop004', 'dr.example.com', 'linux', 7, '10.7.0.4', 'airshop004.dr.example.com')

  -- Air Booking
, (15, 'airbook001.int.example.com', 'airbook001', 'int.example.com', 'linux', 3, '10.13.0.3', 'airbook001.int.example.com')
, (16, 'airbook002.int.example.com', 'airbook002', 'int.example.com', 'linux', 3, '10.13.0.4', 'airbook002.int.example.com')
, (17, 'airbook001.acc.example.com', 'airbook001', 'acc.example.com', 'linux', 3, '10.13.16.3', 'airbook001.acc.example.com')
, (18, 'airbook002.acc.example.com', 'airbook002', 'acc.example.com', 'linux', 3, '10.13.16.4', 'airbook002.acc.example.com')
, (19, 'airbook001.perf.example.com', 'airbook001', 'perf.example.com', 'linux', 3, '10.13.32.3', 'airbook001.perf.example.com')
, (20, 'airbook002.perf.example.com', 'airbook002', 'perf.example.com', 'linux', 3, '10.13.32.4', 'airbook002.perf.example.com')
, (21, 'airbook001.prod.example.com', 'airbook001', 'prod.example.com', 'linux', 3, '10.1.0.5', 'airbook001.prod.example.com')
, (22, 'airbook002.prod.example.com', 'airbook002', 'prod.example.com', 'linux', 3, '10.1.0.6', 'airbook002.prod.example.com')
, (23, 'airbook003.prod.example.com', 'airbook003', 'prod.example.com', 'linux', 3, '10.1.0.7', 'airbook003.prod.example.com')
, (24, 'airbook004.prod.example.com', 'airbook004', 'prod.example.com', 'linux', 3, '10.1.0.8', 'airbook004.prod.example.com')
, (25, 'airbook001.dr.example.com', 'airbook001', 'dr.example.com', 'linux', 7, '10.7.0.5', 'airbook001.dr.example.com')
, (26, 'airbook002.dr.example.com', 'airbook002', 'dr.example.com', 'linux', 7, '10.7.0.6', 'airbook002.dr.example.com')
, (27, 'airbook003.dr.example.com', 'airbook003', 'dr.example.com', 'linux', 7, '10.7.0.7', 'airbook003.dr.example.com')
, (28, 'airbook004.dr.example.com', 'airbook004', 'dr.example.com', 'linux', 7, '10.7.0.8', 'airbook004.dr.example.com')

  -- Car Shopping
, (29, 'carshop001.int.example.com', 'carshop001', 'int.example.com', 'linux', 3, '10.13.0.5', 'carshop001.int.example.com')
, (30, 'carshop002.int.example.com', 'carshop002', 'int.example.com', 'linux', 3, '10.13.0.6', 'carshop002.int.example.com')
, (31, 'carshop001.acc.example.com', 'carshop001', 'acc.example.com', 'linux', 3, '10.13.16.5', 'carshop001.acc.example.com')
, (32, 'carshop002.acc.example.com', 'carshop002', 'acc.example.com', 'linux', 3, '10.13.16.6', 'carshop002.acc.example.com')
, (33, 'carshop001.perf.example.com', 'carshop001', 'perf.example.com', 'linux', 3, '10.13.32.5', 'carshop001.perf.example.com')
, (34, 'carshop002.perf.example.com', 'carshop002', 'perf.example.com', 'linux', 3, '10.13.32.6', 'carshop002.perf.example.com')
, (35, 'carshop001.prod.example.com', 'carshop001', 'prod.example.com', 'linux', 3, '10.1.0.9', 'carshop001.prod.example.com')
, (36, 'carshop002.prod.example.com', 'carshop002', 'prod.example.com', 'linux', 3, '10.1.0.10', 'carshop002.prod.example.com')
, (37, 'carshop003.prod.example.com', 'carshop003', 'prod.example.com', 'linux', 3, '10.1.0.11', 'carshop003.prod.example.com')
, (38, 'carshop004.prod.example.com', 'carshop004', 'prod.example.com', 'linux', 3, '10.1.0.12', 'carshop004.prod.example.com')
, (39, 'carshop001.dr.example.com', 'carshop001', 'dr.example.com', 'linux', 7, '10.7.0.9', 'carshop001.dr.example.com')
, (40, 'carshop002.dr.example.com', 'carshop002', 'dr.example.com', 'linux', 7, '10.7.0.10', 'carshop002.dr.example.com')
, (41, 'carshop003.dr.example.com', 'carshop003', 'dr.example.com', 'linux', 7, '10.7.0.11', 'carshop003.dr.example.com')
, (42, 'carshop004.dr.example.com', 'carshop004', 'dr.example.com', 'linux', 7, '10.7.0.12', 'carshop004.dr.example.com')

  -- Car Booking
, (43, 'carbook001.int.example.com', 'carbook001', 'int.example.com', 'linux', 3, '10.13.0.7', 'carbook001.int.example.com')
, (44, 'carbook002.int.example.com', 'carbook002', 'int.example.com', 'linux', 3, '10.13.0.8', 'carbook002.int.example.com')
, (45, 'carbook001.acc.example.com', 'carbook001', 'acc.example.com', 'linux', 3, '10.13.16.7', 'carbook001.acc.example.com')
, (46, 'carbook002.acc.example.com', 'carbook002', 'acc.example.com', 'linux', 3, '10.13.16.8', 'carbook002.acc.example.com')
, (47, 'carbook001.perf.example.com', 'carbook001', 'perf.example.com', 'linux', 3, '10.13.32.7', 'carbook001.perf.example.com')
, (48, 'carbook002.perf.example.com', 'carbook002', 'perf.example.com', 'linux', 3, '10.13.32.8', 'carbook002.perf.example.com')
, (49, 'carbook001.prod.example.com', 'carbook001', 'prod.example.com', 'linux', 3, '10.1.0.13', 'carbook001.prod.example.com')
, (50, 'carbook002.prod.example.com', 'carbook002', 'prod.example.com', 'linux', 3, '10.1.0.14', 'carbook002.prod.example.com')
, (51, 'carbook003.prod.example.com', 'carbook003', 'prod.example.com', 'linux', 3, '10.1.0.15', 'carbook003.prod.example.com')
, (52, 'carbook004.prod.example.com', 'carbook004', 'prod.example.com', 'linux', 3, '10.1.0.16', 'carbook004.prod.example.com')
, (53, 'carbook001.dr.example.com', 'carbook001', 'dr.example.com', 'linux', 7, '10.7.0.13', 'carbook001.dr.example.com')
, (54, 'carbook002.dr.example.com', 'carbook002', 'dr.example.com', 'linux', 7, '10.7.0.14', 'carbook002.dr.example.com')
, (55, 'carbook003.dr.example.com', 'carbook003', 'dr.example.com', 'linux', 7, '10.7.0.15', 'carbook003.dr.example.com')
, (56, 'carbook004.dr.example.com', 'carbook004', 'dr.example.com', 'linux', 7, '10.7.0.16', 'carbook004.dr.example.com')

  -- Cruise Shopping and Booking
, (57, 'cruise001.int.example.com', 'cruise001', 'int.example.com', 'linux', 10, '10.13.0.9', 'cruise001.int.example.com')
, (58, 'cruise002.int.example.com', 'cruise002', 'int.example.com', 'linux', 10, '10.13.0.10', 'cruise002.int.example.com')
, (59, 'cruise001.acc.example.com', 'cruise001', 'acc.example.com', 'linux', 10, '10.13.16.9', 'cruise001.acc.example.com')
, (60, 'cruise002.acc.example.com', 'cruise002', 'acc.example.com', 'linux', 10, '10.13.16.10', 'cruise002.acc.example.com')
, (61, 'cruise001.perf.example.com', 'cruise001', 'perf.example.com', 'linux', 10, '10.13.32.9', 'cruise001.perf.example.com')
, (62, 'cruise002.perf.example.com', 'cruise002', 'perf.example.com', 'linux', 10, '10.13.32.10', 'cruise002.perf.example.com')
, (63, 'cruise001.prod.example.com', 'cruise001', 'prod.example.com', 'linux', 1, '10.1.0.17', 'cruise001.prod.example.com')
, (64, 'cruise002.prod.example.com', 'cruise002', 'prod.example.com', 'linux', 1, '10.1.0.18', 'cruise002.prod.example.com')
, (65, 'cruise003.prod.example.com', 'cruise003', 'prod.example.com', 'linux', 1, '10.1.0.19', 'cruise003.prod.example.com')
, (66, 'cruise004.prod.example.com', 'cruise004', 'prod.example.com', 'linux', 1, '10.1.0.20', 'cruise004.prod.example.com')
, (67, 'cruise001.dr.example.com', 'cruise001', 'dr.example.com', 'linux', 2, '10.7.0.17', 'cruise001.dr.example.com')
, (68, 'cruise002.dr.example.com', 'cruise002', 'dr.example.com', 'linux', 2, '10.7.0.18', 'cruise002.dr.example.com')
, (69, 'cruise003.dr.example.com', 'cruise003', 'dr.example.com', 'linux', 2, '10.7.0.19', 'cruise003.dr.example.com')
, (70, 'cruise004.dr.example.com', 'cruise004', 'dr.example.com', 'linux', 2, '10.7.0.20', 'cruise004.dr.example.com')
  ;

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
  ;

insert into service_instance (id, ukey, service_id, environment_id, data_center_id, load_balanced) values
  (1, 'air-shopping-int', 1, 1, 3, true)
, (2, 'air-shopping-acc', 1, 2, 3, true)
, (3, 'air-shopping-perf', 1, 3, 3, true)
, (4, 'air-shopping-prod', 1, 4, 3, true)
, (5, 'air-shopping-dr', 1, 5, 7, true)
 
, (6, 'air-booking-int', 2, 1, 3, true)
, (7, 'air-booking-acc', 2, 2, 3, true)
, (8, 'air-booking-perf', 2, 3, 3, true)
, (9, 'air-booking-prod', 2, 4, 3, true)
, (10, 'air-booking-dr', 2, 5, 7, true)
 
, (11, 'car-shopping-int', 3, 1, 3, true)
, (12, 'car-shopping-acc', 3, 2, 3, true)
, (13, 'car-shopping-perf', 3, 3, 3, true)
, (14, 'car-shopping-prod', 3, 4, 3, true)
, (15, 'car-shopping-dr', 3, 5, 7, true)
 
, (16, 'car-booking-int', 4, 1, 3, true)
, (17, 'car-booking-acc', 4, 2, 3, true)
, (18, 'car-booking-perf', 4, 3, 3, true)
, (19, 'car-booking-prod', 4, 4, 3, true)
, (20, 'car-booking-dr', 4, 5, 7, true)
 
, (21, 'cruise-shopping-int', 5, 1, 10, true)
, (22, 'cruise-shopping-acc', 5, 2, 10, true)
, (23, 'cruise-shopping-perf', 5, 3, 10, true)
, (24, 'cruise-shopping-prod', 5, 4, 1, true)
, (25, 'cruise-shopping-dr', 5, 5, 2, true)
 
, (26, 'cruise-booking-int', 6, 1, 10, true)
, (27, 'cruise-booking-acc', 6, 2, 10, true)
, (28, 'cruise-booking-perf', 6, 3, 10, true)
, (29, 'cruise-booking-prod', 6, 4, 1, true)
, (30, 'cruise-booking-dr', 6, 5, 2, true)
  ;

insert into service_instance_port (id, service_instance_id, number, protocol, description) values
  (1, 1, 8443, 'https', 'UI + REST API port')
, (2, 2, 8443, 'https', 'UI + REST API port')
, (3, 3, 8443, 'https', 'UI + REST API port')
, (4, 4, 8443, 'https', 'UI + REST API port')
, (5, 5, 8443, 'https', 'UI + REST API port')
, (6, 6, 8443, 'https', 'UI + REST API port')
, (7, 7, 8443, 'https', 'UI + REST API port')
, (8, 8, 8443, 'https', 'UI + REST API port')
, (9, 9, 8443, 'https', 'UI + REST API port')
, (10, 10, 8443, 'https', 'UI + REST API port')
, (11, 11, 8443, 'https', 'UI + REST API port')
, (12, 12, 8443, 'https', 'UI + REST API port')
, (13, 13, 8443, 'https', 'UI + REST API port')
, (14, 14, 8443, 'https', 'UI + REST API port')
, (15, 15, 8443, 'https', 'UI + REST API port')
, (16, 16, 8443, 'https', 'UI + REST API port')
, (17, 17, 8443, 'https', 'UI + REST API port')
, (18, 18, 8443, 'https', 'UI + REST API port')
, (19, 19, 8443, 'https', 'UI + REST API port')
, (0, 20, 8443, 'https', 'UI + REST API port')
, (21, 21, 8443, 'https', 'UI + REST API port')
, (22, 22, 8443, 'https', 'UI + REST API port')
, (23, 23, 8443, 'https', 'UI + REST API port')
, (24, 24, 8443, 'https', 'UI + REST API port')
, (25, 25, 8443, 'https', 'UI + REST API port')
, (26, 26, 8443, 'https', 'UI + REST API port')
, (27, 27, 8443, 'https', 'UI + REST API port')
, (28, 28, 8443, 'https', 'UI + REST API port')
, (29, 29, 8443, 'https', 'UI + REST API port')
, (30, 30, 8443, 'https', 'UI + REST API port')
  ;
  
insert into ip_address_role (id, service_instance_id, name, description) values
  (1, 1, 'default', 'Default IP address role')
, (2, 2, 'default', 'Default IP address role')
, (3, 3, 'default', 'Default IP address role')
, (4, 4, 'default', 'Default IP address role')
, (5, 5, 'default', 'Default IP address role')
, (6, 6, 'default', 'Default IP address role')
, (7, 7, 'default', 'Default IP address role')
, (8, 8, 'default', 'Default IP address role')
, (9, 9, 'default', 'Default IP address role')
, (10, 10, 'default', 'Default IP address role')
, (11, 11, 'default', 'Default IP address role')
, (12, 12, 'default', 'Default IP address role')
, (13, 13, 'default', 'Default IP address role')
, (14, 14, 'default', 'Default IP address role')
, (15, 15, 'default', 'Default IP address role')
, (16, 16, 'default', 'Default IP address role')
, (17, 17, 'default', 'Default IP address role')
, (18, 18, 'default', 'Default IP address role')
, (19, 19, 'default', 'Default IP address role')
, (20, 20, 'default', 'Default IP address role')
, (21, 21, 'default', 'Default IP address role')
, (22, 22, 'default', 'Default IP address role')
, (23, 23, 'default', 'Default IP address role')
, (24, 24, 'default', 'Default IP address role')
, (25, 25, 'default', 'Default IP address role')
, (26, 26, 'default', 'Default IP address role')
, (27, 27, 'default', 'Default IP address role')
, (28, 28, 'default', 'Default IP address role')
, (29, 29, 'default', 'Default IP address role')
, (30, 30, 'default', 'Default IP address role')
  ;

insert into node (id, name, version, service_instance_id, machine_id, health_status_id) values

  -- Air Shopping
  (1, 'airshop001-int', '3.15.0', 1, 1, 1)
, (2, 'airshop002-int', '3.15.0', 1, 2, 1)
, (3, 'airshop001-acc', '3.15.0', 2, 3, 1)
, (4, 'airshop002-acc', '3.15.0', 2, 4, 1)
, (5, 'airshop001-perf', '3.15.0', 3, 5, 1)
, (6, 'airshop002-perf', '3.15.0', 3, 6, 1)
, (7, 'airshop001-prod', '3.14.1', 4, 7, 1)
, (8, 'airshop002-prod', '3.14.1', 4, 8, 2)
, (9, 'airshop003-prod', '3.14.1', 4, 9, 1)
, (10, 'airshop004-prod', '3.14.1', 4, 10, 1)
, (11, 'airshop001-dr', '3.14.1', 5, 11, 1)
, (12, 'airshop002-dr', '3.14.1', 5, 12, 1)
, (13, 'airshop003-dr', '3.14.1', 5, 13, 1)
, (14, 'airshop004-dr', '3.14.1', 5, 14, 1)

  -- Air Booking
, (15, 'airbook001-int', '6.0.23', 6, 15, 1)
, (16, 'airbook002-int', '6.0.23', 6, 16, 1)
, (17, 'airbook001-acc', '6.0.23', 7, 17, 1)
, (18, 'airbook002-acc', '6.0.23', 7, 18, 1)
, (19, 'airbook001-perf', '6.0.23', 8, 19, 1)
, (20, 'airbook002-perf', '6.0.23', 8, 20, 1)
, (21, 'airbook001-prod', '6.0.22', 9, 21, 3)
, (22, 'airbook002-prod', '6.0.22', 9, 22, 3)
, (23, 'airbook003-prod', '6.0.22', 9, 23, 3)
, (24, 'airbook004-prod', '6.0.22', 9, 24, 3)
, (25, 'airbook001-dr', '6.0.22', 10, 25, 1)
, (26, 'airbook002-dr', '6.0.22', 10, 26, 1)
, (27, 'airbook003-dr', '6.0.22', 10, 27, 1)
, (28, 'airbook004-dr', '6.0.22', 10, 28, 1)

  -- Car Shopping
, (29, 'carshop001-int', '1.21.0', 11, 29, 1)
, (30, 'carshop002-int', '1.21.0', 11, 30, 1)
, (31, 'carshop001-acc', '1.21.0', 12, 31, 1)
, (32, 'carshop002-acc', '1.21.0', 12, 32, 1)
, (33, 'carshop001-perf', '1.21.0', 13, 33, 2)
, (34, 'carshop002-perf', '1.21.0', 13, 34, 2)
, (35, 'carshop001-prod', '1.20.3', 14, 35, 1)
, (36, 'carshop002-prod', '1.20.3', 14, 36, 2)
, (37, 'carshop003-prod', '1.20.3', 14, 37, 1)
, (38, 'carshop004-prod', '1.20.3', 14, 38, 1)
, (39, 'carshop001-dr', '1.20.3', 15, 39, 1)
, (40, 'carshop002-dr', '1.20.3', 15, 40, 1)
, (41, 'carshop003-dr', '1.20.3', 15, 41, 1)
, (42, 'carshop004-dr', '1.20.3', 15, 42, 1)

  -- Car Booking
, (43, 'carbook001-int', '1.18.0', 16, 43, 3)
, (44, 'carbook002-int', '1.18.0', 16, 44, 1)
, (45, 'carbook001-acc', '1.18.0', 17, 45, 1)
, (46, 'carbook002-acc', '1.18.0', 17, 46, 1)
, (47, 'carbook001-perf', '1.18.0', 18, 47, 1)
, (48, 'carbook002-perf', '1.18.0', 18, 48, 1)
, (49, 'carbook001-prod', '1.17.3', 19, 49, 3)
, (50, 'carbook002-prod', '1.17.3', 19, 50, 3)
, (51, 'carbook003-prod', '1.17.3', 19, 51, 3)
, (52, 'carbook004-prod', '1.17.3', 19, 52, 3)
, (53, 'carbook001-dr', '1.17.3', 20, 53, 1)
, (54, 'carbook002-dr', '1.17.3', 20, 54, 1)
, (55, 'carbook003-dr', '1.17.3', 20, 55, 1)
, (56, 'carbook004-dr', '1.17.3', 20, 56, 1)

  -- Cruise Shopping
, (57, 'cruiseshop001-int', '2.1.0', 21, 57, 1)
, (58, 'cruiseshop002-int', '2.1.0', 21, 58, 1)
, (59, 'cruiseshop001-acc', '2.1.0', 22, 59, 1)
, (60, 'cruiseshop002-acc', '2.1.0', 22, 60, 1)
, (61, 'cruiseshop001-perf', '2.1.0', 23, 61, 1)
, (62, 'cruiseshop002-perf', '2.1.0', 23, 62, 1)
, (63, 'cruiseshop001-prod', '2.0.0', 24, 63, 1)
, (64, 'cruiseshop002-prod', '2.0.0', 24, 64, 1)
, (65, 'cruiseshop003-prod', '2.0.0', 24, 65, 1)
, (66, 'cruiseshop004-prod', '2.0.0', 24, 66, 1)
, (67, 'cruiseshop001-dr', '2.0.0', 25, 67, 1)
, (68, 'cruiseshop002-dr', '2.0.0', 25, 68, 1)
, (69, 'cruiseshop003-dr', '2.0.0', 25, 69, 1)
, (70, 'cruiseshop004-dr', '2.0.0', 25, 70, 1)

  -- Cruise Booking
, (71, 'cruisebook001-int', '1.1.0', 26, 57, 1)
, (72, 'cruisebook002-int', '1.1.0', 26, 58, 1)
, (73, 'cruisebook001-acc', '1.1.0', 27, 59, 1)
, (74, 'cruisebook002-acc', '1.1.0', 27, 60, 1)
, (75, 'cruisebook001-perf', '1.1.0', 28, 61, 1)
, (76, 'cruisebook002-perf', '1.1.0', 28, 62, 1)
, (77, 'cruisebook001-prod', '1.0.7', 29, 63, 1)
, (78, 'cruisebook002-prod', '1.0.7', 29, 64, 1)
, (79, 'cruisebook003-prod', '1.0.7', 29, 65, 1)
, (80, 'cruisebook004-prod', '1.0.7', 29, 66, 1)
, (81, 'cruisebook001-dr', '1.0.7', 30, 67, 1)
, (82, 'cruisebook002-dr', '1.0.7', 30, 68, 1)
, (83, 'cruisebook003-dr', '1.0.7', 30, 69, 1)
, (84, 'cruisebook004-dr', '1.0.7', 30, 70, 1)
  ;
  
insert into node_ip_address (id, node_id, ip_address_role_id, ip_address, rotation_status_id) values

  -- Air Shopping
  (1, 1, 1, '10.13.0.1', 1)
, (2, 2, 1, '10.13.0.2', 1)
, (3, 3, 2, '10.13.16.1', 1)
, (4, 4, 2, '10.13.16.2', 1)
, (5, 5, 3, '10.13.32.1', 1)
, (6, 6, 3, '10.13.32.2', 1)
, (7, 7, 4, '10.1.0.1', 1)
, (8, 8, 4, '10.1.0.2', 1)
, (9, 9, 4, '10.1.0.3', 1)
, (10, 10, 4, '10.1.0.4', 1)
, (11, 11, 5, '10.7.0.1', 1)
, (12, 12, 5, '10.7.0.2', 1)
, (13, 13, 5, '10.7.0.3', 1)
, (14, 14, 5, '10.7.0.4', 1)

  -- Air Booking
, (15, 15, 6, '10.13.0.3', 2)
, (16, 16, 6, '10.13.0.4', 1)
, (17, 17, 7, '10.13.16.3', 1)
, (18, 18, 7, '10.13.16.4', 1)
, (19, 19, 8, '10.13.32.3', 1)
, (20, 20, 8, '10.13.32.4', 1)
, (21, 21, 9, '10.1.0.5', 1)
, (22, 22, 9, '10.1.0.6', 1)
, (23, 23, 9, '10.1.0.7', 1)
, (24, 24, 9, '10.1.0.8', 1)
, (25, 25, 10, '10.7.0.5', 1)
, (26, 26, 10, '10.7.0.6', 1)
, (27, 27, 10, '10.7.0.7', 1)
, (28, 28, 10, '10.7.0.8', 1)

  -- Car Shopping
, (29, 29, 11, '10.13.0.5', 1)
, (30, 30, 11, '10.13.0.6', 1)
, (31, 31, 12, '10.13.16.5', 1)
, (32, 32, 12, '10.13.16.6', 1)
, (33, 33, 13, '10.13.32.5', 1)
, (34, 34, 13, '10.13.32.6', 1)
, (35, 35, 14, '10.1.0.9', 1)
, (36, 36, 14, '10.1.0.10', 1)
, (37, 37, 14, '10.1.0.11', 1)
, (38, 38, 14, '10.1.0.12', 1)
, (39, 39, 15, '10.7.0.9', 1)
, (40, 40, 15, '10.7.0.10', 1)
, (41, 41, 15, '10.7.0.11', 1)
, (42, 42, 15, '10.7.0.12', 1)

  -- Car Booking
, (43, 43, 16, '10.13.0.5', 2)
, (44, 44, 16, '10.13.0.6', 1)
, (45, 45, 17, '10.13.16.5', 1)
, (46, 46, 17, '10.13.16.6', 1)
, (47, 47, 18, '10.13.32.5', 1)
, (48, 48, 18, '10.13.32.6', 1)
, (49, 49, 19, '10.1.0.9', 1)
, (50, 50, 19, '10.1.0.10', 1)
, (51, 51, 19, '10.1.0.11', 1)
, (52, 52, 19, '10.1.0.12', 1)
, (53, 53, 20, '10.7.0.9', 1)
, (54, 54, 20, '10.7.0.10', 1)
, (55, 55, 20, '10.7.0.11', 1)
, (56, 56, 20, '10.7.0.12', 1)
  ;
