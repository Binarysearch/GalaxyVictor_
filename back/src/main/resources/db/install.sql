
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