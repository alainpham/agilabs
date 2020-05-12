
Run on local docker : 

docker network create --subnet=172.18.0.0/16 primenet
docker run --name postgres --net primenet --ip 172.18.0.100 -e POSTGRES_PASSWORD=password -d postgres

docker run --name mysql --net primenet --ip 172.18.0.101 -e MYSQL_ROOT_PASSWORD=debezium -e MYSQL_USER=mysqluser -e MYSQL_PASSWORD=mysqlpw debezium/example-mysql:0.10

