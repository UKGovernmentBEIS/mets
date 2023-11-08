package uk.gov.pmrv.api.web.security;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.Collections;
import java.util.Map;

import static uk.gov.pmrv.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

@RequiredArgsConstructor
public class RoleTypeClaimConverter implements Converter<Map<String, Object>, Map<String, Object>> {
    private final UserRoleTypeService userRoleTypeService;

    private final MappedJwtClaimSetConverter delegate =
            MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    public Map<String, Object> convert(@NotNull Map<String, Object> claims) {
        Map<String, Object> convertedClaims = this.delegate.convert(claims);
        convertedClaims.put(CLAIM_ROLE_TYPE, getRoleType((String)claims.get(JwtClaimNames.SUB)));
        return convertedClaims;
    }

    private RoleType getRoleType(String userId) {
        return userRoleTypeService.getUserRoleTypeByUserId(userId).getRoleType();
    }
}
