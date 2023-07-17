--DROP TABLE IF EXISTS users CASCADE;
--DROP TABLE IF EXISTS categories CASCADE;
--DROP TABLE IF EXISTS locations CASCADE;
--DROP TABLE IF EXISTS events CASCADE;
--DROP TABLE IF EXISTS requests CASCADE;
--DROP TABLE IF EXISTS compilations CASCADE;
--DROP TABLE IF EXISTS compilation_to_event CASCADE;
--DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    email       VARCHAR(254)    NOT NULL    UNIQUE,
    name        VARCHAR(250)    NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
     id         BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
     name       VARCHAR(250)    NOT NULL   UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
     id         BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
     lat        DOUBLE PRECISION,
     lon        DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS events (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    annotation          VARCHAR(2000),
    category_id         BIGINT          REFERENCES categories (id) ON DELETE CASCADE,
    created_on          TIMESTAMP WITHOUT TIME ZONE,
    description         VARCHAR(7000),
    event_date          TIMESTAMP WITHOUT TIME ZONE,
    initiator_id        BIGINT          REFERENCES users (id) ON DELETE CASCADE,
    location_id         BIGINT          REFERENCES locations (id) ON DELETE CASCADE,
    paid                BOOLEAN,
    participant_limit   BIGINT,
    published_on        TIMESTAMP WITHOUT TIME ZONE,
    request_moderation  BOOLEAN,
    state               VARCHAR(10),
    title               VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS requests (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    created             TIMESTAMP WITHOUT TIME ZONE,
    event_id            BIGINT          REFERENCES events (id) ON DELETE CASCADE,
    requester_id        BIGINT          REFERENCES users (id) ON DELETE CASCADE,
    status              VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    title               VARCHAR(50)     UNIQUE,
    pinned              BOOLEAN
);

CREATE TABLE IF NOT EXISTS compilation_to_event
(
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    event_id            BIGINT          REFERENCES events (id) ON DELETE CASCADE,
    compilation_id      BIGINT          REFERENCES compilations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    author_id           BIGINT          REFERENCES users (id) ON DELETE CASCADE,
    event_id            BIGINT          REFERENCES events (id) ON DELETE CASCADE,
    text                VARCHAR(2000),
    created             TIMESTAMP WITHOUT TIME ZONE
);