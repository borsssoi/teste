-- schema changes for season 39
create table if not exists TYRE_SUPPLIER (
    id integer not null auto_increment,
    season_id integer,
    name varchar(32),
    dry integer,
    wet integer,
    peak integer,
    durability integer,
    warmup integer,
    cost integer,
    primary key (id)
);

alter table SEASON
    add column ( group_name varchar(62) ); 

alter table TYRE_SUPPLIER
    add constraint FK26BEF8182734 
    foreign key (season_id) 
    references SEASON;

update APPLICATION_STATUS
    set schema_version = 13
    where id = 1;
