CREATE TABLE IF NOT EXIST saved_user_nets
(
    id          int(11)         not null AUTO_INCREMENT,
    net_name   varchar(255)    not null,
    net_export  varchar(10000)   not null

    primary key (id)
);