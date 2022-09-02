DELETE
FROM `user`;

INSERT INTO `user` (`state`, `name`, `phone`, `email`)
VALUES ('INIT', 'kai', '11012345678', 'kai@kk.com');

DELETE
FROM `user_base`;

INSERT INTO `user_base` (`id`, `gender`, `birthday`, `profile_path`)
VALUES (1, 0, '1995-05-10', 'http://kk.com/profile/123456');

DELETE
FROM `account`;

INSERT INTO `account` (`user_id`, `type`, `principal`, `unbind_time`)
VALUES (1, 'USERNAME', 'kai', NULL),
       (1, 'PHONE', '11000000000', '2002-02-02 02:02:02.123'),
       (1, 'PHONE', '11012345678', NULL),
       (1, 'EMAIL', 'kai@kk.com', NULL),
       (1, 'QQ', '123456', NULL);