package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalEntityWithoutHoldingCompanyDTO {

	private String name;
    private LegalEntityType type;
    private String referenceNumber;
    private AddressDTO address;
}
