DELETE
FROM account;

INSERT INTO account (user_id, status, create_time)
VALUES (1, 'INIT', '2002-02-02 02:02:02'),
       (1, 'ACTIVE', '2011-11-11 11:11:11');

DELETE
FROM user;

INSERT INTO user (name, username, password, gender, age, email)
VALUES ('凯', 'Kai', '87c56bc9d59e16edeff921cac8779149', 'MALE', 26, 'kai@kkkop.com');