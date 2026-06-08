package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingObligationFirstYearDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.service.AccountDetailsHistoryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAccountEmpCommandOrchestratorTest {

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private AviationAccountUpdateService aviationAccountUpdateService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private RequestQueryService requestService;

    @Mock
    private AviationAccountReportingStatusQueryOrchestrator aviationAccountReportingStatusQueryOrchestrator;

    @Mock
    private AccountDetailsHistoryService accountDetailsHistoryService;

    @Mock AviationAerCreationService aviationAerCreationService;

    @InjectMocks
    private AviationAccountEmpCommandOrchestrator aviationAccountEmpCommandOrchestrator;

    @Test
    void updateAccountFirstYearOfReportingObligation_uk_ets_aviation_with_emp() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AviationAccountReportingObligationFirstYearDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        EmissionsMonitoringPlanUkEtsDTO empDTO = buildEmissionsMonitoringPlanUkEtsDTO();
        AppUser appUser = new AppUser();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));

        aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, commencementDateDTO,appUser);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verify(emissionsMonitoringPlanQueryService).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(publisher).publishEvent(any(AviationAccountUpdatedRegistryEvent.class));

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());
        verify(aviationAerCreationService, times(2)).createAerFromFirstYearOfReportingObligation(any(), any(), any());


        AviationAccountUpdatedRegistryEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(accountId, event.getAccountId());
        assertNotNull(event.getEmissionsMonitoringPlan());
    }

    @Test
    void updateAccountFirstYearOfReportingObligation_uk_ets_aviation_without_emp() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AviationAccountReportingObligationFirstYearDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        EmissionsMonitoringPlanUkEts emp = buildEmissionsMonitoringPlanUkEts();
        Request request = buildRequest(accountId);
        RequestTask requestTask = buildRequestTask(emp);
        request.getRequestTasks().add(requestTask);
        AppUser appUser = new AppUser();


        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.empty());
        when(requestService.findRequestsByAccountIdAndType(accountId, RequestType.EMP_ISSUANCE_UKETS)).thenReturn(List.of(request));

        aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, commencementDateDTO,appUser);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verify(emissionsMonitoringPlanQueryService).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(requestService).findRequestsByAccountIdAndType(accountId, RequestType.EMP_ISSUANCE_UKETS);
        verify(publisher).publishEvent(any(AviationAccountUpdatedRegistryEvent.class));

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());
        verify(accountDetailsHistoryService,times(1)).createAccountDetailsHistory(any(), any(), any(), any(), any(), any());
        verify(aviationAerCreationService, times(2)).createAerFromFirstYearOfReportingObligation(any(), any(), any());


        AviationAccountUpdatedRegistryEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(accountId, event.getAccountId());
        assertNotNull(event.getEmissionsMonitoringPlan());
    }

    @Test
    void updateAccountFirstYearOfReportingObligation_non_uk_ets_aviation() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AviationAccountReportingObligationFirstYearDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.CORSIA);
        AppUser appUser = new AppUser();


        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, commencementDateDTO,appUser);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verifyNoInteractions(emissionsMonitoringPlanQueryService);
        verifyNoInteractions(publisher);
        verify(accountDetailsHistoryService,times(1)).createAccountDetailsHistory(any(), any(), any(), any(), any(), any());

    }

    @Test
    void updateAccountFirstYearOfReportingObligation_year_before_2021() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2020, 1, 1);
        AviationAccountReportingObligationFirstYearDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        AppUser appUser = new AppUser();


        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, commencementDateDTO,appUser));

        assertEquals(MetsErrorCode.AVIATION_COMMENCEMENT_DATE_NOT_BEFORE_2021_NOT_AFTER_CURRENT_YEAR, exception.getErrorCode());
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService, never()).updateAccountCommencementDate(any(), any());
        verifyNoInteractions(publisher);
        verifyNoInteractions(accountDetailsHistoryService);

    }

    @Test
    void updateAccountFirstYearOfReportingObligation_year_after_current_year() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
        AviationAccountReportingObligationFirstYearDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        AppUser appUser = new AppUser();


        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, commencementDateDTO,appUser));

        assertEquals(MetsErrorCode.AVIATION_COMMENCEMENT_DATE_NOT_BEFORE_2021_NOT_AFTER_CURRENT_YEAR, exception.getErrorCode());
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService, never()).updateAccountCommencementDate(any(), any());
        verifyNoInteractions(publisher);
        verifyNoInteractions(accountDetailsHistoryService);

    }

    @Test
    void updateAccountFirstYearOfReportingObligation_moved_backwards_adds_reporting_statuses() {
        Long accountId = 1L;
        LocalDate previousDate = LocalDate.of(2023, 1, 1);
        LocalDate newDate = LocalDate.of(2021, 1, 1);
        AviationAccountReportingObligationFirstYearDTO dto = AviationAccountReportingObligationFirstYearDTO.builder()
                .commencementDate(newDate)
                .reason("test")
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .commencementDate(previousDate)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        AppUser appUser = new AppUser();


        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, dto,appUser);

        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, newDate);

        ArgumentCaptor<List<Integer>> yearsCaptor = ArgumentCaptor.forClass(List.class);
        verify(aviationAccountReportingStatusQueryOrchestrator).addReportingStatusesForYears(yearsCaptor.capture(), eq(accountId));
        verify(accountDetailsHistoryService,times(1)).createAccountDetailsHistory(any(), any(), any(), any(), any(), any());


        List<Integer> capturedYears = yearsCaptor.getValue();
        assertEquals(2, capturedYears.size());
        Assertions.assertTrue(capturedYears.contains(2022));
        Assertions.assertTrue(capturedYears.contains(2021));
    }

    @Test
    void updateAccountFirstYearOfReportingObligation_moved_forwards_no_reporting_statuses_added() {
        Long accountId = 1L;
        LocalDate previousDate = LocalDate.of(2021, 1, 1);
        LocalDate newDate = LocalDate.of(2023, 1, 1);
        AviationAccountReportingObligationFirstYearDTO dto = AviationAccountReportingObligationFirstYearDTO.builder()
                .commencementDate(newDate)
                .reason("test")
                .build();

        AppUser appUser = new AppUser();


        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .commencementDate(previousDate)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountFirstYearOfReportingObligation(accountId, dto,appUser);

        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, newDate);
        verify(accountDetailsHistoryService,times(1)).createAccountDetailsHistory(any(), any(), any(), any(), any(), any());

        verifyNoInteractions(aviationAccountReportingStatusQueryOrchestrator);
    }

    private AviationAccountReportingObligationFirstYearDTO buildCommencementDateDTO(LocalDate commencementDate) {
        return AviationAccountReportingObligationFirstYearDTO.builder()
                .commencementDate(commencementDate)
                .reason("test")
                .build();
    }

    private AviationAccountDTO buildAviationAccountDTO(EmissionTradingScheme emissionTradingScheme) {
        return AviationAccountDTO.builder()
                .id(1L)
                .name("Aviation Account")
                .emissionTradingScheme(emissionTradingScheme)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.of(2025, 1, 1))
                .build();
    }

    private EmissionsMonitoringPlanUkEtsDTO buildEmissionsMonitoringPlanUkEtsDTO() {
        EmissionsMonitoringPlanUkEts emp = buildEmissionsMonitoringPlanUkEts();
        EmissionsMonitoringPlanUkEtsContainer container = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(emp)
                .build();
        return EmissionsMonitoringPlanUkEtsDTO.builder()
                .empContainer(container)
                .build();
    }

    private EmissionsMonitoringPlanUkEts buildEmissionsMonitoringPlanUkEts() {
        return EmissionsMonitoringPlanUkEts.builder().build();
    }

    private Request buildRequest(Long accountId) {
        Request request = new Request();
        request.setAccountId(accountId);
        request.setType(RequestType.EMP_ISSUANCE_UKETS);
        return request;
    }

    private RequestTask buildRequestTask(EmissionsMonitoringPlanUkEts emp) {
        RequestTask requestTask = new RequestTask();
        requestTask.setType(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW);
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = new EmpIssuanceUkEtsApplicationReviewRequestTaskPayload();
        payload.setEmissionsMonitoringPlan(emp);
        requestTask.setPayload(payload);
        return requestTask;
    }
}