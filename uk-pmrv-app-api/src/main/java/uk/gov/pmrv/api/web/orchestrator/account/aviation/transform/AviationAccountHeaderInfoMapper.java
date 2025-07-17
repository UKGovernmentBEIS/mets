package uk.gov.pmrv.api.web.orchestrator.account.aviation.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAccountHeaderInfoMapper {

    AviationAccountHeaderInfoDTO toAccountHeaderInfoDTO(AviationAccountInfoDTO accountInfoDTO, String empId);
}
