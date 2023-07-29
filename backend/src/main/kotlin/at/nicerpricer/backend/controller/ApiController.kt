package at.nicerpricer.backend.controller

import at.nicerpricer.backend.model.Data
import at.nicerpricer.backend.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController @Autowired constructor(
    private val dataService: DataService
) {

    @GetMapping("/")
    fun index(): String {
        return "Hello to NicerPricer's API!"
    }

    @GetMapping("/data")
    fun data(): Data? {
        return dataService.first()
    }

    @GetMapping("/query")
    fun query(query: String): List<String> {
        return dataService.query(query)
    }

    @PostMapping("/data")
    fun post(data: Data) {
        println(data)
    }
}
