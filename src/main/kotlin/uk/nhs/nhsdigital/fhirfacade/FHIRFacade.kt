package uk.nhs.nhsdigital.fhirfacade

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import uk.nhs.nhsdigital.fhirfacade.configuration.FHIRServerProperties


@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(FHIRServerProperties::class)
open class FHIRFacade

fun main(args: Array<String>) {
    runApplication<FHIRFacade>(*args)
}
