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
class MedicationDispenseProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<MedicationDispense> {
        return MedicationDispense::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): MedicationDispense? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is MedicationDispense) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = MedicationDispense.SP_PATIENT) medicationDispense : ReferenceParam?,
        @OptionalParam(name = MedicationDispense.SP_WHENHANDEDOVER)  date : DateRangeParam?,
        @OptionalParam(name = MedicationDispense.SP_PRESCRIPTION)  prescription: ReferenceParam,
        @OptionalParam(name = MedicationDispense.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = MedicationDispense.SP_RES_ID)  resid : StringParam?
    ): List<MedicationDispense> {
        val medicationDispenses = mutableListOf<MedicationDispense>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is MedicationDispense) medicationDispenses.add(entry.resource as MedicationDispense)
            }
        }

        return medicationDispenses
    }
}
