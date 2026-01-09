package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedRegistryDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationEmpApprovedSendToRegistryProducer;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.EmpIssuanceApprovedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceRegistryIntegrationAddRequestActionService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceApprovedNotifyRegistryServiceTest {

    @InjectMocks
    private EmpIssuanceApprovedNotifyRegistryService empIssuanceApprovedNotifyRegistryService;

    @Mock
    private AviationAccountEmpQueryOrchestrator queryOrchestrator;

    @Mock
    private AviationEmpApprovedSendToRegistryProducer registryProducer;

    @Mock
    private EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;


    @Test
    void notifyRegistry_whenRegistryIdExists_doNothing() {
        Long accountId = 1L;
        String requestId = "1";
        EmpIssuanceApprovedEvent event = EmpIssuanceApprovedEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .build();

        AviationAccountEmpDTO aviationAccount = AviationAccountEmpDTO.builder()
                .aviationAccount(AviationAccountDTO.builder().id(accountId).registryId(123456).build())
                .emp(EmpDetailsDTO.builder().id("1").build())
                .build();

        when(queryOrchestrator.getAviationAccountWithEMP(accountId)).thenReturn(aviationAccount);
        when(empQueryService.getEmpContainerById("1")).thenReturn(EmissionsMonitoringPlanUkEtsContainer.builder().build());


        empIssuanceApprovedNotifyRegistryService.notifyRegistry(event);

        verify(queryOrchestrator, times(1)).getAviationAccountWithEMP(accountId);
        verify(registryProducer, never()).produce(any(AviationAccountCreatedRegistryDTO.class));
        verify(addRequestActionService, never()).addRequestAction(any(), any(AviationAccountCreatedRequestActionDTO.class));
    }

    @Test
    void notifyRegistry_whenRegistryIdMissing_producesAndAddsRequestAction() {

        Long accountId = 1L;
        String requestId = "1";
        EmpIssuanceApprovedEvent event = EmpIssuanceApprovedEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .build();

        AviationAccountEmpDTO aviationAccount = AviationAccountEmpDTO.builder()
                .aviationAccount(AviationAccountDTO.builder().id(accountId).registryId(null)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND).commencementDate(LocalDate.of(2025,1,1)).build())
                .emp(EmpDetailsDTO.builder().id("1").build())
                .build();

        when(queryOrchestrator.getAviationAccountWithEMP(accountId)).thenReturn(aviationAccount);
        when(empQueryService.getEmpContainerById("1")).thenReturn(EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().operatorDetails(EmpOperatorDetails.builder()
                        .organisationStructure(LimitedCompanyOrganisation.builder()
                                .organisationLocation(LocationOnShoreStateDTO.builder().build()).build()).build()).build()).build());


        ArgumentCaptor<AviationAccountCreatedRegistryDTO> registryDtoCaptor =
                ArgumentCaptor.forClass(AviationAccountCreatedRegistryDTO.class);
        ArgumentCaptor<AviationAccountCreatedRequestActionDTO> reqActionDtoCaptor =
                ArgumentCaptor.forClass(AviationAccountCreatedRequestActionDTO.class);

        empIssuanceApprovedNotifyRegistryService.notifyRegistry(event);

        verify(queryOrchestrator, times(1)).getAviationAccountWithEMP(accountId);
        verify(registryProducer, times(1)).produce(registryDtoCaptor.capture());
        AviationAccountCreatedRegistryDTO produced = registryDtoCaptor.getValue();
        assertNotNull(produced);

        verify(addRequestActionService, times(1))
                .addRequestAction(org.mockito.Mockito.eq(requestId), reqActionDtoCaptor.capture());
        AccountCreatedRequestActionDTO addedAction = reqActionDtoCaptor.getValue();
        assertNotNull(addedAction);
    }
}