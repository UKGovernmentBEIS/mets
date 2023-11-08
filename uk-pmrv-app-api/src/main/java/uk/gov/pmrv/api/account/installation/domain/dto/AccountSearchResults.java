package uk.gov.pmrv.api.account.installation.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountSearchResults {

    private List<AccountSearchResultsInfoDTO> accounts;
    private Long total;
    
}
