-- DROP TABLE public.users;

CREATE TABLE users (
    id varchar(32) NOT NULL,
    checksum integer NOT NULL,
    name varchar(64) NOT NULL,
    password_hash varchar(32) NOT NULL,
    email varchar(128) NOT NULL,
    first_name varchar(128),
    last_name varchar(128),
    created_at date NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
)
