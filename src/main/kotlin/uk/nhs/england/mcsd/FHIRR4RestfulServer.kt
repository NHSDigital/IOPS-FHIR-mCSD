package uk.nhs.england.mcsd

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.server.RestfulServer
import org.springframework.beans.factory.annotation.Qualifier
import uk.nhs.england.mcsd.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.england.mcsd.interceptor.CapabilityStatementInterceptor
import uk.nhs.england.mcsd.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet

@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    public val fhirServerProperties: uk.nhs.england.mcsd.configuration.FHIRServerProperties,

    val practitionerProvider: PractitionerProvider,
    val practitionerRoleProvider: PractitionerRoleProvider,
    val organizationProvider: OrganizationProvider,
    val healthcareServiceProvider: HealthcareServiceProvider,
    val locationProvider: LocationProvider,
    val endpointProvider: EndpointProvider,


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


        val awsAuditEventLoggingInterceptor =
            AWSAuditEventLoggingInterceptor(
                this.fhirContext,
                fhirServerProperties
            )
        interceptorService.registerInterceptor(awsAuditEventLoggingInterceptor)
        registerInterceptor(CapabilityStatementInterceptor(fhirServerProperties))


        isDefaultPrettyPrint = true
        defaultResponseEncoding = EncodingEnum.JSON
    }
}
