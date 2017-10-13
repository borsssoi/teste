create table if not exists FACILITIES (
    id integer not null auto_increment,
    overall integer,
    experience integer, 
    motivation integer, 
    technical integer, 
    stress integer, 
    concentration integer, 
    efficiency integer, 
    windtunnel integer, 
    pitstop integer, 
    workshop integer, 
    design integer, 
    engineering integer, 
    alloy integer, 
    commercial integer, 
    mlt integer, 
    salary integer,
    maintenance integer,
    primary key (id)
);

alter table RACE
    add column ( riskClearWet integer, 
                 facilities_id integer );

alter table RACE 
        add constraint FK65477542AF31 
        foreign key (facilities_id) 
        references FACILITIES
        on delete cascade;

update RACE
    set riskClearWet=riskClear
    where riskClearWet is null;

update APPLICATION_STATUS
    set schema_version = 24
    where id = 1;
    
