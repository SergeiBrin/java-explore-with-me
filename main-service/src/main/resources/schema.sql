DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;

CREATE TABLE IF NOT EXISTS categories (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(50) NOT NULL,
CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(250) NOT NULL,
email varchar(254) NOT NULL,
private_account boolean NOT NULL,
CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS subscriptions (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
owner BIGINT NOT NULL REFERENCES users(id),
subscriber BIGINT NOT NULL REFERENCES users(id),
status varchar(10) NOT NULL,
UNIQUE (owner, subscriber)
);

CREATE TABLE IF NOT EXISTS locations (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
lat float NOT NULL,
lon float NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pinned boolean,
title varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
initiator BIGINT NOT NULL,
category BIGINT NOT NULL,
location BIGINT NOT NULL,
compilation BIGINT,
annotation varchar(2000),
created_on timestamp,
description varchar(7000),
event_date timestamp,
paid boolean,
participant_limit int NOT NULL,
request_moderation boolean,
state varchar(255),
published_on timestamp,
title varchar(120),
CONSTRAINT fk_initiator FOREIGN KEY (initiator) REFERENCES users(id),
CONSTRAINT fk_category FOREIGN KEY (category) REFERENCES categories(id) ON DELETE RESTRICT, -- Категорию удалить нельзя, если она здесь используется
CONSTRAINT fk_location FOREIGN KEY (location) REFERENCES locations(id),
CONSTRAINT fk_compilation FOREIGN KEY (compilation) REFERENCES compilations(id)
);

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
event BIGINT NOT NULL REFERENCES events(id),
requester BIGINT NOT NULL REFERENCES users(id),
created timestamp,
status varchar(255),
UNIQUE (event, requester)
);





