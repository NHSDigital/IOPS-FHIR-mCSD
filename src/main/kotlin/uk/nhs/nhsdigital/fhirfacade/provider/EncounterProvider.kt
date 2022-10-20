package uk.nhs.nhsdigital.fhirfacade.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.fhirfacade.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class EncounterProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Encounter> {
        return Encounter::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Encounter? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Encounter) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Encounter.SP_PATIENT) encounter : ReferenceParam?,
        @OptionalParam(name = Encounter.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Encounter.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Encounter.SP_RES_ID)  resid : StringParam?
    ): List<Encounter> {
        val encounters = mutableListOf<Encounter>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Encounter) encounters.add(entry.resource as Encounter)
            }
        }

        return encounters
    }
}
