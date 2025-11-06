package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class DemoApplication

@RestController
@RequestMapping("/api")
class HelloController {
    @GetMapping("/hello")
    fun hello() = "Hello from sample-service!"
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
