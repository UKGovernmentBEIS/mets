package uk.gov.pmrv.api.account.installation.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;

class InstallationAccountMapperTest {

    private final InstallationAccountMapper mapper = Mappers.getMapper(InstallationAccountMapper.class);
    private final LegalEntityMapper legalEntityMapper = Mappers.getMapper(LegalEntityMapper.class);
    private final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(mapper, "legalEntityMapper", legalEntityMapper);
        ReflectionTestUtils.setField(mapper, "locationMapper", locationMapper);
    }

    @Test
    void toInstallationAccount() {
        Long identifier = 128L;
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .status(InstallationAccountStatus.UNAPPROVED)
            .siteName("siteName")
            .location(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("grid")
                .address(AddressDTO.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntityDTO.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .build())
            .build();

        InstallationAccount result = mapper.toInstallationAccount(accountDTO, identifier);

        assertThat(result).isEqualTo(InstallationAccount.builder()
            .id(128L)
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .status(InstallationAccountStatus.UNAPPROVED)
            .siteName("siteName")
            .location(LocationOnShore.builder()
                .gridReference("grid")
                .address(Address.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntity.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .build())
            .emitterId("EM00128")
            .build());
    }

    @Test
    void toInstallationAccountDTO() {
        InstallationAccount account = InstallationAccount.builder()
            .id(1L)
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .status(InstallationAccountStatus.DEEMED_WITHDRAWN)
            .siteName("siteName")
            .location(LocationOnShore.builder()
                .gridReference("grid")
                .address(Address.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntity.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .location(LocationOnShore.builder()
                    .address(Address.builder()
                        .city("city2")
                        .line1("line2")
                        .build())
                    .build())
                .build())
            .sopId(1L)
            .registryId(2)
            .emitterType(EmitterType.GHGE)
            .installationCategory(InstallationCategory.A)
            .transferCode("123456789")
            .faStatus(Boolean.FALSE)
            .build();

        InstallationAccountDTO result = mapper.toInstallationAccountDTO(account);

        assertThat(result).isEqualTo(InstallationAccountDTO.builder()
            .id(1L)
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .status(InstallationAccountStatus.DEEMED_WITHDRAWN)
            .siteName("siteName")
            .location(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("grid")
                .address(AddressDTO.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntityDTO.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .address(AddressDTO.builder()
                    .city("city2")
                    .line1("line2")
                    .build())
                .build())
            .emitterType(EmitterType.GHGE)
            .installationCategory(InstallationCategory.A)
            .applicationType(ApplicationType.NEW_PERMIT)
            .transferCode("123456789")
            .sopId(1L)
            .registryId(2)
            .faStatus(Boolean.FALSE)
            .build());
    }

    @Test
    void toInstallationAccountWithoutLeHoldingCompany() {
        InstallationAccount account = InstallationAccount.builder()
            .id(1L)
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .status(InstallationAccountStatus.DEEMED_WITHDRAWN)
            .siteName("siteName")
            .location(LocationOnShore.builder()
                .gridReference("grid")
                .address(Address.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntity.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .location(LocationOnShore.builder()
                    .address(Address.builder()
                        .city("city2")
                        .line1("line2")
                        .build())
                    .build())
                .build())
            .sopId(1L)
            .registryId(2)
            .emitterType(EmitterType.GHGE)
            .installationCategory(InstallationCategory.A)
            .transferCode("123456789")
            .faStatus(Boolean.FALSE)
            .build();

        InstallationAccountWithoutLeHoldingCompanyDTO result = mapper.toInstallationAccountWithoutLeHoldingCompany(account);

        assertThat(result).isEqualTo(InstallationAccountWithoutLeHoldingCompanyDTO.builder()
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .siteName("siteName")
            .location(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("grid")
                .address(AddressDTO.builder()
                    .city("city1")
                    .line1("line1")
                    .build())
                .build())
            .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                .name("lename")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("refnum")
                .address(AddressDTO.builder()
                    .city("city2")
                    .line1("line2")
                    .build())
                .build())
            .emitterType(EmitterType.GHGE)
            .installationCategory(InstallationCategory.A)
            .faStatus(Boolean.FALSE)
            .build());
    }

    @Test
    void toInstallationAccountInfoDTO() {
        Long accountId = 1L;
        String name = "name";
        InstallationAccountStatus status = InstallationAccountStatus.TRANSFERRED;
        AccountType accountType = AccountType.INSTALLATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;
        String transferCode = "code";

        InstallationAccount account = InstallationAccount.builder()
            .id(accountId)
            .name(name)
            .status(InstallationAccountStatus.TRANSFERRED)
            .accountType(accountType)
            .competentAuthority(competentAuthority)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .transferCode(transferCode)
            .faStatus(Boolean.FALSE)
            .build();

        InstallationAccountInfoDTO result = mapper.toInstallationAccountInfoDTO(account);

        assertThat(result).isEqualTo(InstallationAccountInfoDTO.builder()
            .id(accountId)
            .name(name)
            .status(status)
            .accountType(accountType)
            .competentAuthority(competentAuthority)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .transferCode(transferCode)
            .faStatus(Boolean.FALSE)
            .build());
    }
}