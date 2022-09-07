create table if not exists Post (
    id serial primary key,
    name text,
    description text,
    link varchar(255) unique,
    created timestamp
);