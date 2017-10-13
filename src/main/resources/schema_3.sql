-- Creating the new wear_plan table
create table if not exists WEAR_PLAN (
    id integer not null,
    name varchar(32),
    season integer,
    race integer,
    plan varchar(65535),
    primary key (id)
);

update APPLICATION_STATUS
    set schema_version = 3
    where id = 1;

