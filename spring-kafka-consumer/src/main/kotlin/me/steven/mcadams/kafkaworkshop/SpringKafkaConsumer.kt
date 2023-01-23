package me.steven.mcadams.kafkaworkshop

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class SpringKafkaConsumer

fun main(args: Array<String>) {
    runApplication<SpringKafkaConsumer>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @KafkaListener(id = "pet-listener", topics = ["pets-pet"], clientIdPrefix = "spring-kafka-consumer-workshop")
    fun listen(message: String) {
        logger.info("received message : {}", message)
        logger.info(jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jacksonObjectMapper().readTree(message)))
    }
}
