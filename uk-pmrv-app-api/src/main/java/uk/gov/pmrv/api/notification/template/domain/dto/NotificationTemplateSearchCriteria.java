package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateSearchCriteria {

    private CompetentAuthorityEnum competentAuthority;
    private AccountType accountType;
    private String term;
    private RoleType roleType;
    private PagingRequest paging;
}
