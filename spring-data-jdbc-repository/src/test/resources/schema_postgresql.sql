DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS boarding_pass CASCADE;

CREATE TABLE users (
  user_name      text PRIMARY KEY,
  date_of_birth  date NOT NULL,
  reputation     integer NOT NULL,
  enabled        boolean NOT NULL
);

CREATE TABLE comments (
  id               serial PRIMARY KEY,
  user_name        text REFERENCES users,
  contents         text,
  created_time     timestamp NOT NULL,
  favourite_count  integer NOT NULL
);

CREATE TABLE boarding_pass (
  flight_no  varchar(8) NOT NULL,
  seq_no     integer NOT NULL,
  passenger  text,
  seat       char(3),
  PRIMARY KEY (flight_no, seq_no)
);
