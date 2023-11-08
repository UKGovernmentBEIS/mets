package uk.gov.pmrv.api.authorization.core.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAuthorityMapperTest {

    private UserAuthorityMapper userAuthorityMapper = Mappers.getMapper(UserAuthorityMapper.class);

    @Test
    void toUserAuthority_non_editable() {
        String userId = "userId";
        AuthorityStatus authorityStatus = AuthorityStatus.ACTIVE;
        String roleName = "roleName";
        String roleCode = "roleCode";
        AuthorityRoleDTO authorityRoleDTO = AuthorityRoleDTO.builder()
            .userId(userId)
            .authorityStatus(authorityStatus)
            .roleCode(roleCode)
            .roleName(roleName)
            .build();

        UserAuthorityDTO expected = UserAuthorityDTO.builder()
            .userId(userId)
            .roleCode(roleCode)
            .roleName(roleName)
            .build();

        UserAuthorityDTO actual = userAuthorityMapper.toUserAuthority(authorityRoleDTO, false);

        assertEquals(expected, actual);
    }

    @Test
    void toUserAuthority_editable() {
        String userId = "userId";
        AuthorityStatus authorityStatus = AuthorityStatus.ACTIVE;
        String roleName = "roleName";
        String roleCode = "roleCode";
        AuthorityRoleDTO authorityRoleDTO = AuthorityRoleDTO.builder()
            .userId(userId)
            .authorityStatus(authorityStatus)
            .roleCode(roleCode)
            .roleName(roleName)
            .build();

        UserAuthorityDTO expected = UserAuthorityDTO.builder()
            .userId(userId)
            .authorityStatus(authorityStatus)
            .roleCode(roleCode)
            .roleName(roleName)
            .build();

        UserAuthorityDTO actual = userAuthorityMapper.toUserAuthority(authorityRoleDTO, true);

        assertEquals(expected, actual);
    }
}