create table public.usr (
    id int8 not null,
    name varchar(70) not null,
    email varchar(255),
    password varchar(255) not null,
    phone_number character varying(13),
    location character varying,
    photo varchar(255),
    active boolean not null,
    primary key (id)
);

create table public.email_code (
    id int8 not null,
    email varchar(255) not null,
    activation_code varchar(255) unique,
    timestamp timestamp,
    user_id int8,
    primary key (id)
);

create table public.user_role (
    user_id int8 not null,
    roles varchar(10) not null
);

create table public.advert (
    id int8 not null,
    headline varchar(70) not null,
    description varchar(9000),
    price_max int4 not null,
    current_price float4 not null,
    price_min int4,
    location varchar not null,
    phone_number varchar(13) not null,
    contact_person varchar(70) not null,
    user_id int8 not null,
    timestamp timestamp,
    primary key (id)
);

create table public.photo (
    id int8 not null,
    advert_id int8 not null,
    filename varchar(255) not null unique,
    number integer not null,
    primary key (id)
);

create sequence advert_sequences start 1 increment 1;
create sequence photo_sequences start 1 increment 1;
create sequence user_sequences start 2 increment 1;
create sequence code_sequences start 1 increment 1;

alter table if exists public.user_role
    add constraint user_role_user_fk
    foreign key (user_id) references public.usr (id);

alter table if exists public.email_code
    add constraint email_code_user_fk
    foreign key (user_id) references public.usr (id);

alter table if exists public.advert
    add constraint advert_user_fk
    foreign key (user_id) references public.usr (id);

alter table if exists public.photo
    add constraint photo_advert_fk
    foreign key (advert_id) references public.advert (id);
