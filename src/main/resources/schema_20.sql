-- Creating the new forecast table
create table if not exists SPONSOR (
    id integer not null auto_increment,
    name varchar(32),
    group_name varchar(32),
    finances integer,
    expectations integer,
    patience integer,
    reputation integer,
    image integer,
    negotiation integer,
    primary key (id)
);

create table if not exists SPONSOR_STATUS (
    id integer not null auto_increment,
    sponsor_id integer,
    race_id integer, 
    status varchar(16),
    car_spot varchar(16),
    amount integer,
    duration integer,
    progress decimal(6,2),
    priority integer,
    neg_counter integer,
    races_left integer,
    q_spot varchar(16),
    q_goal varchar(16),
    q_driver varchar(16),
    q_amount varchar(16),
    q_duration varchar(16),
    primary key (id)
);

alter table SPONSOR_STATUS 
        add constraint FK107B465439200 
        foreign key (sponsor_id) 
        references SPONSOR
        on delete cascade;

alter table SPONSOR_STATUS 
        add constraint FK107B444449200 
        foreign key (race_id) 
        references RACE
        on delete cascade;

update APPLICATION_STATUS
    set schema_version = 20
    where id = 1;

