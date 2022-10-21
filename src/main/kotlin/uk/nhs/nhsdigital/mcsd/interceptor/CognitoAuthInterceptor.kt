package uk.nhs.nhsdigital.mcsd.interceptor

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.api.IClientInterceptor
import ca.uhn.fhir.rest.client.api.IHttpRequest
import ca.uhn.fhir.rest.client.api.IHttpResponse
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder
import com.amazonaws.services.cognitoidp.model.AuthFlowType
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import org.apache.commons.io.IOUtils
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.mcsd.configuration.MessageProperties
import uk.nhs.nhsdigital.mcsd.model.ResponseObject
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.servlet.http.HttpServletRequest


@Component
class CognitoAuthInterceptor(val messageProperties: MessageProperties, val ctx : FhirContext) : IClientInterceptor {

    var authenticationResult: AuthenticationResultType? = null



    override fun interceptRequest(iHttpRequest: IHttpRequest) {
        getAccessToken()
        // 10th Oct 2022 use id token instead of access token
        if (authenticationResult != null) iHttpRequest.addHeader("Authorization", "Bearer " + authenticationResult!!.idToken)
        iHttpRequest.addHeader("x-api-key", messageProperties.getAwsApiKey())
    }

    @Throws(IOException::class)
    override fun interceptResponse(iHttpResponse: IHttpResponse) {
        if (iHttpResponse.status != 200 && iHttpResponse.status != 201) {
            println(iHttpResponse.status)
        }
        // if unauthorised force a token refresh
        if (iHttpResponse.status == 401) {
            this.authenticationResult = null
        }
    }


    private fun getAccessToken(): AuthenticationResultType? {
        if (this.authenticationResult != null) return authenticationResult
        val cognitoClient: AWSCognitoIdentityProvider =
            AWSCognitoIdentityProviderClientBuilder.standard() // .withCredentials(propertiesFileCredentialsProvider)
                .withRegion("eu-west-2")
                .build()
        val authParams: MutableMap<String, String> = HashMap()
        messageProperties.getAwsClientUser()?.let { authParams.put("USERNAME", it) }
        messageProperties.getAwsClientPass()?.let { authParams.put("PASSWORD", it) }
        val authRequest = InitiateAuthRequest()
        authRequest.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .withClientId(messageProperties.getAwsClientId())
            .withAuthParameters(authParams)
        val result: InitiateAuthResult = cognitoClient.initiateAuth(authRequest)
        authenticationResult = result.getAuthenticationResult()
        return authenticationResult
    }

    @Throws(Exception::class)
    fun readFromUrl(path: String, queryParams: String?): Resource? {
        val responseObject = ResponseObject()
        val url = messageProperties.getCdrFhirServer()
        var myUrl: URL? = null
        myUrl = if (queryParams != null) {
            URL("$url$path?$queryParams")
        } else {
            URL(url + path)
        }
        val conn = myUrl.openConnection() as HttpURLConnection
        getAccessToken()
        val basicAuth = "Bearer "+authenticationResult!!.idToken
        conn.setRequestProperty("Authorization", basicAuth)
        conn.setRequestProperty("x-api-key",messageProperties.getAwsApiKey())
        conn.requestMethod = "GET"
        return try {
            conn.connect()
            val `is` = InputStreamReader(conn.inputStream)
            try {
                val rd = BufferedReader(`is`)
                responseObject.responseCode = 200
                val resource = ctx.newJsonParser().parseResource(IOUtils.toString(rd)) as Resource

                if (resource is Bundle) {
                    val bundle = resource
                    if (bundle.hasEntry()) {
                        for (entryComponent in bundle.entry) {

                        }
                    }
                }
                resource
            } finally {
                `is`.close()
            }
        } catch (ex: FileNotFoundException) {
            null
        } catch (ex: IOException) {
            throw UnprocessableEntityException(ex.message)
        }
    }

    @Throws(Exception::class)
    fun updatePost(httpRequest : HttpServletRequest, resource : Resource): MethodOutcome {

        val method = MethodOutcome()
        method.created = true
        val opOutcome = OperationOutcome()

        method.operationOutcome = opOutcome

        val url = messageProperties.getCdrFhirServer()
        var myUrl: URL? = null
        val queryParams = httpRequest.queryString
        val path = httpRequest.pathInfo
        myUrl = if (queryParams != null) {
            URL("$url$path?$queryParams")
        } else {
            URL(url + path)
        }
        val conn = myUrl.openConnection() as HttpURLConnection
        getAccessToken()
        val basicAuth = "Bearer "+authenticationResult!!.idToken
        conn.setRequestProperty("Authorization", basicAuth)
        conn.setRequestProperty("x-api-key",messageProperties.getAwsApiKey())
        conn.setRequestProperty("Content-Type", "application/fhir+json")
        conn.setRequestProperty("Accept", "application/fhir+json")
        conn.requestMethod = httpRequest.method
        conn.setDoOutput(true)
        val jsonInputString = ctx.newJsonParser().encodeResourceToString(resource)
        return try {
            conn.getOutputStream().use { os ->
                val input = jsonInputString.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }
            //conn.connect()
            val `is` = InputStreamReader(conn.inputStream)
            try {
                val rd = BufferedReader(`is`)
                val resource = ctx.newJsonParser().parseResource(IOUtils.toString(rd)) as Resource
                if (resource != null && resource is Resource) {
                    method.resource = resource
                }
                method
            } finally {
                `is`.close()
            }
        } catch (ex: FileNotFoundException) {
            method.created = false
            method
        } catch (ex: IOException) {
            throw UnprocessableEntityException(ex.message)
        }
    }

}
