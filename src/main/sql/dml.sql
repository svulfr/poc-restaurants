DROP SCHEMA IF EXISTS pocrest;

CREATE SCHEMA pocrest;

USE pocrest;

CREATE TABLE accounts (
  id       INT NOT NULL AUTO_INCREMENT,
  login    VARCHAR(63) UNIQUE,
  password CHAR(32),
  PRIMARY KEY (id),
  INDEX (login)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

CREATE TABLE accounts_roles (
  login VARCHAR(63),
  role  VARCHAR(63),
  CONSTRAINT FOREIGN KEY (login) REFERENCES accounts (login)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;


CREATE TABLE restaurants (
  id   INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(63),
  PRIMARY KEY (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

CREATE TABLE dishes (
  id         INT NOT NULL AUTO_INCREMENT,
  price      DECIMAL(6, 2),
  name       VARCHAR(63),
  restaurant INT NOT NULL,
  valid_on   DATE,
  PRIMARY KEY (id),
  INDEX (valid_on, restaurant),
  CONSTRAINT FOREIGN KEY (restaurant) REFERENCES restaurants (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

CREATE TABLE votes (
  restaurant INT NOT NULL,
  account    INT NOT NULL,
  UNIQUE (restaurant, account),
  CONSTRAINT FOREIGN KEY (restaurant) REFERENCES restaurants (id),
  CONSTRAINT FOREIGN KEY (account) REFERENCES accounts (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;


-- DDL

INSERT INTO accounts (login, password) VALUES
  ('a@a', MD5('test')),
  ('u1@a', MD5('test')),
  ('u2@a', MD5('test'));

INSERT INTO accounts_roles (login, role) VALUES
  ('a@a', 'ROLE_USER'),
  ('a@a', 'ROLE_ADMIN'),
  ('u1@a', 'ROLE_USER'),
  ('u2@a', 'ROLE_USER');


INSERT INTO restaurants (name) VALUES
  ('restaurant 1'),
  ('restaurant 2'),
  ('restaurant 3'),
  ('restaurant 4'),
  ('restaurant 5');

