CREATE EXTENSION IF NOT EXISTS pgcrypto SCHEMA pg_catalog;

DROP SCHEMA IF EXISTS core CASCADE;

CREATE SCHEMA core;

DROP SCHEMA IF EXISTS tg CASCADE;

CREATE SCHEMA tg;

DROP SCHEMA IF EXISTS test CASCADE;

CREATE SCHEMA test;

SET search_path TO core;



CREATE TABLE galaxies(
    id BIGSERIAL PRIMARY KEY,
    name text NOT NULL UNIQUE
);

CREATE TABLE star_systems(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    name text,
    galaxy bigint NOT NULL REFERENCES galaxies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    x double precision NOT NULL,
    y double precision NOT NULL,
    type integer NOT NULL,
    size integer NOT NULL,
    explored boolean NOT NULL DEFAULT false,
    UNIQUE(name, galaxy)
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

CREATE TABLE technologies(
    id text PRIMARY KEY,
    level integer NOT NULL CHECK(level > 0),
    name text NOT NULL
);

CREATE TABLE technologies_prerequisites(
    technology text NOT NULL REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    prerequisite text NOT NULL REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(technology, prerequisite)
);

CREATE TABLE planets(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    star_system bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE CASCADE,
    orbit integer NOT NULL,
    type integer NOT NULL,
    size integer NOT NULL,
    UNIQUE(star_system, orbit),
    CHECK (orbit > 0)
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
    planet bigint NOT NULL UNIQUE REFERENCES planets(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE colonies_technologies(
    colony bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    technology text NOT NULL REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(colony, technology)
);

CREATE TABLE fleets(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    origin bigint NOT NULL REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    travel_start_time bigint NOT NULL DEFAULT 0,
    colony_ships integer NOT NULL DEFAULT 0
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

CREATE TABLE resource_types(
    id text PRIMARY KEY,
    name text NOT NULL,
    merchantable boolean NOT NULL
);

CREATE TABLE colony_building_types(
    id text PRIMARY KEY,
    name text NOT NULL,
    buildable boolean NOT NULL,
    repeatable boolean NOT NULL
);

CREATE TABLE colony_building_types_prerequisites(
    building_type text NOT NULL REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    prerequisite text NOT NULL REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(building_type, prerequisite)
);

CREATE TABLE colony_building_capability_types(
    id text PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE colony_building_types_capabilities(
    building_type text NOT NULL REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    capability_type text NOT NULL REFERENCES colony_building_capability_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(building_type, capability_type)
);

CREATE TABLE colony_building_types_resources(
    building_type text REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    resource_type text REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL,
    PRIMARY KEY(building_type, resource_type)
);

CREATE TABLE colony_building_types_costs(
    building_type text REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    resource_type text REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL CHECK (quantity >= 0),
    PRIMARY KEY(building_type, resource_type)
);

CREATE TABLE colony_buildings(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    colony bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    building_type text NOT NULL REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE colony_resources(
    colony bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    resource_type text NOT NULL REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL,
    PRIMARY KEY(colony, resource_type)
);

CREATE TABLE ship_models(
    name text PRIMARY KEY,
    can_colonize boolean NOT NULL,
    can_fight boolean NOT NULL
);

CREATE TABLE civilization_ship_models(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    name text NOT NULL,
    can_colonize boolean NOT NULL,
    can_fight boolean NOT NULL
);

CREATE TABLE colony_building_orders(
    colony bigint PRIMARY KEY REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    building_type text REFERENCES colony_building_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    ship_model bigint REFERENCES civilization_ship_models(id) ON UPDATE CASCADE ON DELETE CASCADE,
    started_time bigint NOT NULL
);

CREATE TABLE stellar_governments(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    star_system bigint REFERENCES star_systems(id) ON UPDATE CASCADE ON DELETE CASCADE,
    civilization bigint NOT NULL REFERENCES civilizations(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL DEFAULT 1,
    UNIQUE(star_system, civilization)
);

CREATE TABLE research_orders(
    stellar_government bigint PRIMARY KEY REFERENCES stellar_governments(id) ON UPDATE CASCADE ON DELETE CASCADE,
    technology text REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    started_time bigint NOT NULL,
    finish_time bigint NOT NULL
);

CREATE TABLE stellar_governments_technologies(
    stellar_government bigint NOT NULL REFERENCES stellar_governments(id) ON UPDATE CASCADE ON DELETE CASCADE,
    technology text NOT NULL REFERENCES technologies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(stellar_government, technology)
);

CREATE TABLE trade_routes(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    origin bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE CHECK(origin <> destination),
    resource_type text NOT NULL REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL CHECK(quantity > 0),
    received_quantity integer NOT NULL CHECK(received_quantity > 0),
    UNIQUE(origin, destination, resource_type)
);

CREATE TABLE trade_routes_creation(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    origin bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE CHECK(origin <> destination),
    resource_type text NOT NULL REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    quantity integer NOT NULL CHECK(quantity > 0),
    started_time bigint NOT NULL,
    finish_time bigint NOT NULL
);

CREATE TABLE trade_routes_destruction(
    id bigint PRIMARY KEY DEFAULT nextval('galaxies_id_seq'::regclass),
    origin bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE,
    destination bigint NOT NULL REFERENCES colonies(id) ON UPDATE CASCADE ON DELETE CASCADE  CHECK(origin <> destination),
    resource_type text NOT NULL REFERENCES resource_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    received_quantity integer NOT NULL CHECK(received_quantity > 0),
    finish_time bigint NOT NULL
);

