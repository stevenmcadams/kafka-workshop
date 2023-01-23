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

* download kafka
* download mongo kafka connect jar
* add mongo connector configuration
* create topics
    * pets-pet

```shell
cd ~/Downloads
curl https://downloads.apache.org/kafka/3.3.1/kafka_2.13-3.3.1.tgz --output kafka_2.13-3.3.1.tgz 
tar -xvf kafka_2.13-3.3.1.tgz
cd kafka_2.13-3.3.1
mkdir plugins
cd plugins
# this doesn't seem to be working. download this file manually and drop it into ~/Downloads/kafka_2.13-3.3.1/plugins/.
# curl https://search.maven.org/remotecontent?filepath=org/mongodb/kafka/mongo-kafka-connect/1.7.0/mongo-kafka-connect-1.7.0-all.jar --output mongo-kafka-connect-1.7.0-all.jar
echo "plugin.path=$(pwd)" >> ../config/connect-standalone.properties

# add connector config
cd ../config
echo "change.stream.full.document=updateLookup" >> source-mongo-pets-pet.properties 
echo "collection=pet" >> source-mongo-pets-pet.properties
echo "connection.uri=mongodb://localhost:27017" >> source-mongo-pets-pet.properties
echo "connector.class=com.mongodb.kafka.connect.MongoSourceConnector" >> source-mongo-pets-pet.properties
echo "copy.existing=true" >> source-mongo-pets-pet.properties
echo "database=pets" >> source-mongo-pets-pet.properties
echo "errors.log.enable=true" >> source-mongo-pets-pet.properties
echo "errors.log.include.messages=true" >> source-mongo-pets-pet.properties
echo "heartbeat.interval.ms=60000" >> source-mongo-pets-pet.properties
echo "heartbeat.topic.name=__mongodb_pets_pet_heartbeats" >> source-mongo-pets-pet.properties
echo "key.converter=org.apache.kafka.connect.storage.StringConverter" >> source-mongo-pets-pet.properties
echo "mongo.errors.deadletterqueue.topic.name=pets-pet-dl" >> source-mongo-pets-pet.properties
echo "mongo.errors.log.enable=true" >> source-mongo-pets-pet.properties
echo "mongo.errors.tolerance=all" >> source-mongo-pets-pet.properties
echo "name=source-mongo-pets-pet" >> source-mongo-pets-pet.properties
echo "output.format.key=schema" >> source-mongo-pets-pet.properties
echo "output.format.value=json" >> source-mongo-pets-pet.properties
echo "output.json.formatter=com.mongodb.kafka.connect.source.json.formatter.SimplifiedJson" >> source-mongo-pets-pet.properties
echo "output.schema.key={\"type\": \"record\", \"name\": \"keySchema\", \"fields\" : [{\"name\": \"documentKey._id\", \"type\": \"string\"}]}" >> source-mongo-pets-pet.properties
echo "tasks.max=1" >> source-mongo-pets-pet.properties
echo "topic.separator=-" >> source-mongo-pets-pet.properties
echo "value.converter=org.apache.kafka.connect.storage.StringConverter" >> source-mongo-pets-pet.properties

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
./bin/kafka-console-consumer.sh --topic pets-pet --bootstrap-server localhost:9092
```
