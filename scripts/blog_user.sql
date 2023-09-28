CREATE TABLE blog_user(
	user_id int,
    first_name varchar(255),
	last_name varchar(255),
    email varchar(255),
    primary key(user_id),
    unique(email)
);