package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;


import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRReInitiateValidationServiceTest {

    @InjectMocks
    private BDRReInitiateValidationService validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;


    @Test
    void validateAction_requestNotBDR_throwBusinessException() {
        final Long accountId = 1L;
        final String requestId = "BDR00000-2025";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED,
                LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);

        BusinessException ex = assertThrows(BusinessException.class,
        		() -> validator.validateAction(accountId, payload));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.BDR_REQUEST_IS_NOT_BDR);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verifyNoInteractions(requestCreateValidatorService);
    }

    @Test
    void validateAction_invalidAccountStatus_throwBusinessException() {
        final Long accountId = 1L;
        final String requestId = "BDR00000-2025";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.BDR, RequestStatus.COMPLETED,
                LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
        	.thenReturn(RequestCreateAccountStatusValidationResult
                    .builder()
                    .reportedAccountStatus(InstallationAccountStatus.NEW)
                    .valid(false)
                    .build());

        RequestCreateValidationResult validationResult = validator.validateAction(accountId, payload);

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getReportedAccountStatus()).isEqualTo(InstallationAccountStatus.NEW);
        assertThat(validationResult.getApplicableAccountStatuses()).containsExactly(InstallationAccountStatus.LIVE);


        verify(requestQueryService, times(1))
                .findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
    }

    @Test
    void validateAction_invalidRequestStatus_throwBusinessException() {
        final Long accountId = 1L;
        final String requestId = "BDR00000-2025";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.BDR, RequestStatus.IN_PROGRESS,
                LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
        	.thenReturn(RequestCreateAccountStatusValidationResult
                    .builder()
                    .reportedAccountStatus(InstallationAccountStatus.LIVE)
                    .valid(true)
                    .build());

        RequestCreateValidationResult validationResult = validator.validateAction(accountId, payload);

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getReportedRequestTypes()).containsExactly(RequestType.BDR);


        verify(requestQueryService, times(1))
                .findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
    }

    @Test
    void validateAction() {
        final Long accountId = 1L;
        final String requestId = "BDR00000-2025";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.BDR, RequestStatus.COMPLETED,
                LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
        	.thenReturn(RequestCreateAccountStatusValidationResult
                    .builder()
                    .reportedAccountStatus(InstallationAccountStatus.LIVE)
                    .valid(true)
                    .build());

        RequestCreateValidationResult validationResult = validator.validateAction(accountId, payload);

        assertThat(validationResult.isValid()).isTrue();

        verify(requestQueryService, times(1))
                .findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.BDR);
    }
}
