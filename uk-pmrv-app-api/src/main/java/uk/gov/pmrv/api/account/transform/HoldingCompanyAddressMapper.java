package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

/**
 * The holding company address mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface HoldingCompanyAddressMapper {

    HoldingCompanyAddress toHoldingCompanyAddress(HoldingCompanyAddressDTO holdingCompanyAddressDTO);
}
