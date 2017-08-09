DROP DATABASE IF EXISTS aspect;

CREATE DATABASE aspect;

CREATE TABLE users (
    id varchar(32) NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    password_hash varchar(32) NOT NULL,
    email varchar(64) NOT NULL,
    first_name varchar(64),
    last_name varchar(64),
    created_at timestamp NOT NULL
);

CREATE UNIQUE INDEX users_name_idx ON users (name);

CREATE TABLE sessions (
    token varchar(32) NOT NULL PRIMARY KEY,
    userId varchar(32) NOT NULL REFERENCES users.id,
    created_at timestamp NOT NULL,
    lastActivityAt timestamp NOT NULL
);

CREATE TABLE projects (
    id varchar(32) NOT NULL PRIMARY KEY,
    user_id varchar(32) NOT NULL REFERENCES users.id,
    name varchar(1024) NOT NULL,
    created_at timestamp NOT NULL
);

CREATE TABLE targets (
    id varchar(32) NOT NULL PRIMARY KEY,
    project_id varchar(32) NOT NULL REFERENCES projects.id,
    name varchar(1024) NOT NULL,
    keywords text NULL,
    created_at timestamp NOT NULL
);
