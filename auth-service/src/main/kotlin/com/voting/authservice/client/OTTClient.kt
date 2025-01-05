package com.voting.authservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "send-token-service", url = "http://localhost:8080")
interface OTTClient {
    @PostMapping("/ott/token")
    fun sendToken(@RequestParam("username") username: String)

    @PostMapping("/login/ott", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun submit(@RequestParam("token") token: String, @RequestParam("username") username: String, @RequestParam("token-type") tokenType: String)
}