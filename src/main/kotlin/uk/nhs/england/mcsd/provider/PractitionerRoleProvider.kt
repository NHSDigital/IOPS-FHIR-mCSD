package uk.nhs.england.mcsd.provider

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
import uk.nhs.england.mcsd.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
class PractitionerRoleProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {


    @Read(type=PractitionerRole::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): PractitionerRole? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"PractitionerRole")
        return if (resource is PractitionerRole) resource else null
    }

    @Search(type=PractitionerRole::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = PractitionerRole.SP_PRACTITIONER) practitioner:  TokenParam?,
        @OptionalParam(name = PractitionerRole.SP_ORGANIZATION) organisation:  TokenParam?,
        @OptionalParam(name = PractitionerRole.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = PractitionerRole.SP_RES_ID)  resid : StringParam?
    ): Bundle? {

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"PractitionerRole")
        if (resource != null && resource is Bundle) {
           return resource
        }
        return null
    }
}
