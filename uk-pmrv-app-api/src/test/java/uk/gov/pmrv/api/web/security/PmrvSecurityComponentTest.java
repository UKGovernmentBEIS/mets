package uk.gov.pmrv.api.web.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.transform.PmrvUserMapper;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

@ExtendWith(MockitoExtension.class)
class PmrvSecurityComponentTest {

    @InjectMocks
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserMapper pmrvUserMapper;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
    }

    @Test
    void getAuthenticatedUser_regulator() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleType.REGULATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims),  authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1)).toPmrvUser("userId", "email", "firstName", "lastName", authorityDTOS, RoleType.REGULATOR);
    }


    @Test
    void getAuthenticatedUser_operator() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .accountId(1L)
            .authorityPermissions(List.of(Permission.PERM_ACCOUNT_USERS_EDIT))
            .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleType.OPERATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims),  authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1)).toPmrvUser("userId", "email", "firstName", "lastName", authorityDTOS, RoleType.OPERATOR);
    }

    @Test
    void getAuthenticatedUser_operator_authority_without_permission() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .accountId(1L)
            .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleType.OPERATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims),  authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1)).toPmrvUser("userId", "email", "firstName", "lastName", List.of(), RoleType.OPERATOR);
    }
}
