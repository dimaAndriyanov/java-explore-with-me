create table if not exists applications (
    app_id int generated always as identity primary key,
    app_name varchar(128) not null unique
);

create table if not exists endpoints_hits (
    endpoint_hit_id bigint generated always as identity primary key,
    app_id int not null,
    uri varchar(1024) not null,
    ip_address bigint not null,
    visited timestamp not null,
    constraint endpoints_hits_applications foreign key (app_id) references applications(app_id) on delete cascade on update cascade
);