CREATE EXTENSION IF NOT EXISTS pgcrypto SCHEMA pg_catalog;

DROP SCHEMA IF EXISTS core CASCADE;

CREATE SCHEMA core;

SET search_path TO core;



CREATE TABLE galaxies(
    id BIGSERIAL PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    galaxy bigint REFERENCES galaxies(id) ON UPDATE CASCADE ON DELETE SET NULL,
    email text NOT NULL UNIQUE,
    password text NOT NULL
);

CREATE TABLE sessions(
    id text PRIMARY KEY,
    usr integer NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE civilizations(
    id BIGSERIAL PRIMARY KEY,
    name text NOT NULL,
    usr bigint NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);
