create table if not exists users (
    user_id bigint generated always as identity primary key,
    user_name varchar(250) not null,
    email varchar(254) not null unique
);

create table if not exists categories (
    category_id bigint generated always as identity primary key,
    category_name varchar(50) not null unique
);

create table if not exists events (
    event_id bigint generated always as identity primary key,
    event_name varchar(120) not null,
    annotation varchar(2000) not null,
    description varchar(7000) not null,
    event_date timestamp not null,
    location_lat float not null,
    location_lon float not null,
    paid boolean not null,
    participant_limit int not null,
    request_moderation boolean not null,
    state varchar(9) not null,
    created_on timestamp not null,
    published_on timestamp,
    initiator_id bigint not null,
    category_id bigint not null,
    confirmed_requests int not null,

    constraint events_users foreign key (initiator_id) references users (user_id) on delete cascade on update cascade,
    constraint events_categories foreign key (category_id) references categories (category_id) on delete restrict on update restrict
);

create table if not exists event_participation_requests (
    event_participation_request_id bigint generated always as identity primary key,
    created timestamp not null,
    status varchar(9) not null,
    event_id bigint not null,
    requester_id bigint not null,

    constraint event_participation_requests_events foreign key (event_id) references events (event_id) on delete cascade on update cascade,
    constraint event_participation_requests_users foreign key (requester_id) references users (user_id) on delete cascade on update cascade
);

create table if not exists compilations (
    compilation_id bigint generated always as identity primary key,
    compilation_name varchar(50) not null,
    pinned boolean
);

create table if not exists compilations_events (
    compilation_id bigint not null,
    event_id bigint not null,

    constraint compilations_events_pk primary key (compilation_id, event_id),
    constraint compilations_events_compilations foreign key (compilation_id) references compilations (compilation_id) on delete cascade on update cascade,
    constraint compilations_events_events foreign key (event_id) references events (event_id) on delete cascade on update cascade
);