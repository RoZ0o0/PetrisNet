CREATE TABLE IF NOT EXIST saved_user_nets
(
    id          int(11)         not null AUTO_INCREMENT,
    user_id     int(11)         not null,
    save_name   varchar(255)    not null,
    net_export  varchar(10000)   not null,
    is_public   int(4)          not null,
    ref_link    varchar(255)    null,

    primary key (id),
    CONSTRAINT savedUserNets_user_fk FOREIGN KEY (user_id) REFERENCES user (id)
);