DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.events;
DROP TABLE IF EXISTS public.categories;

DROP SEQUENCE IF EXISTS USER_ID_SEQ;
CREATE SEQUENCE IF NOT EXISTS USER_ID_SEQ START WITH 1;
DROP SEQUENCE IF EXISTS EVENT_ID_SEQ;
CREATE SEQUENCE IF NOT EXISTS EVENT_ID_SEQ START WITH 1;
DROP SEQUENCE IF EXISTS CAT_ID_SEQ;
CREATE SEQUENCE IF NOT EXISTS CAT_ID_SEQ START WITH 1;

CREATE TABLE IF NOT EXISTS public.USERS
(
    ID    bigint                 NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 ),
    USERNAME  character varying(256) NOT NULL,
    EMAIL character varying(256) UNIQUE NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (ID),
    CONSTRAINT EMAIL_UNIQUE UNIQUE (EMAIL)
    );
CREATE TABLE IF NOT EXISTS public.EVENTS
(
    ID    bigint                 NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 ),
    ANNOTATION  character varying(2048) NOT NULL,
    CATEGORY_ID bigint,
    CREATED_ON timestamp without time zone,
    PUBLISHED_ON timestamp without time zone,
    EVENT_DATE timestamp without time zone,
    DESCRIPTION character varying(8192) NOT NULL,
    PAID boolean NOT NULL,
    PARTICIPANT_LIMIT int NOT NULL,
    REQUEST_MODERATION boolean NOT NULL,
    TITLE character varying(128) NOT NULL,
    USER_ID bigint NOT NULL,
    LOCATION_LAT double NOT NULL,
    LOCATION_LON double NOT NULL,
    EVENT_STATE character varying(64) NOT NULL,

    CONSTRAINT event_pkey PRIMARY KEY (ID),
    CONSTRAINT event_user_fk FOREIGN KEY (USER_ID)
    REFERENCES public.USERS (ID)
                         ON UPDATE CASCADE
                         ON DELETE CASCADE,
    CONSTRAINT event_cat_fk FOREIGN KEY (CATEGORY_ID)
    REFERENCES public.CATEGORIES (ID)
    ON UPDATE CASCADE
    ON DELETE SET TO NULL
    );