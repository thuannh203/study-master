create table t_user
(
 user_id INT NOT NULL,
 user_name VARCHAR(128) NOT NULL,
 user_password VARCHAR(128) NOT NULL,
 role_name VARCHAR(10) NOT NULL
);

create table permission
(
 permission_name VARCHAR(20) NOT NULL
);

create table role_permission
(
 role_permission_id VARCHAR(128) NOT NULL,
 role_name VARCHAR(10) NOT NULL,
 permission_name VARCHAR(20) NOT NULL
);

create table t_token
(
 token_id VARCHAR(128) NOT NULL,
 user_id VARCHAR(128) NOT NULL,
 access_token VARCHAR(1000) NOT NULL,
 register_timestamp BIGINT(11)
);
create table customer
(
  customer_id VARCHAR(128) NOT NULL,
  customer_code VARCHAR(128) NOT NULL,
  customer_name VARCHAR(128),
  sex VARCHAR(10),
  age int,
  address VARCHAR(128),
  register_timestamp BIGINT(11),
  UNIQUE INDEX customer_code (customer_code)
);