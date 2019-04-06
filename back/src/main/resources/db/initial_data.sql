-----------------------------------------------------------------
------------                               ----------------------
------------           TEST DATA           ----------------------
------------                               ----------------------
-----------------------------------------------------------------

select core.register('admin@galaxyvictor.com', '12345');
select core.register('otro@galaxyvictor.com', '12345');

select core.create_galaxy('Via Lactea', 10, 1);
select core.create_galaxy('Andromeda', 1000, 3);

update core.users set galaxy=(select id from core.galaxies where name='Via Lactea');


select core.create_civilization('Humanos', 'Sol', (select id from core.sessions limit 1));
select core.create_civilization('Klingons', 'Kronos', (select id from core.sessions limit 1 offset 1));