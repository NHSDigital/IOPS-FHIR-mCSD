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
class LocationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Location> {
        return Location::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Location? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Location) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Location.SP_NAME) name : StringParam?,
        @OptionalParam(name = Location.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Location.SP_ORGANIZATION)  organization :TokenParam?,
        @OptionalParam(name = Location.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = Location.SP_PARTOF)  partOf : StringParam?,
        @OptionalParam(name = Location.SP_TYPE) type : TokenParam?,
        @OptionalParam(name = "near") near : TokenParam?
    ): List<Location> {
        val locations = mutableListOf<Location>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Location) locations.add(entry.resource as Location)
            }
        }

        return locations
    }
}
