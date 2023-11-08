package uk.gov.pmrv.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

/**
 * The authenticated User's applicable accounts.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmrvAuthority {

    private String code;

    private Long accountId;

    private CompetentAuthorityEnum competentAuthority;

    private Long verificationBodyId;

    private List<Permission> permissions;
}
