create table if not exists movie
(
    id          uuid
        constraint movie_pk primary key,
    title       varchar(255),
    description varchar(255),
    duration    numeric(21, 0)
);

create table if not exists movie_genre
(
    movie_id uuid not null,
    genres   varchar(255),
    constraint movie_genre_pk primary key (movie_id, genres),
    constraint movie_genre_movie_fk foreign key (movie_id) references movie (id)
);

create table if not exists room
(
    id       uuid
        constraint room_pk primary key,
    number   varchar(255),
    capacity integer
);

create table if not exists seat
(
    id      uuid
        constraint seat_pk primary key,
    number  varchar(255),
    room_id uuid not null
        constraint seat_room_fk references room (id)
);

create table if not exists "user"
(
    id         uuid
        constraint user_pk primary key,
    first_name varchar(255),
    last_name  varchar(255) not null,
    birth_date date,
    email      varchar(255) not null
        constraint user_email_unique unique,
    password   varchar(255) not null,
    phone      varchar(255),
    role       varchar(255) not null
);

create table if not exists projection
(
    id         uuid
        constraint projection_pk primary key,
    datetime   timestamptz,
    seat_price numeric(38, 2),
    movie_id   uuid not null
        constraint projection_movie_fk references movie (id),
    room_id    uuid not null
        constraint projection_room_fk references room (id)
);

create table if not exists reservation
(
    id            uuid
        constraint reservation_pk primary key,
    created_at    timestamptz,
    status        varchar(255),
    projection_id uuid not null
        constraint reservation_projection_fk references projection (id),
    user_id       uuid not null
        constraint reservation_user_fk references "user" (id)
);

create table if not exists reservation_seat
(
    reservation_id uuid not null,
    seat_id        uuid not null,
    constraint reservation_seat_pk primary key (reservation_id, seat_id),
    constraint reservation_seat_reservation_fk foreign key (reservation_id) references reservation (id),
    constraint reservation_seat_seat_fk foreign key (seat_id) references seat (id)
);
