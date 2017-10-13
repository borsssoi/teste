alter table RACE_STATUS
    add column ( 
        SETUP_PUB timestamp,
        TELEMETRY_PUB timestamp,
        TESTS_PUB timestamp
 );

update APPLICATION_STATUS
    set schema_version = 16
    where id = 1;
