package me.steven.mcadams.kafkaworkshop

import org.apache.kafka.clients.producer.ProducerConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer

@SpringBootApplication
class SpringKafkaTemplate

fun main(args: Array<String>) {
    runApplication<SpringKafkaTemplate>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, String> = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(3)
        factory.containerProperties.pollTimeout = 3000
        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun consumerConfigs(): Map<String, String> {
        return mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
        )
    }
}
