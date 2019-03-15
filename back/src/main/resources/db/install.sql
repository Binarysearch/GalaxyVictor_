
select core.register('admin@galaxyvictor.com', '12345');

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


select core.create_civilization('Humanos', 'Sol', (select id from core.sessions));
--insert into core.planets(star_system, orbit, type, size) values(1, 3, 10, 3);
--insert into core.civilizations(galaxy, name, homeworld, usr) values(1, 'Humanos', 1, 1);
--insert into core.known_star_systems(civilization, star_system) values(1, 1);