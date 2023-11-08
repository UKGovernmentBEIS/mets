package uk.gov.pmrv.api.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.config.KeycloakProperties;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtProperties jwtProperties;
    private final KeycloakProperties keycloakProperties;
    private final Clock clock;

    public String generateToken(JwtTokenActionEnum jwtTokenAction, String claimValue, long expirationInterval) {
        Algorithm algorithm = Algorithm.HMAC256(keycloakProperties.getClientSecret());

        ZonedDateTime now = ZonedDateTime.now(clock);
        Date issued = Date.from(now.toInstant());
        Date expires = Date.from(now.plusMinutes(expirationInterval).toInstant());

        return JWT.create()
                .withIssuer(keycloakProperties.getAuthServerUrl())
                .withIssuedAt(issued)
                .withSubject(jwtTokenAction.getSubject())
                .withClaim(jwtTokenAction.getClaimName(), claimValue)
                .withAudience(jwtProperties.getClaim().getAudience())
                .withExpiresAt(expires)
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(keycloakProperties.getClientSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(keycloakProperties.getAuthServerUrl())
                .withSubject(subject)
                .build();

        try {
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new BusinessException(ErrorCode.VERIFICATION_LINK_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String resolveTokenActionClaim(String token, JwtTokenActionEnum jwtTokenAction) {
        DecodedJWT decodedJwt = verifyToken(token, jwtTokenAction.getSubject());
        return decodedJwt.getClaim(jwtTokenAction.getClaimName()).asString();
    }
}
