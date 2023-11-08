package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@Builder
public class AuthorizationCriteria {
    private Long accountId;
    private CompetentAuthorityEnum competentAuthority;
    private Long verificationBodyId;
    private Permission permission;
}
