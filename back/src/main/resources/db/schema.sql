CREATE EXTENSION IF NOT EXISTS pgcrypto SCHEMA pg_catalog;

DROP SCHEMA IF EXISTS core CASCADE;

CREATE SCHEMA core;

SET search_path TO core;



CREATE TABLE galaxies(
    id BIGSERIAL PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE star_systems(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    name text,
    galaxy bigint REFERENCES galaxies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    x double precision NOT NULL,
    y double precision NOT NULL,
    type integer NOT NULL,
    size integer NOT NULL,
    explored boolean NOT NULL DEFAULT false
);

CREATE TABLE users(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    galaxy bigint REFERENCES galaxies(id) ON UPDATE CASCADE ON DELETE SET NULL,
    email text NOT NULL UNIQUE,
    password text NOT NULL
);

CREATE TABLE sessions(
    id text PRIMARY KEY,
    usr integer NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE planets(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    star_system bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE CASCADE,
    orbit integer NOT NULL,
    type integer NOT NULL,
    size integer NOT NULL
);

CREATE TABLE civilizations(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    galaxy bigint NOT NULL REFERENCES galaxies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    name text NOT NULL,
    homeworld bigint NOT NULL REFERENCES planets(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    usr bigint NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE known_star_systems(
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    star_system bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(civilization, star_system)
);

CREATE TABLE visible_star_systems(
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    star_system bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL DEFAULT 1,
    PRIMARY KEY(civilization, star_system)
);

CREATE TABLE known_civilizations(
    knows bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    known bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(knows, known)
);

CREATE TABLE colonies(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    planet bigint NOT NULL REFERENCES planets(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE fleets(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    origin bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    travel_start_time bigint NOT NULL DEFAULT 0
);

CREATE TABLE travels(
    fleet bigint PRIMARY KEY REFERENCES fleets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    origin bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    start_time bigint NOT NULL
);


CREATE TABLE ships(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    fleet bigint NOT NULL REFERENCES fleets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    model_name text NOT NULL,
    can_colonize boolean NOT NULL,
    can_fight boolean NOT NULL
);