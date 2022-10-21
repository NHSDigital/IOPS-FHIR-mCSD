package uk.nhs.nhsdigital.mcsd.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.api.server.RequestDetails
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.dstu3.model.Encounter
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.mcsd.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class CareTeamProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<CareTeam> {
        return CareTeam::class.java
    }



    @Update
    fun update(
        theRequest: HttpServletRequest,
        @ResourceParam careTeam: CareTeam,
        @IdParam theId: IdType?,
        theRequestDetails: RequestDetails?
    ): MethodOutcome? {

        return cognitoAuthInterceptor.updatePost(theRequest,careTeam)
    }
    @Create
    fun create(theRequest: HttpServletRequest, @ResourceParam careTeam: CareTeam): MethodOutcome? {

        return cognitoAuthInterceptor.updatePost(theRequest,careTeam)

    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): CareTeam? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is CareTeam) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = CareTeam.SP_DATE) date: DateRangeParam?,
        @OptionalParam(name = CareTeam.SP_PATIENT) patient: ReferenceParam?,
        @OptionalParam(name = CareTeam.SP_STATUS) status: TokenParam?,
        @OptionalParam(name = CareTeam.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = CareTeam.SP_RES_ID)  resid : StringParam?
    ): List<CareTeam> {
        val careTeams = mutableListOf<CareTeam>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is CareTeam) careTeams.add(entry.resource as CareTeam)
            }
        }

        return careTeams
    }
}
