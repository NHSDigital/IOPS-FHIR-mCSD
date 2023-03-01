package uk.nhs.england.mcsd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import uk.nhs.england.mcsd.configuration.FHIRServerProperties


@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(uk.nhs.england.mcsd.configuration.FHIRServerProperties::class)
open class FHIRFacade

fun main(args: Array<String>) {
    runApplication<uk.nhs.england.mcsd.FHIRFacade>(*args)
}
