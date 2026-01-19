package uk.gov.pmrv.api.account.aviation.service.reportingstatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingRequiredEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusTypeHistoryCreationServiceTest {

    @InjectMocks
    private AviationAccountReportingStatusHistoryCreationService service;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private ApplicationEventPublisher publisher;
    
    @Test
    void submitReportingStatus_to_exempt_current_year() {
        Long accountId = 1L;
		final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO = AviationAccountReportingStatusHistoryCreationDTO
				.builder().status(AviationAccountReportingStatusType.EXEMPT_COMMERCIAL).reason("reason").year(Year.of(2025)).build();
        final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();

        AviationAccount account = createAviationAccount(accountId);

        when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(account));

        service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, appUser);
        
        assertThat(account.getReportingStatusList().getFirst().getStatus()).isEqualTo(AviationAccountReportingStatusType.EXEMPT_COMMERCIAL);
        assertThat(account.getReportingStatusHistoryList()).hasSize(4);
        assertThat(account.getReportingStatusByYear(Year.of(2024)).get().getStatus()).isEqualTo(AviationAccountReportingStatusType.EXEMPT_COMMERCIAL);
        assertThat(account.getReportingStatusHistoryList().getLast().getSubmitterId()).isEqualTo(appUser.getUserId());
        assertThat(account.getReportingStatusHistoryList().getLast().getSubmitterName()).isEqualTo(appUser.getFullName());
        
    }

    @Test
    void submitReportingStatus_to_exempt_previous_year() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO = AviationAccountReportingStatusHistoryCreationDTO
                .builder().status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT).reason("reason").year(Year.of(2024)).build();
        final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();

        AviationAccount account = createAviationAccount(accountId);

        when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(account));

        service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, appUser);

        assertThat(account.getReportingStatusList().getFirst().getStatus()).isEqualTo(AviationAccountReportingStatusType.REQUIRED_TO_REPORT);
        assertThat(account.getReportingStatusHistoryList()).hasSize(4);
        assertThat(account.getReportingStatusByYear(Year.of(2024)).get().getStatus()).isEqualTo(AviationAccountReportingStatusType.REQUIRED_TO_REPORT);
        assertThat(account.getReportingStatusHistoryList().getLast().getSubmitterId()).isEqualTo(appUser.getUserId());
        assertThat(account.getReportingStatusHistoryList().getLast().getSubmitterName()).isEqualTo(appUser.getFullName());

        verify(publisher, times(1)).publishEvent(Mockito.isA(AviationAccountReportingRequiredEvent.class));
    }



    @Test
    void submitReportingStatus_account_report_status_not_found() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT).reason("reason").year(Year.of(2025)).build();
        final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();


        BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, appUser));
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void submitReportingStatus_reporting_status_not_changed() {
        Long accountId = 1L;
        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusHistoryCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT).year(Year.of(2025)).reason("reason").build();
        final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).userId("userId").firstName("first name").lastName("last name").build();

        when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(createAviationAccount(accountId)));


        BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.submitReportingStatus(accountId, reportingStatusHistoryCreationDTO, appUser));

        assertThat(businessException.getErrorCode()).isEqualTo(MetsErrorCode.AVIATION_ACCOUNT_REPORTING_STATUS_NOT_CHANGED);
    }

    private AviationAccount createAviationAccount(Long accountId) {
        return AviationAccount.builder()
                .id(accountId)
                .name("name")
                .crcoCode("crco code")
                .status(AviationAccountStatus.LIVE)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .accountType(AccountType.AVIATION)
                .reportingStatusList(createReportingStatusList())
                .reportingStatusHistoryList(createReportingStatusHistoryList())
                .build();
    }

    private List<AviationAccountReportingStatus> createReportingStatusList() {
        List<AviationAccountReportingStatus> reportingStatusHistoryList = new ArrayList<>();

        reportingStatusHistoryList.add(AviationAccountReportingStatus.builder().status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT)
                .year(Year.of(2025))
                .lastUpdate(LocalDateTime.of(2025,1,1,0,0)).build());
        reportingStatusHistoryList.add(AviationAccountReportingStatus.builder().status(AviationAccountReportingStatusType.EXEMPT_COMMERCIAL)
                .year(Year.of(2024))
                .lastUpdate(LocalDateTime.of(2024,1,1,0,0)).build());
        reportingStatusHistoryList.add(AviationAccountReportingStatus.builder().status(AviationAccountReportingStatusType.EXEMPT_NON_COMMERCIAL)
                .year(Year.of(2023))
                .lastUpdate(LocalDateTime.of(2023,1,1,0,0)).build());

        return reportingStatusHistoryList;

    }


	private List<AviationAccountReportingStatusHistory> createReportingStatusHistoryList() {

        List<AviationAccountReportingStatusHistory> aviationAccountReportingStatusHistoryList = new ArrayList<>();
        aviationAccountReportingStatusHistoryList.add(AviationAccountReportingStatusHistory.builder()
                .status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT).year(Year.of(2025)).build());
        aviationAccountReportingStatusHistoryList.add(AviationAccountReportingStatusHistory.builder()
                .status(AviationAccountReportingStatusType.EXEMPT_COMMERCIAL).year(Year.of(2024)).build());
        aviationAccountReportingStatusHistoryList.add(AviationAccountReportingStatusHistory.builder()
                .status(AviationAccountReportingStatusType.EXEMPT_NON_COMMERCIAL).year(Year.of(2023)).build());
        return aviationAccountReportingStatusHistoryList;

    }

}
