package uk.nhs.england.mcsd.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search

import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.mcsd.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
class PractitionerProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {



    @Read(type=Practitioner::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Practitioner? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Practitioner")
        return if (resource is Practitioner) resource else null
    }

    @Search(type=Practitioner::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Practitioner.SP_NAME) name : StringParam?,
        @OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE) postcode : StringParam?,
        @OptionalParam(name = Practitioner.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Practitioner.SP_RES_ID)  resid : StringParam?
    ): Bundle? {

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Practitioner")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null;
    }
}
