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
import javax.servlet.http.HttpServletRequest

@Component
class EndpointProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {


    @Read(type=Endpoint::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Endpoint? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Endpoint")
        return if (resource is Endpoint) resource else null
    }

    @Search(type=Endpoint::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Endpoint.SP_ORGANIZATION) organization : TokenParam?,
        @OptionalParam(name = Endpoint.SP_STATUS) status : StringParam?,
        @OptionalParam(name = Endpoint.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Endpoint.SP_RES_ID)  resid : StringParam?
    ): Bundle? {

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Endpoint")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }
}
