package uk.gov.pmrv.api.bulkdownload.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.bulkdownload.core.domain.PmrvJwtTokenAction;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO: move to netz
@Service
@RequiredArgsConstructor
public class PmrvJwtTokenService {

    private final JwtProperties jwtProperties;
    private final Clock clock;
    private final JwtTokenService jwtTokenService;


    public Map<PmrvJwtTokenAction, String> resolveTokenClaims(String token, Set<PmrvJwtTokenAction> expectedActions) {
        return expectedActions.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        action -> jwtTokenService.resolveTokenActionClaim(token, action)
                ));
    }

    public FileToken generateToken(Map<PmrvJwtTokenAction, String> claims) {

        if (claims == null || claims.isEmpty()) {
            throw new IllegalArgumentException("Claims map must not be empty");
        }

        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getClaim().getSecret());
        ZonedDateTime now = ZonedDateTime.now(clock);

        Date issued = Date.from(now.toInstant());

        //Default value
        long expirationMinutes = jwtProperties.getClaim().getGetFileAttachmentExpIntervalMinutes();

        Date expires = Date.from(now.plusMinutes(expirationMinutes).toInstant());

        // Subject: derived from actions
        String subject = buildSubject(claims.keySet());

        JWTCreator.Builder jwtBuilder = JWT.create()
                .withIssuer(jwtProperties.getClaim().getIssuer())
                .withIssuedAt(issued)
                .withAudience(jwtProperties.getClaim().getAudience())
                .withExpiresAt(expires)
                .withSubject(subject);

        // Add claims dynamically
        claims.forEach((action, value) ->
                jwtBuilder.withClaim(action.getClaimName(), value)
        );

        String token = jwtBuilder.sign(algorithm);

        return FileToken.builder()
                .token(token)
                .tokenExpirationMinutes(expirationMinutes)
                .build();
    }

    private String buildSubject(Set<PmrvJwtTokenAction> actions) {
        return actions.stream()
                .map(PmrvJwtTokenAction::getSubject)
                .distinct()
                .collect(Collectors.joining(","));
    }
}
