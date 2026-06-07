package conference.service.microservice.util;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

public final class JwtClaimsUtil {

    private JwtClaimsUtil() {
    }

    public static UUID extractUserId(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        if (userId == null || userId.isBlank()) {
            userId = jwt.getSubject();
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El token JWT no incluye identificador de usuario");
        }
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El identificador de usuario del JWT no es un UUID válido", ex);
        }
    }
}
