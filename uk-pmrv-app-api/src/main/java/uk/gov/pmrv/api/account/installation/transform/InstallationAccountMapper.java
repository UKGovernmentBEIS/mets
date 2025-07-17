package uk.gov.pmrv.api.account.installation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.service.AccountEmitterIdGenerator;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;

@Mapper(
    componentModel = "spring",
    uses = {LocationMapper.class, LegalEntityMapper.class},
    config = MapperConfig.class
)
public interface InstallationAccountMapper {

    @Mapping(target = "id", source = "identifier")
    InstallationAccount toInstallationAccount(InstallationAccountDTO accountDTO, Long identifier);

    InstallationAccountDTO toInstallationAccountDTO(InstallationAccount account);

    InstallationAccountWithoutLeHoldingCompanyDTO toInstallationAccountWithoutLeHoldingCompany(InstallationAccount account);

    InstallationAccountInfoDTO toInstallationAccountInfoDTO(InstallationAccount account);

    @AfterMapping
    default void populateAccountEmitterId(@MappingTarget InstallationAccount aviationAccount, Long identifier) {
        aviationAccount.setEmitterId(AccountEmitterIdGenerator.generate(identifier));
    }
}
