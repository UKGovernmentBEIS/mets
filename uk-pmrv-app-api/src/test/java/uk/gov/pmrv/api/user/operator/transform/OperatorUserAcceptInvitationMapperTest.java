package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorUserAcceptInvitationMapperTest {

	private OperatorUserAcceptInvitationMapper mapper;
	
	@BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorUserAcceptInvitationMapper.class);
    }
	
	@Test
	void toOperatorInvitedUserInfoDTO() {
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation =
            OperatorUserWithAuthorityDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .accountName("accountName")
                .build();
        String roleCode = "roleCode";
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;

        OperatorInvitedUserInfoDTO expectedDto = mapper
            .toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, roleCode, userInvitationStatus);

        assertThat(expectedDto.getFirstName()).isEqualTo(operatorUserAcceptInvitation.getFirstName());
        assertThat(expectedDto.getLastName()).isEqualTo(operatorUserAcceptInvitation.getLastName());
        assertThat(expectedDto.getEmail()).isEqualTo(operatorUserAcceptInvitation.getEmail());
        assertThat(expectedDto.getAccountName()).isEqualTo(operatorUserAcceptInvitation.getAccountName());
        assertThat(expectedDto.getRoleCode()).isEqualTo(roleCode);
        assertThat(expectedDto.getInvitationStatus()).isEqualTo(userInvitationStatus);
    }

    @Test
    void toOperatorUserWithAuthorityDTO() {
        OperatorUserDTO operatorUser = OperatorUserDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .enabled(true)
            .build();

        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .accountId(2L)
            .build();

        String accountName = "accountName";


        OperatorUserWithAuthorityDTO expectedDto = mapper
            .toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo, accountName);

        assertThat(expectedDto.getEmail()).isEqualTo(operatorUser.getEmail());
        assertThat(expectedDto.getFirstName()).isEqualTo(operatorUser.getFirstName());
        assertThat(expectedDto.getLastName()).isEqualTo(operatorUser.getLastName());
        assertThat(expectedDto.getUserAuthorityId()).isEqualTo(authorityInfo.getId());
        assertThat(expectedDto.getAccountName()).isEqualTo(accountName);
        assertThat(expectedDto.isEnabled()).isTrue();
    }
}
