CREATE TABLE public.favorites(
    id int8 NOT NULL,
    user_id int8 NOT NULL,
    advert_id int8 NOT NULL,
    PRIMARY KEY (id),

CONSTRAINT user_favorites_fk FOREIGN KEY (user_id)
        REFERENCES public.usr (id),
CONSTRAINT advert_favorites_fk FOREIGN KEY (advert_id)
        REFERENCES public.advert (id)
);

create sequence favorites_sequences start 1 increment 1;