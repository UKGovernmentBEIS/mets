package uk.gov.pmrv.api.web.orchestrator.account.installation.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface InstallationAccountHeaderInfoMapper {

    InstallationAccountHeaderInfoDTO toAccountHeaderInfoDTO(InstallationAccountInfoDTO accountInfoDTO);
}
