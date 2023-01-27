package me.steven.mcadams.kafkaworkshop

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class SpringKafkaListenerProperties

fun main(args: Array<String>) {
    runApplication<SpringKafkaListenerProperties>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @KafkaListener(id = "consumer-record-listener", topics = ["pets-pet"])
    fun listen(record: ConsumerRecord<String, String>) {
        logger.info(
            "topic={}, partition={}, offset={}, timestamp={}, key={}, payload={}",
            record.topic(),
            record.partition(),
            record.offset(),
            record.timestamp(),
            record.key(),
            record.value()
        )
    }


    data class PetDto(
        val fullDocument: PetEntity
    ) {
        data class PetEntity(
            val _id: String,
            val name: String,
            val type: String
        )
    }

    @KafkaListener(id = "dto-listener", topics = ["pets-pet"])
    fun listenToObject(petDto: PetDto) {
        logger.info("petDto={}", petDto)
    }
}
