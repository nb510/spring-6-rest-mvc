drop table if exists category;
drop table if exists beer_category;

CREATE TABLE category (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    description VARCHAR(50),
    version BIGINT DEFAULT NULL,
    created_date timestamp,
    last_modified_date DATETIME(6) DEFAULT NULL
) ENGINE=InnoDB;

CREATE TABLE beer_category (
    beer_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (beer_id, category_id),
    CONSTRAINT pc_beer_id_fk FOREIGN KEY (beer_id) REFERENCES beer(id),
    CONSTRAINT pc_category_id_fk FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB;

