package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

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
    private AviationAccountReportingStatusType reportingStatus;
    private String emitterId;
}
