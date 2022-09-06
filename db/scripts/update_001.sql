create table if not exists Post (
    id serial primary key,
    name text,
    description text,
    link text unique,
    created timestamp
);