package uk.nhs.nhsdigital.mcsd.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.mcsd.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class HealthcareServiceProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {


    @Read(type=HealthcareService::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): HealthcareService? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"HealthcareService")
        return if (resource is HealthcareService) resource else null
    }

    @Search(type = HealthcareService::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = HealthcareService.SP_NAME) name : StringParam?,
        @OptionalParam(name = HealthcareService.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = HealthcareService.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = HealthcareService.SP_ACTIVE) active : StringParam?,
        @OptionalParam(name = HealthcareService.SP_LOCATION) location : TokenParam?,
        @OptionalParam(name = HealthcareService.SP_ORGANIZATION) organization : TokenParam?,
        @OptionalParam(name = HealthcareService.SP_SERVICE_TYPE) type : TokenParam?
    ): Bundle? {
        val healthcareServices = mutableListOf<HealthcareService>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"HealthcareService")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
