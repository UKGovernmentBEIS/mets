package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateCommencementDateDTO;
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

    @InjectMocks
    private AviationAccountEmpCommandOrchestrator aviationAccountEmpCommandOrchestrator;

    @Test
    void updateAccountCommencementDate_uk_ets_aviation_with_emp() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AccountUpdateCommencementDateDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        EmissionsMonitoringPlanUkEtsDTO empDTO = buildEmissionsMonitoringPlanUkEtsDTO();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));

        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verify(emissionsMonitoringPlanQueryService).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(publisher).publishEvent(any(AviationAccountUpdatedRegistryEvent.class));

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        AviationAccountUpdatedRegistryEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(accountId, event.getAccountId());
        assertNotNull(event.getEmissionsMonitoringPlan());
    }

    @Test
    void updateAccountCommencementDate_uk_ets_aviation_without_emp() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AccountUpdateCommencementDateDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);
        EmissionsMonitoringPlanUkEts emp = buildEmissionsMonitoringPlanUkEts();
        Request request = buildRequest(accountId);
        RequestTask requestTask = buildRequestTask(emp);
        request.getRequestTasks().add(requestTask);

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.empty());
        when(requestService.findRequestsByAccountIdAndType(accountId, RequestType.EMP_ISSUANCE_UKETS)).thenReturn(List.of(request));

        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verify(emissionsMonitoringPlanQueryService).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(requestService).findRequestsByAccountIdAndType(accountId, RequestType.EMP_ISSUANCE_UKETS);
        verify(publisher).publishEvent(any(AviationAccountUpdatedRegistryEvent.class));

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        AviationAccountUpdatedRegistryEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(accountId, event.getAccountId());
        assertNotNull(event.getEmissionsMonitoringPlan());
    }

    @Test
    void updateAccountCommencementDate_non_uk_ets_aviation() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        AccountUpdateCommencementDateDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.CORSIA);

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO);

        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, commencementDate);
        verifyNoInteractions(emissionsMonitoringPlanQueryService);
        verifyNoInteractions(publisher);
    }

    @Test
    void updateAccountCommencementDate_year_before_2021() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(2020, 1, 1);
        AccountUpdateCommencementDateDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO));

        assertEquals(MetsErrorCode.AVIATION_COMMENCEMENT_DATE_NOT_BEFORE_2021_NOT_AFTER_CURRENT_YEAR, exception.getErrorCode());
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService, never()).updateAccountCommencementDate(any(), any());
        verifyNoInteractions(publisher);
    }

    @Test
    void updateAccountCommencementDate_year_after_current_year() {

        Long accountId = 1L;
        LocalDate commencementDate = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
        AccountUpdateCommencementDateDTO commencementDateDTO = buildCommencementDateDTO(commencementDate);
        AviationAccountDTO aviationAccountDTO = buildAviationAccountDTO(EmissionTradingScheme.UK_ETS_AVIATION);

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(aviationAccountDTO);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO));

        assertEquals(MetsErrorCode.AVIATION_COMMENCEMENT_DATE_NOT_BEFORE_2021_NOT_AFTER_CURRENT_YEAR, exception.getErrorCode());
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(aviationAccountUpdateService, never()).updateAccountCommencementDate(any(), any());
        verifyNoInteractions(publisher);
    }

    @Test
    void updateAccountCommencementDate_moved_backwards_adds_reporting_statuses() {
        Long accountId = 1L;
        LocalDate previousDate = LocalDate.of(2023, 1, 1);
        LocalDate newDate = LocalDate.of(2021, 1, 1);
        AccountUpdateCommencementDateDTO dto = AccountUpdateCommencementDateDTO.builder()
                .commencementDate(newDate)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .commencementDate(previousDate)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, dto);

        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, newDate);

        ArgumentCaptor<List<Integer>> yearsCaptor = ArgumentCaptor.forClass(List.class);
        verify(aviationAccountReportingStatusQueryOrchestrator).addReportingStatusesForYears(yearsCaptor.capture(), eq(accountId));

        List<Integer> capturedYears = yearsCaptor.getValue();
        assertEquals(2, capturedYears.size());
        Assertions.assertTrue(capturedYears.contains(2022));
        Assertions.assertTrue(capturedYears.contains(2021));
    }

    @Test
    void updateAccountCommencementDate_moved_forwards_no_reporting_statuses_added() {
        Long accountId = 1L;
        LocalDate previousDate = LocalDate.of(2021, 1, 1);
        LocalDate newDate = LocalDate.of(2023, 1, 1);
        AccountUpdateCommencementDateDTO dto = AccountUpdateCommencementDateDTO.builder()
                .commencementDate(newDate)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .commencementDate(previousDate)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, dto);

        verify(aviationAccountUpdateService).updateAccountCommencementDate(accountId, newDate);
        verifyNoInteractions(aviationAccountReportingStatusQueryOrchestrator);
    }

    private AccountUpdateCommencementDateDTO buildCommencementDateDTO(LocalDate commencementDate) {
        return AccountUpdateCommencementDateDTO.builder()
                .commencementDate(commencementDate)
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