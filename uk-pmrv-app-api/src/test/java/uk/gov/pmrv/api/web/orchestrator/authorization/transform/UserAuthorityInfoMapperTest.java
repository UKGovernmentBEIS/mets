package uk.gov.pmrv.api.web.orchestrator.authorization.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UserAuthorityInfoDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAuthorityInfoMapperTest {

    private UserAuthorityInfoMapper userAuthorityInfoMapper = Mappers.getMapper(UserAuthorityInfoMapper.class);

    @Test
    void toUserAuthorityInfo() {
        String userId = "userId";
        String roleName = "roleName";
        String roleCode = "roleName";
        AuthorityStatus authorityStatus = AuthorityStatus.ACTIVE;
        String firstName = "firstName";
        String lastName = "lastName";

        UserAuthorityDTO userAuthority = UserAuthorityDTO.builder()
            .userId(userId)
            .roleName(roleName)
            .roleCode(roleCode)
            .authorityStatus(authorityStatus)
            .build();
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
            .userId(userId)
            .firstName(firstName)
            .lastName(lastName)
            .build();

        UserAuthorityInfoDTO expectedDTO = UserAuthorityInfoDTO.builder()
            .userId(userId)
            .firstName(firstName)
            .lastName(lastName)
            .roleCode(roleCode)
            .roleName(roleName)
            .authorityStatus(authorityStatus)
            .build();

        UserAuthorityInfoDTO actualDTO =
            userAuthorityInfoMapper.toUserAuthorityInfo(userAuthority, userInfoDTO);

        assertEquals(expectedDTO, actualDTO);
    }
}