package uk.nhs.england.mcsd.configuration


import ca.uhn.fhir.context.FhirContext
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType

import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.nhs.england.mcsd.util.FHIRExamples


@Configuration
open class OpenApiConfig(@Qualifier("R4") val ctx : FhirContext) {
    var MCSD = "Care Services Directory"
    var APIM = "Security and API Management"
  //


    @Bean
    open fun customOpenAPI(
        fhirServerProperties: uk.nhs.england.mcsd.configuration.FHIRServerProperties
       // restfulServer: FHIRR4RestfulServer
    ): OpenAPI? {

        val oas = OpenAPI()
            .info(
                Info()
                    .title(fhirServerProperties.server.name)
                    .version(fhirServerProperties.server.version)
                    .description(""
                        /*

                        "## FHIR Implementation Guides"
                        + "\n\n [UK Core Implementation Guide (0.5.1)](https://simplifier.net/guide/ukcoreimplementationguide0.5.0-stu1/home?version=current)"
                        + "\n\n [NHS Digital Implementation Guide (2.6.0)](https://simplifier.net/guide/nhsdigital?version=2.6.0)"
*/
                    )
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
        oas.addServersItem(
            Server().description(fhirServerProperties.server.name).url(fhirServerProperties.server.baseUrl)
        )

        // CSD

        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(MCSD)
                .description("[HL7 FHIR Administration Module](https://www.hl7.org/fhir/R4/administration-module.html) \n"
                + " [IHE mCSD ITI-90](https://profiles.ihe.net/ITI/mCSD/ITI-90.html)")
        )

        oas.path("/FHIR/R4/metadata",PathItem()
            .get(
                Operation()
                    .addTagsItem(APIM)
                    .summary("server-capabilities: Fetch the server FHIR CapabilityStatement").responses(getApiResponses())))


        // Endpoint
        var endpointItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Endpoint/{id}",endpointItem)

        endpointItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search Endpoint")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the HealthcareService resource")
                    .responses(getApiResponses())

                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Identifies this endpoint across multiple systems")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("organization")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The organization that is managing the endpoint")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("status")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The current status of the Endpoint (usually expected to be active)")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Endpoint",endpointItem)

        // HealthcareService
        var healthcareSericeItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read Organisation")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/HealthcareService/{id}",healthcareSericeItem)
        healthcareSericeItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search HealthcareService")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the HealthcareService resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("_id")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("active")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The Healthcare Service is currently marked as active")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("External identifiers for this item")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("location")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The location of the Healthcare Service")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("organization")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The organization that provides this Healthcare Service")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("service-type")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The type of service provided by this healthcare service")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/HealthcareService",healthcareSericeItem)

        // location
        var locationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read Location")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Location/{id}",locationItem)
        locationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search Location")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the Location resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("An identifier for the location")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("name")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A portion of the location's name or alias")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("organization")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Searches for locations that are managed by the provided organization")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("partof")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A location of which this location is a part")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("status")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Searches for locations with a specific kind of status")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("type")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A code for the type of location")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("near")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Search for locations where the location.position is near to, or within a specified distance of, the provided coordinates expressed as [latitude]|[longitude]|[distance]|[units] (using the WGS84 datum, see notes). If the units are omitted, then kms should be assumed. If the distance is omitted, then the server can use its own discretion as to what distances should be considered near (and units are irrelevant)\n" +
                                "\n" +
                                "Servers may search using various techniques that might have differing accuracies, depending on implementation efficiency.\n" +
                                "\n" +
                                "Requires the near-distance parameter to be provided also")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Location",locationItem)

        // Organisation
        var organisationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read Organisation")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Organization/{id}",organisationItem)
        organisationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search Organisation")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the Organization resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("_id")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Any identifier for the organization (not the accreditation issuer's identifier)")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("address-postalcode")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A postal code specified in an address")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("name")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A portion of the organization's name or alias")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Organization",organisationItem)

        // Practitioner
        var practitionerItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read Practitioner")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Practitioner/{id}",practitionerItem)
        practitionerItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search Practitioner")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the Practitioner resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("_id")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A practitioner's Identifier")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("address-postalcode")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A postalCode specified in an address")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("name")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A server defined search that may match any of the string fields in the HumanName, including family, give, prefix, suffix, suffix, and/or text")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Practitioner",practitionerItem)

        // PractitionerRole
        var practitionerRoleItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Read PractitionerRole")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/PractitionerRole/{id}",practitionerRoleItem)
        practitionerRoleItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MCSD)
                    .summary("Search PractitionerRole")
                    .description("The Care Services Selective Supplier shall support the following search parameters on the PractitionerRole resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("_id")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A practitioner's Identifier")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("organization")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The identity of the organization the practitioner represents / acts on behalf of")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("practitioner")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Practitioner that is able to provide the defined services for the organization")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/PractitionerRole",practitionerRoleItem)


        return oas
    }



    fun getApiResponses() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesMarkdown() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("text/markdown", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getApiResponsesXMLJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+xml", MediaType().schema(StringSchema()._default("<>")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesRAWJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getPathItem(tag :String, name : String,fullName : String, param : String, example : String, description : String ) : PathItem {
        val pathItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(tag)
                    .summary("search-type")
                    .description(description)
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name(param)
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The uri that identifies the "+fullName)
                        .schema(StringSchema().format("token"))
                        .example(example)))
        return pathItem
    }
}
