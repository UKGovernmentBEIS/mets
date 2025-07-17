package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;

import java.util.List;

/**
 * The Legal Entity Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface LegalEntityMapper {

    @Mapping(target = "location.address", source = "address")
    LegalEntity toLegalEntity(LegalEntityDTO legalEntityDTO);
    
    @Mapping(target = "address", source = "location.address")
    LegalEntityDTO toLegalEntityDTO(LegalEntity legalEntity);
    
    @Mapping(target = "address", source = "location.address")
    LegalEntityWithoutHoldingCompanyDTO toLegalEntityWithoutHoldingCompanyDTO(LegalEntity legalEntity);

    LegalEntityInfoDTO toLegalEntityInfoDTO(LegalEntity legalEntity);
    
    List<LegalEntityInfoDTO> toLegalEntityInfoDTOs(List<LegalEntity> legalEntities);
    
}
