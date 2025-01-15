package uk.nhs.england.mcsd.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import jakarta.servlet.http.HttpServletRequest
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.mcsd.interceptor.CognitoAuthInterceptor


@Component
class LocationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {

    @Read(type =Location::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Location? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Location")
        return if (resource is Location) resource else null
    }

    @Search(type =Location::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Location.SP_NAME) name : StringParam?,
        @OptionalParam(name = Location.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Location.SP_ORGANIZATION)  organization :TokenParam?,
        @OptionalParam(name = Location.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = Location.SP_PARTOF)  partOf : StringParam?,
        @OptionalParam(name = Location.SP_TYPE) type : TokenParam?,
        @OptionalParam(name = "near") near : TokenParam?
    ): Bundle? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Location")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }
}
