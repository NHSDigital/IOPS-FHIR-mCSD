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
class EndpointProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Endpoint> {
        return Endpoint::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Endpoint? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Endpoint) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Endpoint.SP_ORGANIZATION) organization : TokenParam?,
        @OptionalParam(name = Endpoint.SP_STATUS) status : StringParam?,
        @OptionalParam(name = Endpoint.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Endpoint.SP_RES_ID)  resid : StringParam?
    ): List<Endpoint> {
        val endpoints = mutableListOf<Endpoint>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Endpoint) endpoints.add(entry.resource as Endpoint)
            }
        }

        return endpoints
    }
}
