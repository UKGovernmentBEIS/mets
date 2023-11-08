package uk.gov.pmrv.api.account.installation.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses = LocationMapper.class)
public interface InstallationAccountToInstallationOperatorDetailsMapper {

    @Mapping(target = "installationName", source = "name")
    @Mapping(target = "installationLocation", source = "location")
    @Mapping(target = "operator", source = "legalEntity.name")
    @Mapping(target = "operatorType", source = "legalEntity.type")
    @Mapping(target = "companyReferenceNumber", source = "legalEntity.referenceNumber")
    @Mapping(target = "operatorDetailsAddress", source = "legalEntity.address")
    InstallationOperatorDetails toPermitInstallationOperatorDetails(InstallationAccountWithoutLeHoldingCompanyDTO accountDTO);

    @Mapping(target = "installationName", source = "name")
    @Mapping(target = "installationLocation", source = "location")
    @Mapping(target = "operator", source = "legalEntity.name")
    @Mapping(target = "operatorType", source = "legalEntity.type")
    @Mapping(target = "companyReferenceNumber", source = "legalEntity.referenceNumber")
    @Mapping(target = "operatorDetailsAddress.line1", source = "legalEntity.location.address.line1")
    @Mapping(target = "operatorDetailsAddress.line2", source = "legalEntity.location.address.line2")
    @Mapping(target = "operatorDetailsAddress.city", source = "legalEntity.location.address.city")
    @Mapping(target = "operatorDetailsAddress.country", source = "legalEntity.location.address.country")
    @Mapping(target = "operatorDetailsAddress.postcode", source = "legalEntity.location.address.postcode")
    InstallationOperatorDetails toPermitInstallationOperatorDetails(Account account);
}
