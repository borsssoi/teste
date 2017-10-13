alter table RACE
    add column (  ENERGIA_INICIAL varchar(10), 
                 ENERGIA_FINAL varchar(10) );


update APPLICATION_STATUS
    set schema_version = 37
    where id = 1;