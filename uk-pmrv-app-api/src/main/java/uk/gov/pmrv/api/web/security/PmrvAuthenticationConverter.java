package uk.gov.pmrv.api.web.security;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;

import java.util.HashSet;

@RequiredArgsConstructor
public class PmrvAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthorityService authorityService;

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        return new JwtAuthenticationToken(jwt, new HashSet<>(authorityService.getActiveAuthoritiesWithAssignedPermissions(jwt.getSubject())));
    }
}
