create schema if not exists ippo;
create extension if not exists "uuid-ossp";

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
    lecturer_max_hours_for_rate decimal(19, 5) not null default 900,
    constraint pk_staffing_table primary key (id),
    constraint fk_staffing_table_lecturer_type
        foreign key (lecturer_type_id)
            references ippo.lecturer_type (id)
            on delete restrict
            on update restrict
);

create table ippo.hours_type
(
    type        varchar not null,
    divide_type varchar not null,
    hours       decimal(19, 5),

    constraint pk_hours_type primary key (type)
);

create table ippo.curriculum
(
    id                  uuid    not null,
    field_of_study      varchar not null,
    educational_profile varchar not null,
    start_year          integer not null,
    course              integer not null,
    subject             varchar not null,
    hours               decimal(19, 5),
    hours_type          varchar not null,

    constraint pk_curriculum primary key (id),
    constraint fk_curriculum_hours_type
        foreign key (hours_type)
            references ippo.hours_type (type)
            on delete restrict
            on update restrict

);

create table ippo.group
(
    code                varchar not null,
    students_number     integer not null,
    field_of_study      varchar not null,
    educational_profile varchar not null,
    start_year          integer not null,

    constraint pk_group primary key (code)
);



create table ippo.stream
(
    id                  uuid    not null,
    type                varchar not null,
    subject             varchar not null,
    field_of_study      varchar not null,
    educational_profile varchar not null,
    start_year          integer not null,

    constraint pk_stream primary key (id)
);

create table ippo.group_stream
(
    stream_id  uuid    not null,
    group_code varchar not null,
    constraint pk_stream_group primary key (stream_id, group_code)
);

create table ippo.hours
(
    id         uuid           not null,
    subject    varchar        not null,
    group_code varchar,
    stream_id  uuid,
    hours_type varchar        not null,
    hours      decimal(19, 5) not null,

    constraint pk_hours primary key (id)
);

create function update_hours_due_to_groups() returns trigger
    language plpgsql
as
$$
declare
    course_tmp          integer;
    subject_tmp         varchar;
    hours_tmp           decimal(19, 5);
    hours_type_tmp      varchar;
    divide_type_tmp     varchar;
    type_hours_tmp      decimal(19, 5);
    students_number_tmp integer;
begin
    if TG_OP = 'INSERT' then
        for course_tmp,subject_tmp,hours_tmp,hours_type_tmp in select course, subject, hours, hours_type
                                                               from ippo.curriculum
                                                               where field_of_study = new.field_of_study
                                                                 and start_year = new.start_year
                                                                 and educational_profile = new.educational_profile
            loop
                select divide_type, hours into divide_type_tmp, type_hours_tmp
                from ippo.hours_type
                where type = hours_type_tmp;
                if divide_type_tmp = 'BY_GROUP' then
                    insert into ippo.hours
                    values (uuid_generate_v4(), subject_tmp, new.code, null, hours_type_tmp, hours_tmp);
                end if;
                if divide_type_tmp = 'BY_STUDENT' then
                    select students_number into students_number_tmp from ippo.group where code = new.code;
                    insert into ippo.hours
                    values (uuid_generate_v4(), subject_tmp, new.code, null, hours_type_tmp,
                            students_number_tmp * type_hours_tmp);
                end if;
            end loop;
        return new;
    elseif TG_OP = 'UPDATE' then
        for course_tmp,subject_tmp,hours_tmp,hours_type_tmp in select course, subject, hours, hours_type
                                                               from curriculum
                                                               where educational_profile = new.educational_profile
                                                                 and start_year = new.start_year
                                                                 and educational_profile = new.educational_profile
            loop
                select divide_type, hours into divide_type_tmp,hours_tmp
                from ippo.hours_type
                where type = hours_type_tmp;
                if divide_type_tmp = 'BY_STUDENT' then
                    select students_number into students_number_tmp from ippo.group where code = new.code;
                    update ippo.hours set hours = students_number_tmp * hours_tmp where group_code = new.code and hours_type = hours_type_tmp;
                end if;
            end loop;
        return new;
    ELSIF TG_OP = 'DELETE' THEN
        delete from ippo.hours where group_code = old.code;
        delete from group_stream where group_code = old.code;
        return old;
    END IF;
END;
$$;


create function update_hours_due_to_streams() returns trigger
    language plpgsql
as
$$
declare
    course_tmp      integer;
    subject_tmp     varchar;
    hours_tmp       decimal(19, 5);
    hours_type_tmp  varchar;
    divide_type_tmp varchar;
    type_hours_tmp  decimal(19, 5);
begin
    if TG_OP = 'INSERT' then
        for course_tmp,subject_tmp,hours_tmp,hours_type_tmp in select course, subject, hours, hours_type
                                                               from curriculum
                                                               where field_of_study = new.field_of_study
                                                                 and start_year = new.start_year
                                                                 and educational_profile = new.educational_profile
                                                                 and subject = new.subject
            loop
                select divide_type, hours into divide_type_tmp, type_hours_tmp from hours_type where type = hours_type_tmp;
                if divide_type_tmp = 'BY_STREAM' then
                    insert into ippo.hours
                    values (uuid_generate_v4(), subject_tmp, null, new.id, hours_type_tmp, hours_tmp);
                end if;
            end loop;
        return new;
    ELSIF TG_OP = 'DELETE' THEN
        delete from ippo.hours where stream_id = old.id;
        delete from group_stream where stream_id = old.id;
        return old;
    END IF;
END;
$$;

CREATE TRIGGER t_group
    AFTER INSERT OR UPDATE OR DELETE
    ON ippo.group
    FOR EACH ROW
EXECUTE PROCEDURE update_hours_due_to_groups();

CREATE TRIGGER t_stream
    AFTER INSERT OR UPDATE OR DELETE
    ON ippo.stream
    FOR EACH ROW
EXECUTE PROCEDURE update_hours_due_to_streams();


