package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AviationAccountSearchResults {

    private List<AviationAccountSearchResultsInfoDTO> accounts;
    private Long total;
}
