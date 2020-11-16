create table person (
	first_name VARCHAR(50),
	last_name VARCHAR(50),
	email VARCHAR(50),
	age INT
);

GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';

GRANT ALL PRIVILEGES ON mysqldb.* TO 'user'@'%';
flush privileges;
alter user 'user' identified with mysql_native_password by 'password';

INSERT INTO person (first_name, last_name, email,age)VALUES (:#firstName, :#lastName, :#email, :#age);


jdbc:mysql://mysql:3306/mysqldb
