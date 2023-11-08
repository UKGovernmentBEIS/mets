package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplateSearchCriteria {

    private CompetentAuthorityEnum competentAuthority;
    private AccountType accountType;
    private String term;
    private PagingRequest paging;
}
