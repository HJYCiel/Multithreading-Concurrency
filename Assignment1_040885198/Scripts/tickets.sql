-- Drop table

DROP TABLE IF exists public.tickets;

-- Create table

CREATE TABLE public.tickets (
    id int not null IDENTITY,
    first_name varchar(100),
    last_name varchar(100),
    email varchar(100),
    tstamp timestamp(9),
    primary key (id)
);