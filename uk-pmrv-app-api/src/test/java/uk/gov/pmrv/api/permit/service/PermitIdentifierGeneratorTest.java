package uk.gov.pmrv.api.permit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitIdentifierGeneratorTest {

    private static final Long TEST_ACCOUNT_ID = 1L;

    @InjectMocks
    private PermitIdentifierGenerator cut;

    @Mock
    private ApprovedInstallationAccountQueryService approvedInstallationAccountService;

    @Test
    void shouldGeneratePermitIdForInstallationInEngland() {
        InstallationAccountInfoDTO accountInfoDTO = InstallationAccountInfoDTO.builder()
            .id(TEST_ACCOUNT_ID)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .accountType(AccountType.INSTALLATION)
            .build();
        when(approvedInstallationAccountService.getApprovedAccountById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(accountInfoDTO));
        String permitId = cut.generate(TEST_ACCOUNT_ID);

        assertThat(permitId).isEqualTo("UK-E-IN-00001");
    }
}
