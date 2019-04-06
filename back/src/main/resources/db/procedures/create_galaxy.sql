CREATE OR REPLACE FUNCTION core.create_galaxy(name_ text, stars_per_iteration_ integer, iterations_ integer)
 RETURNS bigint
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  galaxy_id_ bigint;
begin

  insert into core.galaxies(name) values(name_)returning id into galaxy_id_;


with i as (select generate_series as i from generate_series(0,stars_per_iteration_-1))
, j as (select generate_series as j from generate_series(0,iterations_-1))

insert into core.star_systems(galaxy,x,y,type,size)
select 
galaxy_id_,

(((random()*(0.1+j.j*0.1)-0.05-0.05*j.j) + i.i::float/1000.0 * cos(i.i * 5001.6 * 3.141592)) *50000) as x,
(((random()*(0.1+j.j*0.1)-0.05-0.05*j.j) + i.i::float/1000.0 * sin(i.i * 5001.6 * 3.141592)) *50000) as y,

 (random()*4+1)::integer as type,
 (random()*4+1)::integer as size 
from i cross join j;



  return galaxy_id_;
end;$function$;