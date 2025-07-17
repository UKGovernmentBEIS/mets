package uk.gov.pmrv.api.user.operator.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserAcceptInvitationMapper {

	OperatorInvitedUserInfoDTO toOperatorInvitedUserInfoDTO(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO,
			String roleCode, UserInvitationStatus invitationStatus);

	@Mapping(target = "userAuthorityId", source = "authorityInfoDTO.id")
	@Mapping(target = "userId", source = "authorityInfoDTO.userId")
	OperatorUserWithAuthorityDTO toOperatorUserWithAuthorityDTO(OperatorUserDTO operatorUserDTO,
																AuthorityInfoDTO authorityInfoDTO, String accountName);
}
