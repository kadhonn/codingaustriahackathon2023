package at.nicerpricer.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {

    @GetMapping("/")
    fun index(): String {
        return "Hello to NicerPricer's API!"
    }

    @GetMapping("/data")
    fun data(): Data {
        return Data("key", "value")
    }

    @PostMapping("/data")
    fun post(data: Data) {
        println(data)
    }
}

data class Data(var name: String, var value: String)
