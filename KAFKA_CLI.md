# kafka cli commands

### list existing topics

```shell
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### delete all kafka topics

```shell
for topic in $(./bin/kafka-topics.sh --list --bootstrap-server localhost:9092); do ./bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic $topic ; done;
```

### create a kafka topic

```shell
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic pets-pet --partitions 3 --replication-factor 1 --config compression.type=zstd --config cleanup.policy=delete --config retention.ms=2592000000 --config min.insync.replicas=1
```

### write to a topic

```shell
./bin/kafka-console-producer.sh --topic pets-pet --bootstrap-server localhost:9092
```

### listen to a topic

```shell
./bin/kafka-console-consumer.sh --property print.key=true --topic pets-pet --bootstrap-server localhost:9092
```

### TOPICS

* pets-pet
* pet-name
* pet-name-update-count

```shell
./bin/kafka-console-consumer.sh --property print.key=true --topic pets-pet --bootstrap-server localhost:9092
./bin/kafka-console-consumer.sh --property print.key=true --topic pet-name --bootstrap-server localhost:9092
./bin/kafka-console-consumer.sh --property print.key=true --topic pet-name-update-count --bootstrap-server localhost:9092
```
