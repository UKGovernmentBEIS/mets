package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateSearchCriteria {

    private CompetentAuthorityEnum competentAuthority;
    private AccountType accountType;
    private String term;
    private String roleType;
    private PagingRequest paging;
}
