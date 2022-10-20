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
class OrganizationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Organization> {
        return Organization::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Organization? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Organization) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Organization.SP_NAME) name : StringParam?,
        @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE) postcode : StringParam?,
        @OptionalParam(name = Organization.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Organization.SP_RES_ID)  resid : StringParam?
    ): List<Organization> {
        val organisations = mutableListOf<Organization>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Organization) organisations.add(entry.resource as Organization)
            }
        }

        return organisations
    }
}
