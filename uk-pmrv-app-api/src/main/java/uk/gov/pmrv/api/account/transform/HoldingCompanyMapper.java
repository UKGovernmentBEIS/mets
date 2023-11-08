package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;

/**
 * Mapper between {@link uk.gov.pmrv.api.account.domain.HoldingCompany}
 * and{@link uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO}
 */
@Mapper(componentModel = "spring")
public interface HoldingCompanyMapper {
    HoldingCompany toHoldingCompany(HoldingCompanyDTO holdingCompanyDTO);
}
