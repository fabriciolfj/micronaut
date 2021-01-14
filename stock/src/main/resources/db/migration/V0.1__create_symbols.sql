CREATE TABLE symbols (
value varchar primary key
);

create table quotes (
    id serial primary key,
    bid numeric,
    ask numeric,
    last_price numeric,
    volume numeric,
    symbol varchar,
    foreign key (symbol) references symbols(value),
    constraint last_price_is_positive check (last_price > 0),
    constraint volume_is_positive check (volume > 0)
);