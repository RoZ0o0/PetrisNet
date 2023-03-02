CREATE TABLE IF NOT EXIST user
(
    id          int(32)   not null AUTO_INCREMENT,
    first_name  varchar(32) not null,
    last_name   varchar(32) not null,
    email       varchar(32) not null,
    password    varchar(255) not null,
    role        varchar(12)  not null,

    primary key (id)
);

INSERT INTO `user` (`id`, `first_name`, `last_name`, `email`, `password`, `role`)
VALUES (NULL, 'Marek', 'Nowak', 'mnowak@example.com', 'testest', 'ROLE_ADMIN'),
       (NULL, 'Adam', 'Kowalski', 'akowalski@example.com', 'testest', 'ROLE_USER');