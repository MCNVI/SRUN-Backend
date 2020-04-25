create schema if not exists ippo;
create extension if not exists "uuid-ossp";

create function getLecturerTypeLoad(id_t uuid) returns decimal(19, 5)
    language plpgsql
as
$$
declare
    load decimal(19, 5);
begin
    select study_load into load from lecturer_type where id = id_t;
    return load;
end;
$$;

create table ippo.lecturer_type
(
    id           uuid    not null,
    type         varchar not null,
    study_load   integer not null,
    is_part_time boolean not null,
    is_external  boolean,
    constraint pk_lecturer_type primary key (id)
);


create table ippo.staffing_table
(
    id                         uuid           not null,
    lecturer_type_id           uuid           not null,
    name                       varchar        not null,
    middle_name                varchar        not null,
    last_name                  varchar        not null,
    lecturer_rate              decimal(19, 5) not null,
    month_amount               decimal(19, 5) not null,
    employment_start_date      timestamptz    not null,
    employment_finish_date     timestamptz    not null,
    lecturer_load_for_rate     decimal(19, 5) not null,
    lecturer_max_load_for_rate decimal(19, 5) not null default 900,
    constraint pk_staffing_table primary key (id),
    constraint fk_staffing_table_lecturer_type
        foreign key (lecturer_type_id)
            references ippo.lecturer_type (id)
            on delete restrict
            on update restrict
);

create table ippo.load_type
(
    type          varchar not null,
    division_type varchar not null,
    type_load     decimal(19, 5),

    constraint pk_load_type primary key (type)
);

create table ippo.curriculum
(
    id                  uuid    not null,
    field_of_study      varchar not null,
    educational_profile varchar not null,
    start_year          integer not null,

    constraint pk_curriculum primary key (id)
);

create table ippo.curriculum_unit
(
    id            uuid    not null,
    course        integer not null,
    semester      integer not null,
    subject       varchar not null,
    load          decimal(19, 5),
    load_type     varchar not null,
    curriculum_id uuid    not null,

    constraint pk_curriculum_unit primary key (id),
    constraint fk_curriculum_load_type
        foreign key (load_type)
            references ippo.load_type (type)
            on delete restrict
            on update restrict,
    constraint fk_curriculum_unit_curriculum
        foreign key (curriculum_id)
            references ippo.curriculum (id)
            on delete cascade
            on update cascade
);

create table ippo.group
(
    code            varchar not null,
    students_number integer not null,
    curriculum_id   uuid    not null,
    course          integer not null,

    constraint pk_group primary key (code),
    constraint fk_group_curriculum
        foreign key (curriculum_id)
            references ippo.curriculum (id)
            on delete restrict
            on update restrict
);



create table ippo.stream
(
    id            uuid    not null,
    type          varchar not null,
    subject       varchar not null,
    course        integer not null,
    curriculum_id uuid    not null,

    constraint pk_stream primary key (id),
    constraint fk_stream_curriculum
        foreign key (curriculum_id)
            references ippo.curriculum (id)
            on delete restrict
            on update restrict
);

create table ippo.group_stream
(
    stream_id  uuid    not null,
    group_code varchar not null,
    constraint pk_stream_group primary key (stream_id, group_code)
);

create table ippo.load
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
    course_t            integer;
    subject_t           varchar;
    load_t              decimal(19, 5);
    load_type_t         varchar;
    division_type_t     varchar;
    type_hours_tmp      decimal(19, 5);
    students_number_tmp integer;
begin
    if TG_OP = 'INSERT' then
        for course_t,subject_t,load_t,load_type_t in select course, subject, load, load_type
                                                     from ippo.curriculum_unit
                                                     where curriculum_id = new.curriculum_id
                                                       and course = new.course
            loop
                select division_type, type_load
                into division_type_t, type_hours_tmp
                from ippo.load_type
                where type = load_type_t;
                if division_type_t = 'BY_GROUP' then
                    insert into ippo.load
                    values (uuid_generate_v4(), subject_t, new.code, null, load_type_t, load_t);
                end if;
                if division_type_t = 'BY_STUDENT' then
                    select students_number into students_number_tmp from ippo.group where code = new.code;
                    insert into ippo.load
                    values (uuid_generate_v4(), subject_t, new.code, null, load_type_t,
                            students_number_tmp * type_hours_tmp);
                end if;
            end loop;
        return new;
    elseif TG_OP = 'UPDATE' then
        for course_t,subject_t,load_t,load_type_t in select course, subject, load, load_type
                                                     from curriculum_unit
                                                     where curriculum_id = new.curriculum_id
                                                       and course = new.course
            loop
                select division_type, type_load
                into division_type_t,load_t
                from ippo.load_type
                where type = load_type_t;
                if division_type_t = 'BY_STUDENT' then
                    select students_number into students_number_tmp from ippo.group where code = new.code;
                    update ippo.load
                    set hours = students_number_tmp * load_t
                    where group_code = new.code
                      and hours_type = load_type_t;
                end if;
            end loop;
        return new;
    ELSIF TG_OP = 'DELETE' THEN
        delete from ippo.load where group_code = old.code;
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
    course_t        integer;
    subject_t       varchar;
    load_t          decimal(19, 5);
    load_type_t     varchar;
    division_type_t varchar;
    type_hours_tmp  decimal(19, 5);
begin
    if TG_OP = 'INSERT' then
        for course_t,subject_t,load_t,load_type_t in select course, subject, load, load_type
                                                     from curriculum_unit
                                                     where curriculum_id = new.curriculum_id
                                                       and subject = new.subject
                                                       and course = new.course
            loop
                select division_type, type_load
                into division_type_t, type_hours_tmp
                from load_type
                where type = load_type_t;
                if division_type_t = 'BY_STREAM' then
                    insert into ippo.load
                    values (uuid_generate_v4(), subject_t, null, new.id, load_type_t, load_t);
                end if;
            end loop;
        return new;
    ELSIF TG_OP = 'DELETE' THEN
        delete from ippo.load where stream_id = old.id;
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

INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('LECTURE', 'BY_STREAM', null);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('PRACTICAL_CLASS', 'BY_GROUP', null);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('LABORATORY_WORK', 'BY_GROUP', null);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('EXAM', 'BY_STUDENT', 0.35000);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('TEST', 'BY_STUDENT', 0.25000);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('COURSEWORK', 'BY_STUDENT', 2.00000);
INSERT INTO ippo.load_type (type, division_type, type_load)
VALUES ('COURSE_PROJECT', 'BY_STUDENT', 3.00000);

create table ippo.load_distribution
(
    id           uuid           not null,
    load_unit_id uuid           not null,
    lecturer_id  uuid           not null,
    load_part    decimal(19, 5) not null,


    constraint pk_load_distribution primary key (id),
    constraint fk_load_distribution_load_unit
        foreign key (load_unit_id)
            references ippo.load (id)
            on delete cascade
            on update cascade,
    constraint fk_load_distribution_lecturer
        foreign key (lecturer_id)
            references ippo.staffing_table (id)
            on delete cascade
            on update cascade
);
