package me.steven.mcadams.kafkaworkshop

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.Acknowledgment

@SpringBootApplication
class SpringKafkaListener

fun main(args: Array<String>) {
    runApplication<SpringKafkaListener>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @KafkaListener(id = "pet-listener", topics = ["pets-pet"], clientIdPrefix = "spring-kafka-consumer-workshop")
    fun listen(record: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {
        debug(record)

//        acknowledgment.nack(Duration.ofMillis(3000))
        acknowledgment.acknowledge()
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

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, String> = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(3)
        factory.containerProperties.pollTimeout = 3000
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun consumerConfigs(): Map<String, String> {
        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false",

            )
    }
}
