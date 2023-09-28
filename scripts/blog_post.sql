CREATE TABLE post(
	post_id int,
    content varchar(255),
    date_published timestamp,
    post_user int,
    primary key(post_id),
    constraint FK_User foreign key(post_user) references blog_user(user_id)
);