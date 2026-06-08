package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AerClosedAccountStatusValidatorTest {

    @InjectMocks
    private AerClosedAccountStatusValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;
    
    @Test
    void validateAction_request_not_aer() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED,
                LocalDateTime.now(), null);
        
        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);
        

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, 
        		() -> validator.validateAction(accountId, payload));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.AER_REQUEST_IS_NOT_AER);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verifyNoInteractions(requestCreateValidatorService);
    }

    @Test
    void validateAction_valid() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED);
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), AerRequestMetadata.builder().build());

        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(true)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.findRequestDetailsById(requestId))
                .thenReturn(requestDetailsDTO);

        // Invoke
        final RequestCreateValidationResult result = validator.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isNull();
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, applicableAccountStatuses);
    }
    
    @Test
    void validateAction_not_valid_account_status() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), AerRequestMetadata.builder().build());

        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(false)
        		.reportedAccountStatus(InstallationAccountStatus.NEW)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED)))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.findRequestDetailsById(requestId))
                .thenReturn(requestDetailsDTO);

        // Invoke
        final RequestCreateValidationResult result = validator.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isEqualTo(InstallationAccountStatus.NEW);
        assertThat(result.getApplicableAccountStatuses()).contains(InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED);
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.SURRENDERED,
                        InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED));
    }
    
    @Test
    void validateAction_invalid_request_type() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED);
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.IN_PROGRESS,
                LocalDateTime.now(), AerRequestMetadata.builder().build());

        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(true)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.findRequestDetailsById(requestId))
                .thenReturn(requestDetailsDTO);

        // Invoke
        final RequestCreateValidationResult result = validator.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.AER);
        assertThat(result.getReportedAccountStatus()).isNull();
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, applicableAccountStatuses);
    }

    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(Set.of(InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.REVOKED, InstallationAccountStatus.TRANSFERRED));
    }

    @Test
    void getReferableRequestType() {
        assertThat(validator.getReferableRequestType()).isEqualTo(RequestType.AER);
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.AER);
    }
}
