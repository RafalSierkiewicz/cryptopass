CREATE TABLE articles (
    id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title varchar(120) NOT NULL UNIQUE,
    body text NOT NULL
);