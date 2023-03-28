CREATE TABLE IF NOT EXISTS user
(
    id                  int(32)         not null AUTO_INCREMENT,
    first_name          varchar(32)     not null,
    last_name           varchar(32)     not null,
    email               varchar(32)     not null,
    password            varchar(255)    not null,
    role                varchar(12)     not null,
    verification_code   varchar(64)     null,
    enabled             int(4)          not null,
    reset_token         varchar(64)     null,

    primary key (id)
    );

INSERT INTO `user` (`id`, `first_name`, `last_name`, `email`, `password`, `role`, `verification_code`, `enabled`, `reset_token`)
VALUES (NULL, 'Marek', 'Nowak', 'mnowak@example.com', '$2a$12$0bmGc4vmz2R/zP9kJuQF1utMU94kXR8kY3nKqbTwlB5rvA2Sh92Wq', 'ROLE_ADMIN', NULL, 1, NULL),
       (NULL, 'Adam', 'Kowalski', 'akowalski@example.com', '$2a$12$0bmGc4vmz2R/zP9kJuQF1utMU94kXR8kY3nKqbTwlB5rvA2Sh92Wq', 'ROLE_USER', NULL, 1, NULL);