-- The overall application status table
create table APPLICATION_STATUS (
    id integer not null,
    schema_version integer,
    current_season integer,
    next_race integer,
    last_download timestamp,
    primary key (id)
);

insert into 
    APPLICATION_STATUS ( id, schema_version, current_season, next_race, last_download ) 
    values             (  1,              1,           null,      null,          null );

-- main, standalone, entities
    create table MANAGER (
        id integer not null,
        group_name varchar(62),
        login varchar(62),
        name varchar(62),
        primary key (id)
    );

    create table TRACK (
        id integer not null,
        name varchar(255),
        -- track profile
        power integer not null,
        handling integer not null,
        acceleration integer not null,
        -- misc data
        avg_speed double not null,
        corners integer not null,
        distance double not null,
        lap_distance double not null,
        laps integer not null,
        time_in_out integer not null,
        -- characteristics
        downforce varchar(32),
        fuel_consumption varchar(32),
        grip_level varchar(32),
        overtaking varchar(32),
        suspension varchar(32),
        tyre_wear varchar(32),
        -- wear factors
        brakesWF decimal(19,5),
        chassisWF decimal(19,5),
        coolingWF decimal(19,5),
        electronicsWF decimal(19,5),
        engineWF decimal(19,5),
        front_wingWF decimal(19,5),
        gearboxWF decimal(19,5),
        rear_wingWF decimal(19,5),
        sidepodsWF decimal(19,5),
        suspensionWF decimal(19,5),
        underbodyWF decimal(19,5),
        primary key (id)
    );

    create table DRIVER_WEAR_WEIGHT (
        id integer not null,
        aggressiveness decimal(19,5),
        concentration decimal(19,5),
        experience decimal(19,5),
        stamina decimal(19,5),
        talent decimal(19,5),
        primary key (id)
    );

    create table WEAR_COEFS (
        id integer not null,
        coefs blob,
        primary key (id)
    );

-- Car related tables
    create table CAR (
        id integer not null,
        power integer not null,
        handling integer not null,
        acceleration integer not null,
        brakes_partId integer,
        chassis_partId integer,
        cooling_partId integer,
        electronics_partId integer,
        engine_partId integer,
        front_wing_partId integer,
        gearbox_partId integer,
        rear_wing_partId integer,
        sidepods_partId integer,
        suspension_partId integer,
        underbody_partId integer,
        primary key (id)
    );

    create table CAR_PART (
        id integer not null,
        level integer not null,
        name varchar(32),
        wear double not null,
        primary key (id)
    );

    create table PART_OPTION (
        id integer not null,
        part_id integer not null,
        action varchar(64),
        cost integer not null,
        new_wear double not null,
        to_level integer not null,
        primary key (id)
    );

-- Driver related tables
    create table DRIVER (
        id integer not null,
        number integer not null,
        datetime timestamp,
        age integer not null,
        aggressiveness integer not null,
        charisma integer not null,
        concentration integer not null,
        experience integer not null,
        motivation integer not null,
        overall integer not null,
        reputation integer not null,
        stamina integer not null,
        talent integer not null,
        tech_insight integer not null,
        weight integer not null,
        contract integer not null,
        fastest_laps integer not null,
        gps integer not null,
        name varchar(255),
        nationality varchar(255),
        podiums integer not null,
        points integer not null,
        poles integer not null,
        salary integer not null,
        trophies integer not null,
        wins integer not null,
        primary key (id)
    );

    create table FAVORITE_TRACK (
        driver_id integer not null,
        track_id integer not null,
        primary key(driver_id, track_id)
    );

-- Race related tables
    create table SEASON (
        id integer not null,
        number integer,
        tyre_sup varchar(32),
        primary key (id)
    );

    create table RACE (
        id integer not null,
        season_id integer,
        number integer not null,
        race_date timestamp,
        track_id integer,
        status_id integer,
        practice_id integer,
        qualify1_id integer,
        qualify2_id integer,
        race_settings_id integer,
        starting_fuel integer,
        finish_fuel integer,
        fuel_strategy varchar(64),
        finish_tyre integer,
        riskStarting varchar(64),
        riskClear integer,
        riskDefend integer,
        riskMalfunction integer,
        riskOvertake integer,
        car_start_id integer,
        car_finish_id integer,
        driver_start_id integer,
        driver_finish_id integer,
        primary key (id)
    );

    create table CAR_SETTINGS (
        id integer not null,
        brakes integer,
        engine integer,
        front_wing integer,
        gear integer,
        rear_wing integer,
        suspension integer,
        tyre varchar(32),
        primary key (id)
    );

    create table LAP (
        id integer not null,
        number integer,
        position integer,
        time integer,
        mistake integer,
        netTime integer,
        weather varchar(32),
        temperature integer,
        humidity integer,
        settings_id integer,
        comments varchar(255),
        events varchar(255),
        primary key (id)
    );

    create table PRACTICE (
        id integer not null,
        primary key (id)
    );

    create table PRACTICE_LAP (
        practice_id integer not null,
        lap_id integer not null,
        unique (lap_id),
        primary key (practice_id, lap_id)
    );

    create table QUALIFY (
        id integer not null,
        number integer not null,
        risk_descr varchar(64),
        lap_id integer,
        primary key (id)
    );

    create table RACE_STATUS (
        id integer not null,
        practice boolean not null,
        qualify1 boolean not null,
        qualify2 boolean not null,
        setup boolean not null,
        telemetry boolean not null,
        track_info boolean not null,
        primary key (id)
    );

    create table PIT (
        id integer not null,
        race_id integer not null,
        number integer,
        fuel integer,
        lap integer,
        reason varchar(64),
        refueled_to integer,
        time integer,
        tyres integer,
        primary key (id)
    );

    create table RACE_LAP (
        race_id integer not null,
        lap_id integer not null,
        unique (lap_id),
        primary key (race_id, lap_id)
    );

    create sequence SEQ_ID;

    alter table Car 
        add constraint FK107B4AF71AF3A 
        foreign key (electronics_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B435D3BE14 
        foreign key (suspension_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B420582602 
        foreign key (rear_wing_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B4B3B708E9 
        foreign key (brakes_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B4FB5D5B35 
        foreign key (gearbox_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B48C3403AB 
        foreign key (chassis_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B4FF2B0C5D 
        foreign key (front_wing_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B42487EC4C 
        foreign key (sidepods_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B47D5A480F 
        foreign key (engine_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B4AEE202D7 
        foreign key (underbody_partId) 
        references CAR_PART
        on delete cascade;

    alter table Car 
        add constraint FK107B4E728FE78 
        foreign key (cooling_partId) 
        references CAR_PART
        on delete cascade;

    alter table PART_OPTION
        add constraint FK5F7A8FE03BDBD8EA 
        foreign key (part_id) 
        references CAR_PART
        on delete cascade;

    alter table LAP 
        add constraint FK1297B1B1A3FA 
        foreign key (settings_id) 
        references CAR_SETTINGS
        on delete cascade;

    alter table PRACTICE_LAP
        add constraint FKEC81897B0CB9B49 
        foreign key (lap_id) 
        references LAP
        on delete cascade;

    alter table PRACTICE_LAP
        add constraint FKEC81897A7A1A2E 
        foreign key (practice_id) 
        references PRACTICE
        on delete cascade;

    alter table QUALIFY 
        add constraint FK8A09572D719B2D06 
        foreign key (lap_id) 
        references LAP;

    alter table RACE 
        add constraint FK26BEF16B40E41C 
        foreign key (season_id) 
        references SEASON;        

    alter table RACE 
        add constraint FK26BEF1E2815284 
        foreign key (car_finish_id) 
        references CAR;

    alter table RACE 
        add constraint FK26BEF1E0321C89 
        foreign key (race_settings_id) 
        references CAR_SETTINGS;

    alter table RACE 
        add constraint FK26BEF18DB3AB8E 
        foreign key (qualify2_id) 
        references QUALIFY;

    alter table RACE 
        add constraint FK26BEF18DB3372F 
        foreign key (qualify1_id) 
        references QUALIFY;

    alter table RACE 
        add constraint FK26BEF157C76D17 
        foreign key (driver_start_id) 
        references DRIVER;

    alter table RACE 
        add constraint FK26BEF157C76333 
        foreign key (driver_finish_id) 
        references DRIVER;

    alter table RACE 
        add constraint FK26BEF1502F5BFF 
        foreign key (status_id) 
        references RACE_STATUS;

    alter table RACE 
        add constraint FK26BEF1B6E317D 
        foreign key (car_start_id) 
        references CAR;

    alter table RACE 
        add constraint FK26BEF1E00EC4CE 
        foreign key (track_id) 
        references TRACK;

    alter table RACE 
        add constraint FK26BEF1A7A1A2E 
        foreign key (practice_id) 
        references PRACTICE;

    alter table RACE_LAP
        add constraint FK8372DB0CB9B49 
        foreign key (lap_id) 
        references LAP
        on delete cascade;

    alter table RACE_LAP 
        add constraint FK8372DFE7DF46E 
        foreign key (race_id) 
        references RACE
        on delete cascade;

    alter table PIT 
        add constraint FK8472DFE7DF46E 
        foreign key (race_id) 
        references RACE
        on delete cascade;

    alter table FAVORITE_TRACK
        add constraint FK8372DB0CB0000 
        foreign key (driver_id) 
        references DRIVER
        on delete cascade;

    alter table FAVORITE_TRACK 
        add constraint FK8372DFE7D1111 
        foreign key (track_id) 
        references TRACK
        on delete cascade;


