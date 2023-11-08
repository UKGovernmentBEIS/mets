package uk.gov.pmrv.api.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.config.KeycloakProperties;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {
    @InjectMocks
    private JwtTokenService jwtTokenService;
    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private KeycloakProperties keycloakProperties;

    @Spy
    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    @Test
    void generateToken() {
        String EMAIL = "email@email";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;

        //mock
        when(keycloakProperties.getClientSecret()).thenReturn("secret");
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getAudience()).thenReturn("jwtAudience");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        //invoke
        jwtTokenService.generateToken(tokenAction, EMAIL, 5L);

        //verify mocks
        verify(jwtProperties, times(1)).getClaim();
        verify(claim, times(1)).getAudience();
    }

    @Test
    void verifyToken() {
        final JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;
        when(keycloakProperties.getClientSecret()).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        // Mock token
        ZonedDateTime now = ZonedDateTime.now(fixedClock);
        String token = JWT.create()
                .withIssuer("authServerUrl")
                .withIssuedAt(Date.from(now.toInstant()))
                .withSubject(tokenAction.getSubject())
                .withAudience("jwtAudience")
                .withExpiresAt(
                        Date.from(ZonedDateTime.now(fixedClock).plusMinutes(1000L).toInstant()))
                .sign(Algorithm.HMAC256("secret"));

        DecodedJWT decodedJwt = jwtTokenService.verifyToken(token, tokenAction.getSubject());

        assertEquals(tokenAction.getSubject(), decodedJwt.getSubject());
    }

    @Test
    void verifyTokenExpired() {
        final String tokenSubject = "email@email";

        //mock
        when(keycloakProperties.getClientSecret()).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        String token = JWT.create()
                .withIssuer("authServerUrl")
                .withIssuedAt(
                        Date.from(ZonedDateTime.now(fixedClock).minusDays(10).toInstant()))
                .withSubject(tokenSubject)
                .withAudience("jwtAudience")
                .withExpiresAt(
                        Date.from(ZonedDateTime.now(fixedClock).minusDays(5).toInstant()))
                .sign(Algorithm.HMAC256("secret"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtTokenService.verifyToken(token, tokenSubject);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_LINK_EXPIRED);
    }

    @Test
    void verifyTokenInvalid() {
        final JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;

        //mock
        when(keycloakProperties.getClientSecret()).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtTokenService.verifyToken("xxxx", tokenAction.getSubject());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    void resolveTokenActionClaim() {
    }
}