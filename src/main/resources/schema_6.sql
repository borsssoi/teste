-- Creating the new TEST_SESSION table
create table if not exists TEST_SESSION (
    id integer not null auto_increment,
    track_id integer,
    weather varchar(32),
    temperature integer,
    humidity integer,
    laps_done integer,
    stints_done integer,
    tp_p decimal(8,2),
    tp_h decimal(8,2),
    tp_a decimal(8,2),
    rdp_p decimal(8,2),
    rdp_h decimal(8,2),
    rdp_a decimal(8,2),
    ep_p decimal(8,2),
    ep_h decimal(8,2),
    ep_a decimal(8,2),
    ccp_p decimal(8,2),
    ccp_h decimal(8,2),
    ccp_a decimal(8,2),
    current_car_id integer,
    primary key (id)
);

-- Creating the new TEST_STINT table
create table if not exists TEST_STINT (
    id integer not null auto_increment,
    test_session_id integer,
    number integer,
    laps_done integer,
    laps_planned integer,
    best_time integer,
    mean_time integer,
    settings_id integer,
    fuel_start integer,
    fuel_end integer,
    tyres_end integer,
    priority varchar(32),
    comments varchar(255),
    car_start_id integer,
    car_finish_id integer,
    primary key (id)
);

alter table TEST_SESSION 
    add constraint FK26BEF8736542 
    foreign key (current_car_id) 
    references CAR;

alter table TEST_STINT 
    add constraint FK26BEF8029342 
    foreign key (test_session_id) 
    references TEST_SESSION;

alter table TEST_STINT 
    add constraint FK26B726309342 
    foreign key (settings_id) 
    references CAR_SETTINGS;

alter table TEST_STINT 
    add constraint FK26B261936542 
    foreign key (car_start_id) 
    references CAR;

alter table TEST_STINT 
    add constraint FK26BEF1209542 
    foreign key (car_finish_id) 
    references CAR;

alter table RACE
    add column ( test_session_id integer ); 

alter table RACE 
    add constraint FK215239309342 
    foreign key (test_session_id) 
    references TEST_SESSION;

update APPLICATION_STATUS
    set schema_version = 6
    where id = 1;
