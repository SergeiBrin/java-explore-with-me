DROP TABLE IF EXISTS statistics CASCADE;

CREATE TABLE IF NOT EXISTS statistics (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
app varchar(255) NOT NULL,
uri varchar(255) NOT NULL,
ip varchar(255) NOT NULL,
request_time timestamp NOT NULL
);


