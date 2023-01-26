# kafka-workshop

# helpful commands

```shell
# list existing topics
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

```shell
# delete all kafka topics
for topic in $(./bin/kafka-topics.sh --list --bootstrap-server localhost:9092); do ./bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic $topic ; done;
```

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

# terminal 4 - watch a kafka topic

```shell
# create topic
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic pets-pet --partitions 3 --replication-factor 1 --config compression.type=zstd --config cleanup.policy=delete --config retention.ms=2592000000 --config min.insync.replicas=1
./bin/kafka-console-consumer.sh --topic pets-pet --bootstrap-server localhost:9092
```
