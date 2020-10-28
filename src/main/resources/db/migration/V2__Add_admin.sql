insert into public.usr (id, name, email, password, active)
    values (1, 'Roman', 'admin', 'password', true);

insert into public.user_role (user_id, roles)
    values (1, 'USER'), (1, 'ADMIN');

create extension if not exists pgcrypto;

update usr set password = crypt(password, gen_salt('bf', 10));