insert into core.resource_types(id, name) values
('goods', 'Bienes de consumo'),
('food', 'Alimentos'),
('work', 'Fuerza de trabajo'),
('wood', 'Madera'),
('energy', 'Energia'),
('tools', 'Herramientas'),
('iron', 'Hierro');


insert into core.colony_building_types(id, name, buildable, repeatable) values
('imperial capital', 'Capital imperial', false, false),
('colony base', 'Base colonial', false, false),
('shipyard', 'Astillero espacial', true, false),
('sawmill', 'Serreria', true, true),
('houses', 'Complejo residencial', true, true),
('farm', 'Granja', true, true),
('tool factory', 'Fabrica de herramientas', true, true),
('goods factory', 'Fabrica de bienes de consumo', true, true),
('wind power plant', 'Planta de enrgia eolica', true, true),
('iron mine', 'Mina de hierro', true, true);


insert into CORE.colony_building_types_resources(building_type, resource_type, quantity) values
('imperial capital', 'energy', 20),
('imperial capital', 'work', 20),
('imperial capital', 'tools', 10),

('colony base', 'energy', 10),
('colony base', 'work', 10),
('colony base', 'tools', 5),

('wind power plant', 'energy', 5),
('wind power plant', 'work', -1),
('wind power plant', 'tools', -1),

('sawmill', 'energy', -2),
('sawmill', 'work', -1),
('sawmill', 'tools', -1),
('sawmill', 'wood', 4),

('houses', 'work', 20),
('houses', 'energy', -1),
('houses', 'food', -5),
('houses', 'goods', -1),

('farm', 'food', 6),
('farm', 'work', -4),
('farm', 'energy', -1),
('farm', 'tools', -1),

('tool factory', 'tools', 6),
('tool factory', 'work', -4),
('tool factory', 'energy', -2),
('tool factory', 'iron', -1),
('tool factory', 'wood', -3),

('goods factory', 'goods', 7),
('goods factory', 'work', -4),
('goods factory', 'energy', -2),
('goods factory', 'iron', -1),
('goods factory', 'wood', -1),
('goods factory', 'tools', -1),

('shipyard', 'energy', -25),
('shipyard', 'tools', -5),
('shipyard', 'work', -25),

('iron mine', 'energy', -2),
('iron mine', 'work', -2),
('iron mine', 'tools', -1),
('iron mine', 'iron', 4);

insert into CORE.colony_building_types_costs(building_type, resource_type, quantity) values
('wind power plant', 'iron', 50),
('sawmill', 'wood', 10),
('shipyard', 'iron', 100),
('iron mine', 'wood', 50);

insert into core.colony_building_capability_types(id, name) values
('build ships', 'Construir naves');

insert into core.colony_building_types_capabilities(building_type, capability_type) values
('shipyard', 'build ships');

insert into core.ship_models(name, can_colonize, can_fight) values
('Colonizador', true, false),
('Explorador', false, false);

-----------------------------------------------------------------
------------                               ----------------------
------------           TEST DATA           ----------------------
------------                               ----------------------
-----------------------------------------------------------------

select core.register('admin@galaxyvictor.com', '12345');
select core.register('otro@galaxyvictor.com', '12345');

insert into core.galaxies(name) values('Via Lactea'), ('Andromeda');

update core.users set galaxy=(select id from core.galaxies where name='Via Lactea');

with g as (select id from core.galaxies)
, i as (select generate_series as i from generate_series(0,999))
, j as (select generate_series as j from generate_series(0,2))

insert into core.star_systems(galaxy,x,y,type,size)
select 
g.id as galaxy,

(((random()*(0.1+j.j*0.1)-0.05-0.05*j.j) + i.i::float/1000.0 * cos(i.i * 5001.6 * 3.141592)) *50000) as x,
(((random()*(0.1+j.j*0.1)-0.05-0.05*j.j) + i.i::float/1000.0 * sin(i.i * 5001.6 * 3.141592)) *50000) as y,

 (random()*4+1)::integer as type,
 (random()*4+1)::integer as size 
from g cross join i cross join j;


select core.create_civilization('Humanos', 'Sol', (select id from core.sessions limit 1));
select core.create_civilization('Klingons', 'Kronos', (select id from core.sessions limit 1 offset 1));