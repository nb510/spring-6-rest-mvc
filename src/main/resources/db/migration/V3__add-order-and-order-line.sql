CREATE TABLE beer_order (
    id VARCHAR(36) NOT NULL,
    customer_ref VARCHAR(255),
    customer_id VARCHAR(36),
    version BIGINT,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB;

CREATE TABLE beer_order_line (
    id VARCHAR(36) NOT NULL,
    beer_id VARCHAR(36),
    beer_order_id VARCHAR(36),
    order_quantity INT,
    quantity_allocated INT,
    version BIGINT,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (beer_order_id) REFERENCES beer_order(id)
) ENGINE=InnoDB;