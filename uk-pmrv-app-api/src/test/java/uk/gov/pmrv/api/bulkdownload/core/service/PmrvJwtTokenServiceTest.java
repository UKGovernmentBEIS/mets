package uk.gov.pmrv.api.bulkdownload.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.bulkdownload.core.domain.PmrvJwtTokenAction;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmrvJwtTokenServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtProperties.Claim claimProperties;

    @Mock
    private JwtTokenService jwtTokenService;

    private Clock fixedClock;
    private PmrvJwtTokenService service;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );

        service = new PmrvJwtTokenService(jwtProperties, fixedClock, jwtTokenService);
    }

    @Test
    void resolveTokenClaims_resolvesAllExpectedActions() {
        when(jwtTokenService.resolveTokenActionClaim("token", PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW))
                .thenReturn("ALR");
        when(jwtTokenService.resolveTokenActionClaim("token", PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD))
                .thenReturn("2024");

        Map<PmrvJwtTokenAction, String> result =
                service.resolveTokenClaims(
                        "token",
                        Set.of(
                                PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW,
                                PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD
                        )
                );

        assertThat(result).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW, "ALR",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD, "2024"
                )
        );
    }

    @Test
    void generateToken_throwsException_whenClaimsAreEmpty() {
        assertThatThrownBy(() -> service.generateToken(Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Claims map must not be empty");
    }

    @Test
    void generateToken_createsTokenWithCorrectExpirationAndSubject() {
        when(jwtProperties.getClaim()).thenReturn(claimProperties);
        when(claimProperties.getSecret()).thenReturn("secret");
        when(claimProperties.getIssuer()).thenReturn("issuer");
        when(claimProperties.getAudience()).thenReturn("audience");
        when(claimProperties.getGetFileAttachmentExpIntervalMinutes()).thenReturn(60L);

        Map<PmrvJwtTokenAction, String> claims =
                Map.of(
                        PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW, "ALR",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD, "2024"
                );

        FileToken token = service.generateToken(claims);

        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getTokenExpirationMinutes()).isEqualTo(60L);
    }

    @Test
    void generateToken_buildsSubjectFromDistinctActionSubjects() {
        when(jwtProperties.getClaim()).thenReturn(claimProperties);
        when(claimProperties.getSecret()).thenReturn("secret");
        when(claimProperties.getIssuer()).thenReturn("issuer");
        when(claimProperties.getAudience()).thenReturn("audience");
        when(claimProperties.getGetFileAttachmentExpIntervalMinutes()).thenReturn(60L);

        Map<PmrvJwtTokenAction, String> claims =
                Map.of(
                        PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW, "ALR",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD, "2024"
                );

        FileToken token = service.generateToken(claims);

        DecodedJWT decoded = JWT.decode(token.getToken());

        assertThat(decoded.getSubject())
                .contains(PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW.getSubject())
                .contains(PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD.getSubject());
    }
}