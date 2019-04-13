-----------------------------------------------------------------
------------                               ----------------------
------------           TECHNOLOGY          ----------------------
------------                               ----------------------
-----------------------------------------------------------------


insert into core.technologies(id, level, name) values
('fusion', 1, 'Fusión nuclear'),
('hypermagnetism', 1, 'Hipermagnetismo'),
('cold fusion', 2, 'Fusión fria');

insert into core.technologies_prerequisites(technology, prerequisite) values
('cold fusion', 'hypermagnetism'),
('cold fusion', 'fusion');

-----------------------------------------------------------------
------------                               ----------------------
------------     BUILDINGS AND RESOURCES   ----------------------
------------                               ----------------------
-----------------------------------------------------------------

insert into core.resource_types(id, name, merchantable) values
('goods', 'Bienes de consumo', true),
('food', 'Alimentos', true),
('work', 'Fuerza de trabajo', false),
('wood', 'Madera', true),
('energy', 'Energia', false),
('tools', 'Herramientas', true),
('iron', 'Hierro', true);


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

insert into core.colony_building_types_prerequisites(building_type, prerequisite) values
('shipyard', 'cold fusion');

insert into CORE.colony_building_types_resources(building_type, resource_type, quantity) values
('imperial capital', 'energy', 20),
('imperial capital', 'work', 20),

('colony base', 'energy', 10),
('colony base', 'work', 20),

('wind power plant', 'energy', 5),
('wind power plant', 'work', -1),

('sawmill', 'energy', -2),
('sawmill', 'work', -1),
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
------------   BUILDING PLANET PROPERTIES  ----------------------
------------                               ----------------------
-----------------------------------------------------------------

insert into core.planet_property_qualifiers(id, name) values
(0, 'nothing'),
(1, 'very low'),
(2, 'low'),
(3, 'normal'),
(4, 'abundant'),
(5, 'high'),
(6, 'very high');

insert into core.planet_property_types(id, name) values
('water', 'Agua'),
('iron', 'Hierro'),
('atmosphere', 'Atmósfera');

insert into core.colony_building_types_planet_properties(building_type, property, min_value, max_value) values
('farm', 'water', 2, 6),
('iron mine', 'iron', 2, 6);