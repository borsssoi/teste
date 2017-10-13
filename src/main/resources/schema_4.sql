-- Creating the new wear_plan table
create table if not exists TECH_DIRECTOR (
    id integer not null,
    datetime timestamp,
    number integer,
    name varchar(32),
    nationality varchar(255),
    gps integer,
    trophies integer,
    wins integer,
    contract integer,
    salary integer,
    points_bonus integer,
    podium_bonus integer,
    win_bonus integer,
    trophy_bonus integer,
    overall integer,
    leadership integer,
    rd_mech integer,
    rd_elect integer,
    rd_aero integer,
    experience integer,
    pit_coord integer,
    motivation integer,
    age integer,
    primary key (id)
);

alter table RACE 
    add column( td_start_id integer, td_finish_id integer,
                tyre_at_start varchar(32), tyre_when_wet varchar(32), tyre_when_dry varchar(32),
                wait_pit_wet integer, wait_pit_dry integer );
                
alter table TRACK
    add column ( setup_wings integer,
                 setup_engine integer, 
                 setup_brakes integer, 
                 setup_gear integer, 
                 setup_suspension integer,
                 wing_split integer,
                 wing_normal boolean );
                

update APPLICATION_STATUS
    set schema_version = 4
    where id = 1;

