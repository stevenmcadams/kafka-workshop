package me.steven.mcadams.pet

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@SpringBootApplication
@EnableReactiveMongoRepositories
class PetServiceApplication

fun main(args: Array<String>) {
    runApplication<PetServiceApplication>(*args)
}

@RestController
@RequestMapping("/pets")
class PetController(
    val petReactiveRepository: PetReactiveRepository
) {

    // create
    @PostMapping
    fun createPet(@RequestBody pet: Pet): Mono<Pet> {
        return petReactiveRepository.insert(pet)
    }

    // update
    @PutMapping("/{id}")
    fun updatePet(@PathVariable("id") id: String, @RequestBody pet: Pet): Mono<Pet> {
        return petReactiveRepository.save(pet)
    }

    // delete
    @DeleteMapping("/{id}")
    fun deletePet(@PathVariable("id") id: String): Mono<Void> {
        return petReactiveRepository.deleteById(id)
    }

    // read

    // delete
    @GetMapping("/{id}")
    fun getPet(@PathVariable("id") id: String): Mono<Pet> {
        return petReactiveRepository.findById(id)
    }
}

@Document
data class Pet(
    @Id val id: String? = null,
    val name: String,
    val type: String,
)

@Repository
interface PetReactiveRepository : ReactiveMongoRepository<Pet, String>


@Configuration
class ReactiveMongoConfig(
    @Autowired val mongoClient: MongoClient
) {

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(mongoClient, "pets")
    }
}
