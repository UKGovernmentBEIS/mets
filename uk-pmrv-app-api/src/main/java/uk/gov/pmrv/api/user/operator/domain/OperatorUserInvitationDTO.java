package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.authorization.core.domain.dto.RoleCode;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;

/**
 * Data transfer object used to add an operator user to an account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OperatorUserInvitationDTO extends UserDTO {

    @RoleCode(roleType = RoleTypeConstants.OPERATOR)
    private String roleCode;
}
