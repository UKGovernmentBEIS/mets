package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;

@Getter
@EqualsAndHashCode
public class AviationAccountSearchResultsInfoDTO {

    private Long id;
    private String name;
    private String emitterId;
    private AviationAccountStatus status;


    public AviationAccountSearchResultsInfoDTO(Long id, String name, String emitterId, String status) {
        this.id = id;
        this.name = name;
        this.emitterId = emitterId;
        this.status = AviationAccountStatus.valueOf(status);
    }
}
