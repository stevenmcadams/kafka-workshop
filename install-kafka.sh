#!/bin/sh

PROJECT_DIR=$(pwd)
SCALA_VERSION="2.13"
KAFKA_VERSION="3.3.2"

echo "PROJECT_DIR=$PROJECT_DIR"

cd ~/Downloads

# clean up any old kafka directories
rm -rf "kafka_${SCALA_VERSION}-${KAFKA_VERSION}"
rm -rf /tmp/zookeeper
rm -rf /tmp/kafka-logs/

echo "> download kafka"
curl -L "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" --output "kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"
tar -xvf "kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"
cd "kafka_${SCALA_VERSION}-${KAFKA_VERSION}"
mkdir plugins
echo "> download mongo kafka connect plugin"
curl -L "https://search.maven.org/remotecontent?filepath=org/mongodb/kafka/mongo-kafka-connect/1.7.0/mongo-kafka-connect-1.7.0-all.jar" --output "plugins/mongo-kafka-connect-1.7.0-all.jar"
echo "plugin.path=$(pwd)" >> config/connect-standalone.properties

echo "> configure localhost kafka broker listener"
echo "listeners=PLAINTEXT://localhost:9092" >> config/server.properties

# add connector config
cp "${PROJECT_DIR}/source-mongo-pets-pet.properties" ./config/.
cd "${PROJECT_DIR}"

echo "kafka installed to"
echo "${HOME}/Downloads/kafka_${SCALA_VERSION}-${KAFKA_VERSION}"
