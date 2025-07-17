package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;

/**
 * The holding company address mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface HoldingCompanyAddressMapper {

    HoldingCompanyAddress toHoldingCompanyAddress(HoldingCompanyAddressDTO holdingCompanyAddressDTO);
}
