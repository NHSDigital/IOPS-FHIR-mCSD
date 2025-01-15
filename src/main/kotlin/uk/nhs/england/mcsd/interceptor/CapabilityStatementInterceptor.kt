package uk.nhs.england.mcsd.interceptor

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.interceptor.api.Hook
import ca.uhn.fhir.interceptor.api.Interceptor
import ca.uhn.fhir.interceptor.api.Pointcut
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.r4.model.*
import uk.nhs.england.mcsd.configuration.FHIRServerProperties

@Interceptor
class CapabilityStatementInterceptor(
    val fhirServerProperties: uk.nhs.england.mcsd.configuration.FHIRServerProperties
) {


    @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
    fun customize(theCapabilityStatement: IBaseConformance) {

        // Cast to the appropriate version
        val cs: CapabilityStatement = theCapabilityStatement as CapabilityStatement
        cs.name = fhirServerProperties.server.name
        cs.software.name = fhirServerProperties.server.name
        cs.software.version = fhirServerProperties.server.version
        cs.publisher = "NHS England"
        cs.implementation.url = fhirServerProperties.server.baseUrl
        cs.implementation.description = fhirServerProperties.server.name
    }

    fun getResourceComponent(type : String, cs : CapabilityStatement ) : CapabilityStatement.CapabilityStatementRestResourceComponent? {
        for (rest in cs.rest) {
            for (resource in rest.resource) {
                // println(type + " - " +resource.type)
                if (resource.type.equals(type))
                    return resource
            }
        }
        return null
    }

}
