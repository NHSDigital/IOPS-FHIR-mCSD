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
class OrganizationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {


    @Read(type=Organization::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Organization? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Organization")
        return if (resource is Organization) resource else null
    }

    @Search(type=Organization::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Organization.SP_NAME) name : StringParam?,
        @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE) postcode : StringParam?,
        @OptionalParam(name = Organization.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Organization.SP_RES_ID)  resid : StringParam?
    ): Bundle? {
        val organisations = mutableListOf<Organization>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Organization")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }
}
