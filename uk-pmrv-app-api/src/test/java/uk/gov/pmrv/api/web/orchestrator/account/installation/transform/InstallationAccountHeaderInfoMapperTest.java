package uk.gov.pmrv.api.web.orchestrator.account.installation.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.web.orchestrator.account.installation.transform.InstallationAccountHeaderInfoMapper;

import static org.assertj.core.api.Assertions.assertThat;

class InstallationAccountHeaderInfoMapperTest {

    private final InstallationAccountHeaderInfoMapper mapper = Mappers.getMapper(InstallationAccountHeaderInfoMapper.class);

    @Test
    void toAccountHeaderInfoDTO() {
        InstallationAccountInfoDTO accountDTO = InstallationAccountInfoDTO.builder()
            .id(1L)
            .name("name")
            .accountType(AccountType.INSTALLATION)
            .status(InstallationAccountStatus.DEEMED_WITHDRAWN)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emitterType(EmitterType.HSE)
            .installationCategory(InstallationCategory.N_A)
            .build();

        InstallationAccountHeaderInfoDTO result = mapper.toAccountHeaderInfoDTO(accountDTO);

        assertThat(result).isEqualTo(InstallationAccountHeaderInfoDTO.builder()
            .id(accountDTO.getId())
            .name("name")
            .status(InstallationAccountStatus.DEEMED_WITHDRAWN)
            .emitterType(EmitterType.HSE)
            .installationCategory(InstallationCategory.N_A)
            .build());
    }
}