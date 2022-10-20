package uk.nhs.nhsdigital.mcsd.provider

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
import uk.nhs.nhsdigital.mcsd.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class PractitionerProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Practitioner> {
        return Practitioner::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Practitioner? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Practitioner) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Practitioner.SP_NAME) name : StringParam?,
        @OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE) postcode : StringParam?,
        @OptionalParam(name = Practitioner.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Practitioner.SP_RES_ID)  resid : StringParam?
    ): List<Practitioner> {
        val practitioners = mutableListOf<Practitioner>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Practitioner) practitioners.add(entry.resource as Practitioner)
            }
        }

        return practitioners
    }
}
