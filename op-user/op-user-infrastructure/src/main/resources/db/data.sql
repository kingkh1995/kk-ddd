DELETE
FROM account;

INSERT INTO account (user_id, state, create_time, update_time)
VALUES (1, 'INIT', '2002-02-02 02:02:02.123', '2002-02-02 02:02:02.123'),
       (1, 'ACTIVE', '2011-11-11 11:11:11.111', '2011-11-11 11:11:11.111');

INSERT INTO account (user_id, state)
VALUES (2, 'FROZEN'),
       (2, 'TERMINATED');

DELETE
FROM user;

INSERT INTO user (name, username, password, gender, age, email)
VALUES ('凯', 'Kai', '87c56bc9d59e16edeff921cac8779149', 'MALE', 35, 'kai@kkkop.com'),
       ('依', 'Yi', '87c56bc9d59e16edeff921cac8779149', 'FEMALE', 32, 'yi@kkkop.com'),
       ('元', 'Yuan', '87c56bc9d59e16edeff921cac8779149', 'MALE', 6, 'yuan@kkkop.com'),
       ('汝', 'Ru', '87c56bc9d59e16edeff921cac8779149', 'FEMALE', 0, 'ru@kkkop.com');