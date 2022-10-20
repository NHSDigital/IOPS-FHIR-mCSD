package uk.nhs.nhsdigital.fhirfacade.provider

import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.ResourceParam
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component

@Component
class ProcessMessageProvider() {

    @Operation(name = "\$process-message", idempotent = true)
    fun expand(@ResourceParam bundle:Bundle,
             ): OperationOutcome? {

        var operationOutcome = OperationOutcome();

        return operationOutcome
    }



}
