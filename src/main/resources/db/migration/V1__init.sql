CREATE TABLE search_query (
    id          BIGSERIAL PRIMARY KEY,
    query_term  VARCHAR(255) NOT NULL UNIQUE,
    searched_at TIMESTAMP    NOT NULL
);

CREATE TABLE company (
    id              BIGSERIAL PRIMARY KEY,
    search_query_id BIGINT       NOT NULL REFERENCES search_query (id),
    company_number  VARCHAR(50)  NOT NULL,
    name            VARCHAR(255),
    status          VARCHAR(100),
    company_type    VARCHAR(100),
    incorporated_on DATE,
    address         TEXT
);

CREATE TABLE officer (
    id             BIGSERIAL PRIMARY KEY,
    company_id     BIGINT       NOT NULL REFERENCES company (id),
    name           VARCHAR(255),
    role           VARCHAR(100),
    appointed_on   DATE
);

CREATE TABLE person_with_significant_control (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT       NOT NULL REFERENCES company (id),
    name            VARCHAR(255),
    nature_of_control TEXT
);