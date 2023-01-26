package me.steven.mcadams.kafkaworkshop

import java.time.Duration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.TimeWindows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.FactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer


@SpringBootApplication
class SpringKafkaStreams

fun main(args: Array<String>) {
    runApplication<SpringKafkaStreams>(*args)
}

@Configuration
@EnableKafka
class KafkaConfig {

    val logger: Logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    companion object {
        const val PET_NAME_TOPIC = "pet-name"
        const val PET_NAME_UPDATE_COUNT_TOPIC = "pet-name-update-count"
    }

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kafkaStreamsConfiguration(): KafkaStreamsConfiguration {
        val kafkaConfig = mutableMapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "spring-kafka-streams-application",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            StreamsConfig.CLIENT_ID_CONFIG to "spring-kafka-streams-application",
            ProducerConfig.COMPRESSION_TYPE_CONFIG to "zstd",
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.StringSerde()::class.java.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.StringSerde()::class.java.name,
            StreamsConfig.NUM_STREAM_THREADS_CONFIG to "3"
        )

        return KafkaStreamsConfiguration(kafkaConfig.toMap())
    }

    @Bean
    fun streamsBuilderFactoryBeanConfigurer(): StreamsBuilderFactoryBeanConfigurer {
        return StreamsBuilderFactoryBeanConfigurer { streamsBuilderFactoryBean: StreamsBuilderFactoryBean ->
            streamsBuilderFactoryBean.setStateListener { newState: KafkaStreams.State, oldState: KafkaStreams.State ->
                logger.info("State transition from {} to {}", oldState, newState)
            }
        }
    }

    @Bean
    fun petStreamsBuilder(kafkaStreamsConfiguration: KafkaStreamsConfiguration): FactoryBean<StreamsBuilder> {
        return StreamsBuilderFactoryBean(kafkaStreamsConfiguration)
    }

    @Bean
    fun petStream(petStreamsBuilder: StreamsBuilder): KStream<String, String> {
        val petStreamProcessor: KStream<String, String> = petStreamsBuilder.stream(PET_NAME_TOPIC)

        petStreamProcessor.groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
            .count()
            .toStream()
            .map { key, value -> KeyValue(key.key(), value) }
            .filter { _, value -> value > 3 }
            .peek { key, value ->
                logger.info("publishing {}:{}", key, value)
            }
            .to(PET_NAME_UPDATE_COUNT_TOPIC, Produced.valueSerde(Serdes.Long()))

        return petStreamProcessor
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

    @KafkaListener(topics = [PET_NAME_UPDATE_COUNT_TOPIC])
    fun listen(record: ConsumerRecord<String, String>) {
        logger.info("RECORD RECEIVED FROM {}", PET_NAME_UPDATE_COUNT_TOPIC)
        debug(record)
    }
}
