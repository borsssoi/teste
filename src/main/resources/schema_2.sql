-- Creating the new forecast table
create table if not exists FORECAST (
    id integer not null,
    weather varchar(32),
    temp_min integer,
    temp_max integer,
    humidity_min integer,
    humidity_max integer,
    rain_min integer,
    rain_max integer,
    primary key (id)
);

alter table RACE
    add column ( fc_q1 integer, fc_q2 integer,
                 fc_r1 integer, fc_r2 integer,
                 fc_r3 integer, fc_r4 integer ); 

alter table TRACK
    add column ( fuel_coef decimal(19,9),
                 compound_coef decimal(19,9) );

alter table RACE 
        add constraint FK107B46542AF31 
        foreign key (fc_q1) 
        references FORECAST
        on delete cascade;

alter table RACE 
        add constraint FK107B46542AF32 
        foreign key (fc_q2) 
        references FORECAST
        on delete cascade;

alter table RACE 
        add constraint FK107B46542AF33 
        foreign key (fc_r1) 
        references FORECAST
        on delete cascade;

alter table RACE 
        add constraint FK107B46542AF34 
        foreign key (fc_r2) 
        references FORECAST
        on delete cascade;

alter table RACE 
        add constraint FK107B46542AF35 
        foreign key (fc_r3) 
        references FORECAST
        on delete cascade;

alter table RACE 
        add constraint FK107B46542AF36 
        foreign key (fc_r4) 
        references FORECAST
        on delete cascade;

update APPLICATION_STATUS
    set schema_version = 2
    where id = 1;

