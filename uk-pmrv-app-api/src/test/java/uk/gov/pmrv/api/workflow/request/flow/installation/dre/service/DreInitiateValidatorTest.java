package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
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

@ExtendWith(MockitoExtension.class)
class DreInitiateValidatorTest {

	@InjectMocks
    private DreInitiateValidator cut;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;
    
    @Test
    void getApplicableAccountStatuses() {
        assertThat(cut.getApplicableAccountStatuses()).isEqualTo(Set.of(
				InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER));
    }

    @Test
    void getReferableRequestType() {
        assertThat(cut.getReferableRequestType()).isEqualTo(RequestType.AER);
    }

    @Test
    void getType() {
        assertThat(cut.getType()).isEqualTo(RequestCreateActionType.DRE);
    }
    
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
        		() -> cut.validateAction(accountId, payload));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AER_REQUEST_IS_NOT_AER);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verifyNoMoreInteractions(requestQueryService);
        verifyNoInteractions(requestCreateValidatorService);
    }
    
    @Test
    void validateAction_valid() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final Year year = Year.of(2023);
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER);
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), AerRequestMetadata.builder().year(year).build());

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);
        
        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(true)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE, RequestStatus.IN_PROGRESS, accountId, year))
        	.thenReturn(false);

        // Invoke
        final RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isNull();
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestQueryService, times(1)).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE,
				RequestStatus.IN_PROGRESS, accountId, year);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, applicableAccountStatuses);
    }
    
    @Test
    void validateAction_not_valid_account_status() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final Year year = Year.of(2023);
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), AerRequestMetadata.builder().year(year).build());
        
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER);
        
        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(false)
        		.reportedAccountStatus(InstallationAccountStatus.NEW)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.findRequestDetailsById(requestId))
                .thenReturn(requestDetailsDTO);
        
        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE, RequestStatus.IN_PROGRESS, accountId, year))
    		.thenReturn(false);

        // Invoke
        final RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isEqualTo(InstallationAccountStatus.NEW);
        assertThat(result.getApplicableAccountStatuses()).isEqualTo(applicableAccountStatuses);
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, applicableAccountStatuses);
    }
    
    @Test
    void validateAction_throws_exception_metadata_not_found() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER);
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), null);

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);
        
        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(true)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, 
        		() -> cut.validateAction(accountId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verifyNoMoreInteractions(requestQueryService);
        verify(requestCreateValidatorService, times(1)).validateAccountStatuses(accountId, applicableAccountStatuses);
    }
    
    @Test
    void validateAction_not_valid_request_type() {
        final Long accountId = 1L;
        final String requestId = "AEM-1";
        final Year year = Year.of(2023);
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(requestId)
                .build();
        final Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER);
        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.AER, RequestStatus.COMPLETED,
                LocalDateTime.now(), AerRequestMetadata.builder().year(year).build());

        when(requestQueryService.findRequestDetailsById(requestId))
        	.thenReturn(requestDetailsDTO);
        
        RequestCreateAccountStatusValidationResult accountStatusValidationResult = RequestCreateAccountStatusValidationResult.builder()
        		.valid(true)
        		.build();
        when(requestCreateValidatorService.validateAccountStatuses(accountId, applicableAccountStatuses))
                .thenReturn(accountStatusValidationResult);
        
        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE, RequestStatus.IN_PROGRESS, accountId, year))
        	.thenReturn(true);

        // Invoke
        final RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.DRE);
        assertThat(result.getReportedAccountStatus()).isNull();
        
        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
        verify(requestQueryService, times(1)).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE,
				RequestStatus.IN_PROGRESS, accountId, year);
        verify(requestCreateValidatorService, times(1))
                .validateAccountStatuses(accountId, applicableAccountStatuses);
    }
}
