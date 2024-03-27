alter table appointment
    add column created_at timestamp default current_timestamp;
alter table review
    add column created_at timestamp default current_timestamp;