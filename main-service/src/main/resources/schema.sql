DROP TABLE IF EXISTS public.users;

DROP SEQUENCE IF EXISTS USER_ID_SEQ;
CREATE SEQUENCE IF NOT EXISTS USER_ID_SEQ START WITH 1;

CREATE TABLE IF NOT EXISTS public.USERS
(
    ID    bigint                 NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 ),
    USERNAME  character varying(256) NOT NULL,
    EMAIL character varying(256) UNIQUE NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (ID)
    );