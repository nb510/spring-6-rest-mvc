drop table if exists beer_audit;

CREATE TABLE beer_audit
(
        audit_id varchar(36) NOT NULL PRIMARY KEY,
        created_date_audit TIMESTAMP,
        audit_event_type varchar(255),
        principal_name varchar(255),
        id varchar(36),
        beer_name varchar(50),
        beer_style smallint,
        created_date datetime(6),
        price decimal(38, 2),
        quantity_on_hand integer,
        upc varchar(255),
        update_date datetime(6),
        VERSION integer
) ENGINE=InnoDB;
