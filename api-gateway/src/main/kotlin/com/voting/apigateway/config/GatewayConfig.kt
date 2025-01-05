package com.voting.apigateway.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Configuration
class GatewayConfig {

    @Bean
    fun userKeyResolver(): KeyResolver {
        return KeyResolver { exchange: ServerWebExchange ->
            Mono.just(
                exchange.request.queryParams.getFirst("user")!!
            )
        }
    }
}