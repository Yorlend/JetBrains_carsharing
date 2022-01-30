create table if not exists company(
    id integer primary key auto_increment,
    name varchar(40) unique not null
);

create table if not exists car(
    id integer primary key auto_increment,
    name varchar(40) not null unique,
    company_id int not null,
    constraint fk_company foreign key (company_id) references company(id)
);

create table if not exists customer(
    id integer primary key auto_increment,
    name varchar(40) not null unique,
    rented_car_id integer,
    constraint fk_car foreign key (rented_car_id) references car(id)
);
