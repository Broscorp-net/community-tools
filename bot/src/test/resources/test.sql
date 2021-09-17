DROP TABLE IF EXISTS state_entity_test;

CREATE TABLE if not exists state_entity_test(
    user_id varchar(30),
    git_name varchar(30),
    state_machine bytea,
    PRIMARY KEY (user_id)
);

INSERT into  state_entity_test values ('Yura', 'testName', null);
