package uk.gov.pmrv.api.user.core.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;

/**
 * The User Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "email", source = "username")
    @Mapping(target = "userId", source = "id")
    UserInfoDTO toUserInfoDTO(UserRepresentation userRepresentation);

    @Mapping(target = "userId", source = "userInfo.id")
    UserInfoDTO toUserInfoDTO(UserInfo userInfo);

    UserDTO toUserDTO(UserRepresentation userRepresentation);
}
