package com.company.security.auth;

import com.company.security.auth.CompanyServerAuthModule;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author flora
 */
public class CompanyServerAuthModuleTest {

    private static final Logger L = Logger.getLogger(CompanyServerAuthModuleTest.class.getName());
    
    private String token;
    private String refreshToken;

    public void getToken(String refresh) {
        String endpoint = "http://localhost:8180/auth/realms/hello-realm/protocol/openid-connect/token";
        Client client = ClientBuilder.newClient();
        Form form = new Form();
        form.param("client_id", "hello-world");
        
        if (refresh == null) {
        form.param("client_id", "hello-world")
                .param("grant_type", "password")
                .param("username", "hmhlongo")
                .param("password", "topsecret");
        } else {
            form.param("grant_type", "refresh_token")
                    .param("refresh_token", refresh);
        }

        
        String raw = client.target(endpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(form), String.class);

        JsonObject result = Json.createReader(new StringReader(raw)).readObject();

        token = result.getString("access_token");
        refreshToken = result.getString("refresh_token");
        L.log(Level.INFO, "access_token: {0}", token);
        L.log(Level.INFO, "expires_in: {0}", result.getInt("expires_in"));
        L.log(Level.INFO, "refresh_expires_in: {0}", result.getInt("refresh_expires_in"));
        L.log(Level.INFO, "refresh_token: {0}", refreshToken);
        L.log(Level.INFO, "token_type: {0}", result.getString("token_type"));
        L.log(Level.INFO, "session_state: {0}", result.getString("session_state"));

    }


    
    @After
    public void tearDown() {
    }

    /**
     * Test of validateRequest method, of class MyServerAuthModule.
     */
    @Test @Ignore
    public void testValidateRequest() throws Exception {
        MessageInfo messageInfo = mock(MessageInfo.class);
        Subject clientSubject = new Subject();
        Subject serviceSubject = new Subject();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MessagePolicy requestPolicy = mock(MessagePolicy.class);
        MessagePolicy responsePolicy = mock(MessagePolicy.class);
        CallbackHandler handler = mock(CallbackHandler.class);
        Map options = new HashMap();
        options.put("public.key", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAityxK7yCmX31g+gjNQC3j3q6tTV76BT/jyeRsxUvZXyiiDQGhuH2CqrDF/Y9yA40oSmunVI+3CIOb//XEMxIS6rPyeYDjcOlbKjWS558wi/DtQcHOXzpVzVgeRYzzJlK96gzNzluzSXE5EyoHKX6nIt5fPHYx2RALt6eA+V3SXwqj2ash28j4M6MAvN/aGwaVRKqSzAT5RxLaTpE2ansSQWvyFQmDF7RGKPVaOtELZ7Shaz4ZyLhINVWZfrcPGmqD/0nNWARpv5afS9snjQH2jZu8M4Amhadm7UyKzJOH4nUoFje+mKVCxnB+HQd/WUEDZHoDMy4kUtWz+9Zj/HtFQIDAQAB");
        options.put("algorithm", "RSA");

        when(messageInfo.getRequestMessage()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxRkNSalUyN0VJWk9nUDlEMFlacTdtVlp3YkQ3bWZJZldXUGtLMDhJdnVFIn0.eyJqdGkiOiI0OWQxYzJiYS01NjkxLTQwNzgtYjAzNS01NWNjMzk5NjcxMjUiLCJleHAiOjE1MTc0MjUxMjAsIm5iZiI6MCwiaWF0IjoxNTE3NDI0ODIwLCJpc3MiOiJodHRwOi8vc29wcmFubzo4MTgwL2F1dGgvcmVhbG1zL2hlbGxvLXJlYWxtIiwiYXVkIjoiaGVsbG8td29ybGQiLCJzdWIiOiIxYTIzNTQyYS1jNzYyLTQ4MzgtOWJkMC03NGNhMTZiMzg2NGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJoZWxsby13b3JsZCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjNlYjc1MDNiLTVmOGItNDFmMS1hYWRiLTY3N2NjMGY4MWE0MSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJnZW5lcmFsIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJoZWxsby13b3JsZCI6eyJyb2xlcyI6WyJzb2NpYWwiXX0sIndpbnRlciI6eyJyb2xlcyI6WyJwYXJ0eSJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IkhsdWxhbmkgTWhsb25nbyIsInByZWZlcnJlZF91c2VybmFtZSI6ImhtaGxvbmdvIiwiZ2l2ZW5fbmFtZSI6IkhsdWxhbmkiLCJmYW1pbHlfbmFtZSI6Ik1obG9uZ28iLCJlbWFpbCI6ImhtaGxvbmdvQG5vdmFsaW5jLm5ldCJ9.IgRVh7fy39yLt36bYO-akjsxHKKc84daZz8suKK-ui7bbJbulOMhiLeyJKV4_LMEGgGQPtUt1c47aefXnQyzCkIsIqPkxlMaYR3nQAFHgwO09lLU4-MmXFn6qCv9otMPgmafNTXMkapjwaf61ZDGjruWPRJ_nTFs0CD2yirJtRRuC05SGSOd6Y27qLUhCKuVvqIBgdNsmu2_Qh3Rf0xe2renWZvbJ2GyQmn8ycaNTojZKcya20lVWXKkmxeLN3P8kH3FbC30gBtuuv5QY9DiagxB52GvQoIua5V1a6AvW2aBxAINOkjYbDr8DcYN34JWrlfgtaBXxsb-NPzD9ReyWQ");

        CompanyServerAuthModule instance = new CompanyServerAuthModule();

        instance.initialize(requestPolicy, responsePolicy, handler, options);

        AuthStatus expResult = AuthStatus.SUCCESS;
        AuthStatus result = instance.validateRequest(messageInfo, clientSubject, serviceSubject);

        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
}
