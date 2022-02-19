CREATE TABLE public.users (
    id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    salt varchar(32) NOT NULL,
    username varchar(64) NOT NULL,
    email varchar(254) NOT NULL UNIQUE,
    password text NOT NULL
);

CREATE INDEX user_email_idx ON users(email);