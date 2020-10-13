create table user
(
    id        int                                       not null auto_increment primary key,
    username  varchar(50)  default ''                   not null,
    password  varchar(128) default ''                   not null,
    createdAt datetime(3)  default CURRENT_TIMESTAMP(3) not null,
    updatedAt datetime(3)  default CURRENT_TIMESTAMP(3) not null,
    constraint u_idx_username unique (username)
)