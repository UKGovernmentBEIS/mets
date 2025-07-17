package uk.gov.pmrv.api.common.domain.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AddressMapper {

    Address toAddress(AddressDTO addressDto);
}
