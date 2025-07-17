package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSearchCriteria {
    
    private String term;
    
    private PagingRequest paging;
}
