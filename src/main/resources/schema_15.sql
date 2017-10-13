-- schema changes for season 39
alter table TRACK
    add column ( 
        F_CON integer,
        F_AGR integer,
        F_EXP integer,
        F_TEI integer,
        F_ENG integer,
        F_ELE integer,
        F_HUM decimal(19,5),
        F_FUE decimal(19,5)
 );

update APPLICATION_STATUS
    set schema_version = 15
    where id = 1;
