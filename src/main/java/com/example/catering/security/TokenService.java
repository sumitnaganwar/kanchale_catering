package com.example.catering.security;

import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private final ObjectMapper mapper;
    private final byte[] secret;

    public TokenService(ObjectMapper mapper, @Value("${app.jwt.secret}") String secret) {
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) throw new IllegalArgumentException("app.jwt.secret must be at least 32 bytes");
    }
    public String issue(String username) {
        try {
            String header = ENCODER.encodeToString(mapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = ENCODER.encodeToString(mapper.writeValueAsBytes(Map.of("sub", username, "role", "ADMIN", "exp", Instant.now().plusSeconds(3600).getEpochSecond())));
            String body = header + "." + payload;
            return body + "." + ENCODER.encodeToString(sign(body));
        } catch (Exception exception) { throw new IllegalStateException("Could not issue authentication token", exception); }
    }
    public String verifyAndGetSubject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !MessageDigest.isEqual(sign(parts[0] + "." + parts[1]), DECODER.decode(parts[2]))) return null;
            Map<?, ?> claims = mapper.readValue(DECODER.decode(parts[1]), Map.class);
            if (!"ADMIN".equals(claims.get("role")) || !(claims.get("exp") instanceof Number expiry) || expiry.longValue() <= Instant.now().getEpochSecond()) return null;
            return claims.get("sub") instanceof String subject ? subject : null;
        } catch (Exception exception) { return null; }
    }
    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
}
