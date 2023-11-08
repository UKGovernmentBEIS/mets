package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.ApprovedAviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsMonitoringPlanIdentifierGeneratorTest {

    @InjectMocks
    private EmissionsMonitoringPlanIdentifierGenerator generator;

    @Mock
    private ApprovedAviationAccountQueryService aviationAccountQueryService;

    @Test
    void generate() {
        Long accountId = 1L;
        AviationAccountInfoDTO accountInfoDTO = AviationAccountInfoDTO.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build();

        String expectedIdentifier = "UK-E-AV-00001";

        when(aviationAccountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(accountInfoDTO));

        assertEquals(expectedIdentifier, generator.generate(accountId));
    }

    @Test
    void generate_no_approved_account() {
        Long accountId = 1L;

        when(aviationAccountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> generator.generate(accountId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }
}