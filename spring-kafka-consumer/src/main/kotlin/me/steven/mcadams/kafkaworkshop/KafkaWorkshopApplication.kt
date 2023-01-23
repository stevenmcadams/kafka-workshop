package me.steven.mcadams.kafkaworkshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaWorkshopApplication

fun main(args: Array<String>) {
	runApplication<KafkaWorkshopApplication>(*args)
}
