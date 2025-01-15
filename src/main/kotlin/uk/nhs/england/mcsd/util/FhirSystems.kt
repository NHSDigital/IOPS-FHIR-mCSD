package uk.nhs.england.mcsd.util

import java.math.BigInteger
import java.util.*

object FhirSystems {
    const val EMIS_PRACTITIONER: String = "https://emis.com/Id/Practitioner/GUID"

    const val EXTENSION_LOCATION_TYPE: String = "http://fhir.virtuallyhealthcare.co.uk/LocationType"

    const val EXTENSION_LOCATION: String = "http://fhir.virtuallyhealthcare.co.uk/Location"

    const val NHS_GMP_NUMBER: String = "https://fhir.hl7.org.uk/Id/gmp-number"

    const val NHS_GMC_NUMBER: String = "https://fhir.hl7.org.uk/Id/gmc-number"

    const val NHS_NUMBER: String = "https://fhir.nhs.uk/Id/nhs-number"

    const val ODS_CODE: String = "https://fhir.nhs.uk/Id/ods-organization-code"

    const val ODS_SITE_CODE: String = "https://fhir.nhs.uk/Id/ods-site-code"

    const val VIRTUALLY_CONNECTION_TYPE: String = "http://fhir.virtuallyhealthcare.co.uk/ConnectionType"

    const val AWS_LOCATION_IDENTIFIER: String = "https://fhir.virtually.healthcare/Id/Location"

    const val AWS_TASK_IDENTIFIER: String = "https://fhir.virtually.healthcare/Id/Task"

    const val EMIS_PATIENT_IDENTIFIER: String = "https://emis.com/Id/Patient/DBID"
    const val EMIS_PATIENT_ODS_IDENTIFIER: String = "https://emis.com/Id/Patient/ID"

    const val EMIS_PRACTITIONER_IDENTIFIER: String = "https://emis.com/Id/Practitioner/DBID"

    const val SNOMED_CT: String = "http://snomed.info/sct"

    const val DMandD: String = "https://dmd.nhs.uk"

    const val ISO_EHR_EVENTS: String = "http://terminology.hl7.org/CodeSystem/iso-21089-lifecycle"

    const val FHIR_RESOURCE_TYPE: String = "http://hl7.org/fhir/resource-types"

    const val DICOM_AUDIT_ROLES: String = "http://dicom.nema.org/resources/ontology/DCM"

    const val V3_ROLE_CLASS: String = "http://terminology.hl7.org/CodeSystem/v3-RoleClass"

    const val V3_PARTICIPANT_TYPE: String = "http://terminology.hl7.org/CodeSystem/v3-ParticipationType"

    fun stripBrace(str: String): String {
        return str.replace("{", "").replace("}", "")
    }

    fun cachedQuery(host: String, path: String?, query: String?): String {
        var path = path
        var query = query
        if (path == null) path = "root"
        if (query == null) query = ""
        return "$host/$path?$query"
    }


    fun getId(reference: String): String {
        val strings = reference.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return strings[strings.size - 1]
    }

    fun isNumeric(reference: String): Boolean {
        val id = getId(reference)
        try {
            BigInteger.valueOf(id.toLong())
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun isUUID(reference: String): Boolean {
        val id = getId(reference)
        try {
            val uuid = UUID.fromString(id)
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
