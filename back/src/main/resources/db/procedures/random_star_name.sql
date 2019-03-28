CREATE OR REPLACE FUNCTION core.random_star_name()
 RETURNS text
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  chars1 text[] := ARRAY['a','e','i','o','u','','','','','','','','','','','','','',''];
  chars2 text[] := ARRAY['b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z','br','cr','dr','gr','kr','pr','sr','tr','str','vr','zr','bl','cl','fl','gl','kl','pl','sl','vl','zl','ch','sh','ph','th'];
  chars3 text[] := ARRAY['a','e','i','o','u','a','e','i','o','u','a','e','i','o','u','ae','ai','ao','au','aa','ea','ei','eo','eu','ee','ia','io','iu','oa','oi','oo','ua','ue'];
  chars4 text[] := ARRAY['b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z','br','cr','dr','gr','kr','pr','sr','tr','str','vr','zr','bl','cl','fl','hl','gl','kl','ml','nl','pl','sl','tl','vl','zl','ch','sh','ph','th','bd','cd','gd','kd','ld','md','nd','pd','rd','sd','zd','bs','cs','ds','gs','ks','ls','ms','ns','ps','rs','ts','ct','gt','lt','nt','st','rt','zt','bb','cc','dd','gg','kk','ll','mm','nn','pp','rr','ss','tt','zz'];
  chars5 text[] := ARRAY['','','','','','','','','','','','','','b','c','d','f','g','h','k','l','m','n','p','r','s','t','x','y','b','c','d','f','g','h','k','l','m','n','p','r','s','t','x','y','cs','ks','ls','ms','ns','ps','rs','ts','ys','ct','ft','kt','lt','nt','ph','sh','th'];
  second_names text[] := ARRAY[' OYVEY', ' Gadol', ' Mishugue', ' Pirisea', ' Laika', ' YV29', ' Eridani', ' Cassiopea', ' Scorpi', ' Crucis', ' Cancri', ' Leonis', ' Tauri', ' Cygni', ' Capricorni', ' Cephei', ' Majoris', ' Columbae', ' Gruis', ' Aquilae', ' Lyrae', ' Sagittari',  ' Aquari', ' Corvi', ' Andromeda', ' Australis', ' Persei', ' Pegasi', ' Aurigae', ' Geminorum', ' Minoris', ' Crateris', ' Orionis', ' Borealis', ' Pollux'];
begin

  if (random() > 0.5) then
    return 
    INITCAP(chars1[(1+random()*(array_length(chars1, 1)-1))] ||
    chars2[(1+random()*(array_length(chars2, 1)-1))] ||
    chars3[(1+random()*(array_length(chars3, 1)-1))] ||
    chars5[(1+random()*(array_length(chars5, 1)-1))] ||
    second_names[(1+random()*(array_length(second_names, 1)-1))]);
  else
    return 
    INITCAP(chars1[(1+random()*(array_length(chars1, 1)-1))] ||
    chars2[(1+random()*(array_length(chars2, 1)-1))] ||
    chars3[(1+random()*(array_length(chars3, 1)-1))] ||
    chars4[(1+random()*(array_length(chars4, 1)-1))] ||
    chars3[(1+random()*(array_length(chars3, 1)-1))] ||
    chars5[(1+random()*(array_length(chars5, 1)-1))] ||
    second_names[(1+random()*(array_length(second_names, 1)-1))]);
  end if;

end;$function$;