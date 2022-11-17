package uk.nhs.nhsdigital.mcsd

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.server.RestfulServer
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.cors.CorsConfiguration
import uk.nhs.nhsdigital.mcsd.configuration.FHIRServerProperties
import uk.nhs.nhsdigital.mcsd.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.nhsdigital.mcsd.interceptor.CapabilityStatementInterceptor
import uk.nhs.nhsdigital.mcsd.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet

@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    public val fhirServerProperties: FHIRServerProperties,

    val practitionerProvider: PractitionerProvider,
    val practitionerRoleProvider: PractitionerRoleProvider,
    val organizationProvider: OrganizationProvider,
    val healthcareServiceProvider: HealthcareServiceProvider,
    val locationProvider: LocationProvider,
    val endpointProvider: EndpointProvider,

    val careTeamProvider: CareTeamProvider



) : RestfulServer(fhirContext) {

    override fun initialize() {
        super.initialize()

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))


        registerProvider(organizationProvider)
        registerProvider(practitionerProvider)
        registerProvider(practitionerRoleProvider)
        registerProvider(healthcareServiceProvider)
        registerProvider(endpointProvider)
        registerProvider(locationProvider)
        registerProvider(careTeamProvider)


        val awsAuditEventLoggingInterceptor =
            AWSAuditEventLoggingInterceptor(
                this.fhirContext,
                fhirServerProperties
            )
        interceptorService.registerInterceptor(awsAuditEventLoggingInterceptor)
        registerInterceptor(CapabilityStatementInterceptor(fhirServerProperties))

        val config = CorsConfiguration()
        config.addAllowedHeader("x-fhir-starter")
        config.addAllowedHeader("Origin")
        config.addAllowedHeader("Accept")
        config.addAllowedHeader("X-Requested-With")
        config.addAllowedHeader("Content-Type")
        config.addAllowedHeader("Authorization")
        config.addAllowedHeader("x-api-key")

        config.addAllowedOrigin("*")

        config.addExposedHeader("Location")
        config.addExposedHeader("Content-Location")
        config.allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")

        // Create the interceptor and register it
        interceptorService.registerInterceptor(CorsInterceptor(config))


        isDefaultPrettyPrint = true
        defaultResponseEncoding = EncodingEnum.JSON
    }
}
