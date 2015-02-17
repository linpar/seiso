insert into `user` (id, username, password, enabled) values
  (1, 'seiso-admin', '$2a$10$FnqVAz2UrFQnQkMxVgVNpOyQj0sFSmF0VD8zsQyG2rhd.Wji7mN9y', 1),
  (2, 'seiso-user', '$2a$10$GkVLYh34PyRd15yaUrltae3gE8uXGhxZqWlKc2ix1v.2LLsibhI6e', 1)
  ;

insert into user_role (id, user_id, role_id) values
  (1, 1, 1),
  (2, 2, 2)
  ;
