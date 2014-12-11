INSERT INTO `role` VALUES
  (1,'admin'),
  (2,'user');
INSERT INTO `status_type` VALUES
  (1,'danger','Danger'),
  (2,'default','Default'),
  (3,'info','Info'),
  (4,'primary','Primary'),
  (5,'success','Success'),
  (6,'warning','Warning');
INSERT INTO `rotation_status` VALUES
  (1,'enabled','Enabled',5),
  (2,'disabled','Disabled',6),
  (3,'excluded','Excluded',3),
  (4,'no-endpoints','No Endpoints',3),
  (5,'partial','Partial',6),
  (6,'unknown','Unknown',6);
INSERT INTO `service_type` VALUES
  (1,'application','User Application'),
  (2,'web-service','Web Service'),
  (3,'app-web-service','User Application + Web Service'),
  (4,'job','Job'),
  (5,'database','Database'),
  (6,'agent','Agent'),
  (7,'service','Service');
