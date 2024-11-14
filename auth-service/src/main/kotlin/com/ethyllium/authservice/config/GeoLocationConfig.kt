package com.ethyllium.authservice.config

import com.maxmind.db.Reader
import com.maxmind.geoip2.DatabaseReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

@Configuration
class GeoLocationConfig(
    @Qualifier("webApplicationContext") private val resourceLoader: ResourceLoader
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    @Bean
    fun databaseReader(): DatabaseReader {
        try {
            log.info("GeoLocationConfig: Trying to load GeoLite2-Country database...")
            val resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb")
            val dbAsStream = resource.inputStream
            log.info("GeoLocationConfig: Database was loaded successfully.")
            return DatabaseReader.Builder(dbAsStream).fileMode(Reader.FileMode.MEMORY).build()
        } catch (e: Exception) {
            log.error("Data base couldn't be initialized\n" +
                    "GeoLocationConfig: Error loading GeoLite2-City database: ${e.message}")
            throw e
        }
    }
}