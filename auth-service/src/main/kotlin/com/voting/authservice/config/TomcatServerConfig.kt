package com.voting.authservice.config

import org.apache.catalina.connector.Connector
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test") // Exclude this from DemoAuthorizationServerApplicationTests and DemoAuthorizationServerConsentTests
@Configuration(proxyBeanMethods = false)
class TomcatServerConfig {
    @Bean
    fun connectorCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer { tomcat: TomcatServletWebServerFactory ->
            tomcat.addAdditionalTomcatConnectors(
                createHttpConnector()
            )
        }
    }

    private fun createHttpConnector(): Connector {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.scheme = "http"
        connector.port = 9000
        connector.secure = false
        connector.redirectPort = 9443
        return connector
    }
}