insert into `role` (id, name) values
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER')
  ;

insert into `status_type` (id, ukey, name) values
  (1, 'danger', 'Danger'),
  (2, 'default', 'Default'),
  (3, 'info', 'Info'),
  (4, 'primary', 'Primary'),
  (5, 'success', 'Success'),
  (6, 'warning', 'Warning')
  ;

insert into `rotation_status` (id, ukey, name, status_type_id) values
  (1, 'enabled', 'Enabled', 5),
  (2, 'disabled', 'Disabled', 6),
  (3, 'excluded', 'Excluded', 3),
  (4, 'no-endpoints', 'No Endpoints', 3),
  (5, 'partial', 'Partial', 6),
  (6, 'unknown', 'Unknown', 6)
  ;

insert into `service_type` (id, ukey, name) values
  (1, 'application', 'User Application'),
  (2, 'web-service', 'Web Service'),
  (3, 'app-web-service', 'User Application + Web Service'),
  (4, 'job', 'Job'),
  (5, 'database', 'Database'),
  (6, 'agent', 'Agent'),
  (7, 'service', 'Service')
  ;
