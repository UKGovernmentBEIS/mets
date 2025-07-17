package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AviationAccountInfoDTO {

    private Long id;
    private AccountType accountType;
    private Integer registryId;
    private CompetentAuthorityEnum competentAuthority;
    private EmissionTradingScheme emissionTradingScheme;
    private String name;
    private String crcoCode;
    private LocationDTO location;
    private AviationAccountStatus status;
    private AviationAccountReportingStatus reportingStatus;
    private String emitterId;
}
