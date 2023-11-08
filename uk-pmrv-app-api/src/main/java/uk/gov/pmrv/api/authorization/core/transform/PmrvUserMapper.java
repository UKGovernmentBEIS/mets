package uk.gov.pmrv.api.authorization.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.transform.MapperConfig;

import java.util.List;

/**
 * The AuthenticatedUser Mapper, mapping authority to authenticated user.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PmrvUserMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "roleType", source = "roleType")
    PmrvUser toPmrvUser(String userId, String email, String firstName, String lastName, List<AuthorityDTO> authorities, RoleType roleType);

    @Mapping(target = "permissions", source = "authorityPermissions")
    PmrvAuthority toPmrvAuthority(AuthorityDTO authorityDTO);
}
