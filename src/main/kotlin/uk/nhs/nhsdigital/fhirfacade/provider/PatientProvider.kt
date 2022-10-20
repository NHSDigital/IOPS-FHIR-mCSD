package uk.nhs.nhsdigital.fhirfacade.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.fhirfacade.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class PatientProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Patient> {
        return Patient::class.java
    }

    @Read
    fun read( httpRequest : HttpServletRequest,@IdParam internalId: IdType): Patient? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo,  null)
        return if (resource is Patient) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) addressPostcode : StringParam?,
        @OptionalParam(name= Patient.SP_BIRTHDATE) birthDate : DateRangeParam?,
        @OptionalParam(name= Patient.SP_EMAIL) email : StringParam?,
        @OptionalParam(name = Patient.SP_FAMILY) familyName : StringParam?,
        @OptionalParam(name= Patient.SP_GENDER) gender :StringParam?,
        @OptionalParam(name= Patient.SP_GIVEN) givenName :StringParam?,
        @OptionalParam(name = Patient.SP_IDENTIFIER) identifier :TokenParam?,
        @OptionalParam(name= Patient.SP_NAME) name :StringParam?,
        @OptionalParam(name= Patient.SP_PHONE) phone : StringParam?
    ): List<Patient> {
        val patients = mutableListOf<Patient>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Patient) patients.add(entry.resource as Patient)
            }
        }

        return patients
    }
}
