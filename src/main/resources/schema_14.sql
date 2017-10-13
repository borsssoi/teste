-- schema changes for season 39

alter table SEASON
    add column ( manager_name varchar(62) );

create index on SEASON ( manager_name, number ); 

update season 
   set MANAGER_NAME = (select name from MANAGER where id = 1);

alter table TYRE_SUPPLIER
  add column( season_number integer );

update tyre_supplier t1
   set season_number = (select number from season s join tyre_supplier t on t.season_id = s.id where t.id = t1.id ); 

alter table TYRE_SUPPLIER
 drop column season_id ;

update APPLICATION_STATUS
    set schema_version = 14
    where id = 1;
