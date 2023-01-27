# kafka-workshop

# launch mongo with replicaset configured

```shell
nerdctl run -d --name mongodb -p 27017:27017 mongo:latest mongod --replSet my-mongo-set
nerdctl exec -it mongodb /bin/mongosh
rs.initiate();
exit
```

## Configure local kafka

```shell
sh ./install-kafka.sh
```

## launch local kafka

# terminal 1 - zookeeper

```shell
./bin/zookeeper-server-start.sh config/zookeeper.properties
```

# terminal 2 - kafka broker

```shell
./bin/kafka-server-start.sh config/server.properties
```

# terminal 3 - kafka connect

```shell
./bin/connect-standalone.sh config/connect-standalone.properties config/source-mongo-pets-pet.properties
```

