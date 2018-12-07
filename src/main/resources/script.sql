drop table subscriber;

insert into subscriber (id, first_name, last_name, username, loca, chat_id)
values (123, 'name', 'surname', 'username', 'location', 123);

create table subscriber (id int PRIMARY KEY,
                         first_name varchar(45) NULL,
                         last_name varchar(45) NULL,
                         username varchar(45) NULL,
                         loca varchar(45) NOT NULL,
                         chat_id int NOT NULL
);