package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReCreateActionPayload;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesReInitiateValidationServiceTest {

    @InjectMocks
    private WithholdingOfAllowancesReInitiateValidationService service;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private WithholdingOfAllowancesCreateValidator withholdingOfAllowancesCreateValidator;

    @Test
    void validateAction_valid() {
        Long accountId = 1L;
        String requestId = "REQ-ID";
        WithholdingOfAllowancesReCreateActionPayload payload = WithholdingOfAllowancesReCreateActionPayload.builder()
            .requestId(requestId)
            .build();
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.WITHHOLDING_OF_ALLOWANCES, RequestStatus.COMPLETED, LocalDateTime.now(), null);
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(requestDetailsDTO);
        when(withholdingOfAllowancesCreateValidator.getApplicableAccountStatuses()).thenReturn(applicableAccountStatuses);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());

        RequestCreateValidationResult result = service.validateAction(accountId, payload);

        assertThat(result.isValid()).isTrue();
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1)).validateAccountStatuses(accountId, applicableAccountStatuses);
    }

    @Test
    void validateAction_invalid_request_type() {
        Long accountId = 1L;
        String requestId = "REQ-ID";
        WithholdingOfAllowancesReCreateActionPayload payload = WithholdingOfAllowancesReCreateActionPayload.builder()
            .requestId(requestId)
            .build();
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED, LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(requestDetailsDTO);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.validateAction(accountId, payload));

        assertThat(exception.getErrorCode()).isEqualTo(MetsErrorCode.WITHHOLDING_OF_ALLOWANCES_CREATION_NOT_ALLOWED);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
    }

    @Test
    void validateAction_invalid_account_status() {
        Long accountId = 1L;
        String requestId = "REQ-ID";
        WithholdingOfAllowancesReCreateActionPayload payload = WithholdingOfAllowancesReCreateActionPayload.builder()
            .requestId(requestId)
            .build();
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.WITHHOLDING_OF_ALLOWANCES, RequestStatus.COMPLETED, LocalDateTime.now(), null);
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(requestDetailsDTO);
        when(withholdingOfAllowancesCreateValidator.getApplicableAccountStatuses()).thenReturn(applicableAccountStatuses);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(false).reportedAccountStatus(InstallationAccountStatus.SURRENDERED).build());

        RequestCreateValidationResult result = service.validateAction(accountId, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedAccountStatus()).isEqualTo(InstallationAccountStatus.SURRENDERED);
        assertThat(result.getApplicableAccountStatuses()).isEqualTo(applicableAccountStatuses);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1)).validateAccountStatuses(accountId, applicableAccountStatuses);
    }

    @Test
    void validateAction_invalid_request_status() {
        Long accountId = 1L;
        String requestId = "REQ-ID";
        WithholdingOfAllowancesReCreateActionPayload payload = WithholdingOfAllowancesReCreateActionPayload.builder()
            .requestId(requestId)
            .build();
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.WITHHOLDING_OF_ALLOWANCES, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(requestDetailsDTO);
        when(withholdingOfAllowancesCreateValidator.getApplicableAccountStatuses()).thenReturn(applicableAccountStatuses);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());

        RequestCreateValidationResult result = service.validateAction(accountId, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.WITHHOLDING_OF_ALLOWANCES);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1)).validateAccountStatuses(accountId, applicableAccountStatuses);
    }

    @Test
    void getType() {
        assertThat(service.getType()).isEqualTo(RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES_RE_INITIATE);
    }
}
