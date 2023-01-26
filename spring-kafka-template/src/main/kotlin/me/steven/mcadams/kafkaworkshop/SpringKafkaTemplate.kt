package me.steven.mcadams.kafkaworkshop

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory


@SpringBootApplication
class SpringKafkaTemplate

fun main(args: Array<String>) {
    runApplication<SpringKafkaTemplate>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    companion object {
        const val PET_NAME_TOPIC = "pet-name"
        val objectMapper = jacksonObjectMapper()
    }

    @Bean
    fun admin() = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"))

    @Bean
    fun petSnapshotTopic(): NewTopic {
        return TopicBuilder.name(PET_NAME_TOPIC)
            .partitions(10)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun producerConfigs(): Map<String, String> {
        return mutableMapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.COMPRESSION_TYPE_CONFIG to "zstd",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer"
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    private fun debug(record: ConsumerRecord<String, String>) {
        logger.info("received message : {}", record.value())
        logger.info("\t{}:{}", "key", record.key())
        logger.info("\t{}:{}", "offset", record.offset())
        logger.info("\t{}:{}", "partition", record.partition())
        logger.info("\t{}:{}", "timestamp", record.timestamp())
        logger.info("\t{}:{}", "topic", record.topic())
        record.headers().iterator().forEach {
            logger.info("\tHEADER : {}:{}", it.key(), it.value().toString())
        }
    }

    @KafkaListener(id = "pets-pet-listener", topics = ["pets-pet"], clientIdPrefix = "spring-kafka-template-consumer")
    fun petsListener(record: ConsumerRecord<String, String>) {
        debug(record)
        val payload = record.value()
        val root = objectMapper.readTree(payload)
        val name = root.get("fullDocument")?.get("name")?.textValue().orEmpty()
        val type = root.get("fullDocument")?.get("type")?.textValue().orEmpty()
        kafkaTemplate().send(PET_NAME_TOPIC, name, type)
    }

    @KafkaListener(id = "pet-name-topic-listener", topics = [PET_NAME_TOPIC], clientIdPrefix = "spring-kafka-consumer-workshop")
    fun listen(record: ConsumerRecord<String, String>) {
        debug(record)
    }
}
