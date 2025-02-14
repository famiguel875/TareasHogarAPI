package com.es

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties()
class TareasHogarAPIAplication

fun main(args: Array<String>) {
    runApplication<TareasHogarAPIAplication>(*args)
}