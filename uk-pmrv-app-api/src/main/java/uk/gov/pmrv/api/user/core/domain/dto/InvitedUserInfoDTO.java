package uk.gov.pmrv.api.user.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedUserInfoDTO {

    private String email;
    private UserInvitationStatus invitationStatus;
    
}
