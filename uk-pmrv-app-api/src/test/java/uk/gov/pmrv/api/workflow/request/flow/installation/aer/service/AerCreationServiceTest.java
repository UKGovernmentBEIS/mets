package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;

@ExtendWith(MockitoExtension.class)
class AerCreationServiceTest {

    @InjectMocks
    private AerCreationService aerCreationService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private AerCreationValidatorService aerCreationValidatorService;

    @Mock
    private AerDueDateService aerDueDateService;

    @Test
    void createRequestAer() {
        Date current = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)));
        Long accountId = 1L;
        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;
        RequestParams params = RequestParams.builder()
                .type(RequestType.AER)
                .accountId(accountId)
                .requestPayload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .build())
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(Year.now().minusYears(1))
                        .initiatorRequest(AerInitiatorRequest.builder().type(requestTypeInitiatedBy).build())
                        .build())
                .processVars(Map.of(BpmnProcessConstants.AER_EXPIRATION_DATE, current))
                .build();

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerDueDateService.generateDueDate())
                .thenReturn(current);

        // Invoke
        aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy);

        // Verify
        verify(startProcessRequestService, times(1)).startProcess(params);
    }

    @Test
    void createRequestAer_throw_account_exception() {
        Long accountId = 1L;
        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AER_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createRequestAer_throw_same_year_exception() {
        Long accountId = 1L;
        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AER_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }
}
