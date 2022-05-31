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
VALUES ('凯', 'kai', 'h8VrydWeFu3v+SHKyHeRSQ==', 'MALE', 35, 'kai@kk.com'),
       ('依', 'yi', '6Xv1dL/gm63tBNdBYM/ZgQ==', 'FEMALE', 32, 'yi@kk.com'),
       ('元', 'yuan', '3xb9TLoGR+fi+y9mnXsUpQ==', 'MALE', 6, 'yuan@kk.com'),
       ('汝', 'ru', 'uwmhQe0KmIPnBKNi32FlKw==', 'FEMALE', 0, 'ru@kk.com');