package uk.gov.pmrv.api.web.orchestrator.authorization.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.mireport.system.accountuserscontacts.OperatorUserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.common.DateTimeFormat;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UserAuthorityInfoDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserAuthorityInfoMapper {

    @Mapping(target = "userId", source = "userInfo.userId")
    @Mapping(target = "authorityCreationDate", source = "userAuthority.authorityCreationDate")
    UserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, UserInfoDTO userInfo);

    @Mapping(target = "userId", source = "operatorUserInfoDTO.id")
    @Mapping(target = "authorityCreationDate", source = "userAuthority.authorityCreationDate")
    @Mapping(target = "lastLoginDate", expression = "java(formatLastLoginDate(operatorUserInfoDTO.getLastLoginDate()))")
    UserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, OperatorUserInfoDTO operatorUserInfoDTO);

    default String formatLastLoginDate(String lastLoginDate) {
        if (lastLoginDate == null) return null;
        try {
            return LocalDateTime.parse(lastLoginDate, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormat.DEFAULT_DATE_TIME.getFormatter());
        } catch (Exception e) {
            return lastLoginDate;
        }
    }
}
