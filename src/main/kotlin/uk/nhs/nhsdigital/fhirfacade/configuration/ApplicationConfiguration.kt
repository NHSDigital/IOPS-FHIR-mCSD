package uk.nhs.nhsdigital.fhirfacade.configuration

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.StrictErrorHandler
import ca.uhn.fhir.rest.client.api.IGenericClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import uk.nhs.nhsdigital.fhirfacade.interceptor.CognitoAuthInterceptor

@Configuration
open class ApplicationConfiguration {
    @Bean("R4")
    open fun fhirR4Context(): FhirContext {
        val fhirContext = FhirContext.forR4()
        fhirContext.setParserErrorHandler(StrictErrorHandler())
        return fhirContext
    }

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }



    @Bean
    fun getAWSclient(cognitoIdpInterceptor: CognitoAuthInterceptor?, mmessageProperties: MessageProperties, @Qualifier("R4") ctx : FhirContext): IGenericClient? {
        val client: IGenericClient = ctx.newRestfulGenericClient(mmessageProperties.getCdrFhirServer())
        client.registerInterceptor(cognitoIdpInterceptor)
        return client
    }
}
