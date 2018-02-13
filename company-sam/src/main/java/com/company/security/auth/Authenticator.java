package com.company.security.auth;

import com.auth0.jwt.algorithms.Algorithm;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;

public class Authenticator {

    private Algorithm algorithm;
    private static final Logger L = Logger.getLogger(Authenticator.class.getName());

    private Authenticator() throws AuthException {
        this(null, null);
    }

    private Authenticator(Object algorithm, Object key) throws AuthException {

        if (algorithm == null || key == null) {
            L.warning(" algorithm / key not specified");
            return;
        }

        this.algorithm = getAlgorithm(algorithm.toString(), key.toString());
    }

    public static Authenticator newInstance(Object algorithm, Object key) throws AuthException {
        return new Authenticator(algorithm, key);
    }

    public static Authenticator newInstance() throws AuthException {
        return new Authenticator();
    }

    public Callback[] validateJWT(String token, Subject clientSubject) {

        L.log(Level.INFO, "DO ALL THE JWT MAGIC HERE EXTRACT STUFF FROM: {0}", token);
        L.warning("This method is far from completion, fill in the blanks...");

        String[] roles = {"general"}; // parsed roles from token
        String subject = "dummy"; // Principal

        return new Callback[]{
            new CallerPrincipalCallback(clientSubject, subject),
            new GroupPrincipalCallback(clientSubject, roles)};
    }

    private Algorithm getAlgorithm(String algorithm, String key) throws AuthException {
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);

            switch (algorithm) {
                case "RSA":
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                    return Algorithm.RSA256(pubKey, null);
                case "HMAC":
                    return Algorithm.HMAC256(key);
                default:
                    throw new AuthException("'" + algorithm + "' is currently not supported");
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | UnsupportedEncodingException ex) {
            L.log(Level.FINE, ex.getMessage(), ex);
            throw new AuthException("There was a problem obtaining algorithm '" + algorithm + "'");
        }
    }
}
