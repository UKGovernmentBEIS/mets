package uk.gov.pmrv.api.web.orchestrator.authorization.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UserAuthorityInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserAuthorityInfoMapper {

    @Mapping(target = "userId", source = "userInfo.userId")
    @Mapping(target = "authorityCreationDate", source = "userAuthority.authorityCreationDate")
    UserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, UserInfoDTO userInfo);
}
