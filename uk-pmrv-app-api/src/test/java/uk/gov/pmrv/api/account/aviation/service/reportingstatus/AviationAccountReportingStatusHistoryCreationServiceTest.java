package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingExemptEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingRequiredEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusHistoryCreationServiceTest {

    @InjectMocks
    private AviationAccountReportingStatusHistoryCreationService service;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private ApplicationEventPublisher publisher;
    
    @Test
    void submitReportingStatus_to_requiredToReport() {
        Long accountId = 1L;
		final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO = AviationAccountReportingStatusHistoryCreationDTO
				.builder().status(AviationAccountReportingStatus.REQUIRED_TO_REPORT).reason("reason").build();
        final PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();
		final AviationAccount aviationAccount = createAviationAccount(accountId,
				AviationAccountReportingStatus.EXEMPT_COMMERCIAL, List.of());
        
        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.of(aviationAccount));

        service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, pmrvUser);
        
        assertThat(aviationAccount.getReportingStatus()).isEqualTo(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
        assertThat(aviationAccount.getReportingStatusHistoryList()).hasSize(1);
        assertThat(aviationAccount.getReportingStatusHistoryList().get(0).getReason()).isEqualTo("reason");
        assertThat(aviationAccount.getReportingStatusHistoryList().get(0).getStatus()).isEqualTo(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
        assertThat(aviationAccount.getReportingStatusHistoryList().get(0).getSubmitterId()).isEqualTo(pmrvUser.getUserId());
        assertThat(aviationAccount.getReportingStatusHistoryList().get(0).getSubmitterName()).isEqualTo(pmrvUser.getFullName());
        
        verify(aviationAccountRepository, times(1)).findById(accountId);
        verify(publisher, times(1)).publishEvent(Mockito.isA(AviationAccountReportingRequiredEvent.class));
    }

    @Test
    void submitReportingStatus_from_requiredToReport() {
        Long accountId = 1L;
		final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO = AviationAccountReportingStatusHistoryCreationDTO
				.builder().status(AviationAccountReportingStatus.EXEMPT_COMMERCIAL).reason("reason").build();
		final PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).userId("userId")
				.firstName("first name").lastName("last name").build();
		final AviationAccount aviationAccount = createAviationAccount(accountId,
				AviationAccountReportingStatus.REQUIRED_TO_REPORT,
				new ArrayList<>(List.of(createReportingStatusHistory(accountId,
						AviationAccountReportingStatus.REQUIRED_TO_REPORT, "reason1"))));
        
        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.of(aviationAccount));

        service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, pmrvUser);
        
        assertThat(aviationAccount.getReportingStatus()).isEqualTo(AviationAccountReportingStatus.EXEMPT_COMMERCIAL);
        assertThat(aviationAccount.getReportingStatusHistoryList()).hasSize(2);
        
        verify(aviationAccountRepository, times(1)).findById(accountId);
        verify(publisher, times(1)).publishEvent(Mockito.isA(AviationAccountReportingExemptEvent.class));
    }


    @Test
    void submitReportingStatus_neither_from_nor_to_requiredToReport() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO =
            AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL).reason("reason").build();
        final PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();
        final AviationAccount aviationAccount = createAviationAccount(accountId,
				AviationAccountReportingStatus.EXEMPT_COMMERCIAL,
				new ArrayList<>(List.of(createReportingStatusHistory(accountId,
						AviationAccountReportingStatus.EXEMPT_COMMERCIAL, "reason1"))));
        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.of(aviationAccount));

        service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, pmrvUser);
        
        assertThat(aviationAccount.getReportingStatus()).isEqualTo(AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL);
        assertThat(aviationAccount.getReportingStatusHistoryList()).hasSize(2);
        verify(aviationAccountRepository, times(1)).findById(accountId);
        verifyNoInteractions(publisher);
    }

    @Test
    void submitReportingStatus_account_not_found() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatus.REQUIRED_TO_REPORT).reason("reason").build();
        final PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();

        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, pmrvUser));
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(aviationAccountRepository, times(1)).findById(accountId);
    }

    @Test
    void submitReportingStatus_reporting_status_not_changed() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatus.REQUIRED_TO_REPORT).reason("reason").build();
        final PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();
        final AviationAccount aviationAccount = createAviationAccount(accountId,
				AviationAccountReportingStatus.REQUIRED_TO_REPORT,
				new ArrayList<>(List.of(createReportingStatusHistory(accountId,
						AviationAccountReportingStatus.REQUIRED_TO_REPORT, "reason1"))));
        
        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.of(aviationAccount));
        
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, pmrvUser));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AVIATION_ACCOUNT_REPORTING_STATUS_NOT_CHANGED);
        verify(aviationAccountRepository, times(1)).findById(accountId);
    }

    private AviationAccount createAviationAccount(Long accountId, AviationAccountReportingStatus reportingStatus, List<AviationAccountReportingStatusHistory> reportingStatusEntries) {
        return AviationAccount.builder()
                .id(accountId)
                .name("name")
                .crcoCode("crco code")
                .status(AviationAccountStatus.LIVE)
                .reportingStatus(reportingStatus)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .accountType(AccountType.AVIATION)
                .reportingStatusHistoryList(reportingStatusEntries.isEmpty() ? new ArrayList<>() : reportingStatusEntries)
                .build();
    }

	private AviationAccountReportingStatusHistory createReportingStatusHistory(Long id,
			AviationAccountReportingStatus status, String reason) {
		return AviationAccountReportingStatusHistory.builder().id(id).status(status).reason(reason)
				.submitterId("submitter Id").submitterName("submitter name").submissionDate(LocalDateTime.now())
				.build();
	}

}
