alter table RACE
    add column ( RISK_CLEAR_WET integer  );
    
update RACE
    set RISK_CLEAR_WET=RISK_CLEAR
    where RISK_CLEAR_WET is null;

update EVEREST_STATUS
    set schema_version = 5
    where id = 1;



