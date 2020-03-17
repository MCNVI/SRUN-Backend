create schema if not exists ippo;

create table ippo.lecturer_type
(
    id           uuid    not null,
    type         varchar not null,
    hours        integer not null,
    is_part_time boolean not null,
    is_external  boolean,
    constraint pk_lecturer_type primary key (id)
);


create table ippo.staffing_table
(
    id                          uuid           not null,
    lecturer_type_id            uuid           not null,
    name                        varchar        not null,
    middle_name                 varchar        not null,
    last_name                   varchar        not null,
    lecturer_rate               decimal(19, 5) not null,
    lecturer_hours_for_rate     decimal(19, 5) not null,
    lecturer_max_hours_for_rate decimal(19, 5) not null,
    constraint pk_staffing_table primary key (id),
    constraint fk_staffing_table_lecturer_type
        foreign key (lecturer_type_id)
            references ippo.lecturer_type (id)
            on delete restrict
            on update restrict
);