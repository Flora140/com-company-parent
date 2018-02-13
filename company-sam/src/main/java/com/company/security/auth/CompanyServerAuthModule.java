package com.company.security.auth;

import com.auth0.jwt.exceptions.TokenExpiredException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CompanyServerAuthModule implements ServerAuthModule {

    private static final Class[] MESSAGE_TYPES = {HttpServletRequest.class, HttpServletResponse.class};
    private static final Logger L = Logger.getLogger(CompanyServerAuthModule.class.getName());

    private CallbackHandler handler;
    private Authenticator authenticator;

    public CompanyServerAuthModule() {

    }

    /**
     * This constructor is for testing purposes ONLY
     *
     * @param authenticator
     */
    public CompanyServerAuthModule(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        L.log(Level.FINE, "INIT {0}, {1}", new Object[]{requestPolicy, responsePolicy});
        this.handler = handler;

        Object publicKey = options.get("public.key");
        Object algorithm = options.get("algorithm");

        if (publicKey != null) {
            authenticator = Authenticator.newInstance(algorithm, publicKey);
        }
        L.log(Level.FINE, "OPTIONS: {0}", options);
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return MESSAGE_TYPES;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

        long start = System.currentTimeMillis();
        try {
            HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

            String token = getToken("Bearer", request.getHeader("Authorization"));

            if (token == null || token.isEmpty()) {
                L.log(Level.INFO, "No Authorization header for request {0}", request.getRequestURI());
                return AuthStatus.SEND_FAILURE;
            }

            try {
                Callback[] callbacks = authenticator.validateJWT(token, clientSubject);
                handler.handle(callbacks);
            } catch (IOException | UnsupportedCallbackException ex) {
                L.log(Level.FINE, ex.getMessage(), ex);
                return AuthStatus.SEND_FAILURE;
            } catch (TokenExpiredException ex) {
                L.log(Level.FINE, "TOKEN EXPIRED {0}", token);
                return AuthStatus.SEND_FAILURE;
            }
            return AuthStatus.SUCCESS;
        } finally {
            L.log(Level.FINE, "validate request: {0} ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        L.log(Level.FINE, "SECURE_RESPONSE {0}, {1}", new Object[]{messageInfo, serviceSubject});
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        L.log(Level.FINE, "CLEAN_SUBJECT {0}, {1}", new Object[]{messageInfo, subject});

        if (subject != null) {
            subject.getPrincipals().clear();
        }
    }

    private String getToken(String prefix, String raw) {
        if (raw == null) {
            return null;
        }
        return raw.replace(prefix, "").trim();
    }
}
