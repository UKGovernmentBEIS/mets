package uk.gov.pmrv.api.authorization.rules.services.resource;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@Builder
public class ResourceCriteria {
    
    private Long accountId;
    private CompetentAuthorityEnum competentAuthority;
    private Long verificationBodyId;
    
}
