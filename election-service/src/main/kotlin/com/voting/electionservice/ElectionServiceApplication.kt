package com.voting.electionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ElectionServiceApplication

fun main(args: Array<String>) {
    runApplication<ElectionServiceApplication>(*args)
}
