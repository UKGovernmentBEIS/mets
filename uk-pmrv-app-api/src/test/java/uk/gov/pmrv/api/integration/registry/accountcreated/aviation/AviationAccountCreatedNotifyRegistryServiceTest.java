package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationEmpApprovedSendToRegistryProducer;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceRegistryIntegrationAddRequestActionService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountCreatedNotifyRegistryServiceTest {

    @InjectMocks
    private AviationAccountCreatedNotifyRegistryService aviationAccountCreatedNotifyRegistryService;

    @Mock
    private AviationAccountEmpQueryOrchestrator queryOrchestrator;

    @Mock
    private AviationEmpApprovedSendToRegistryProducer registryProducer;

    @Mock
    private EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;


    @Test
    void notifyRegistry_whenRegistryIdExists_doNothing() {
        Long accountId = 1L;
        String requestId = "1";
        AviationAccountCreatedRegistryEvent event = AviationAccountCreatedRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .build();

        AviationAccountEmpDTO aviationAccount = AviationAccountEmpDTO.builder()
                .aviationAccount(AviationAccountDTO.builder().id(accountId).registryId(123456).build())
                .emp(EmpDetailsDTO.builder().id("1").build())
                .build();

        when(queryOrchestrator.getAviationAccountWithEMP(accountId)).thenReturn(aviationAccount);

        aviationAccountCreatedNotifyRegistryService.notifyRegistry(event);

        verify(queryOrchestrator, times(1)).getAviationAccountWithEMP(accountId);
        verify(registryProducer, never()).produce(any(AccountOpeningEvent.class));
        verify(addRequestActionService, never()).addRequestAction(any(AviationAccountCreatedRegistryEvent.class));
    }

    @Test
    void notifyRegistry_whenRegistryIdMissing_producesAndAddsRequestAction() {

        Long accountId = 1L;
        String requestId = "1";
        EmissionsMonitoringPlanUkEts monitoringPlanUkEts =
        EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder().organisationStructure(LimitedCompanyOrganisation.builder()
                                .organisationLocation(LocationOnShoreStateDTO.builder().country("UK").build())
                        .build()).build()).build();


        AviationAccountCreatedRegistryEvent event = AviationAccountCreatedRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .emissionsMonitoringPlan(monitoringPlanUkEts)
                .build();

        AviationAccountEmpDTO aviationAccount = AviationAccountEmpDTO.builder()
                .aviationAccount(AviationAccountDTO.builder().id(accountId).registryId(null)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND).commencementDate(LocalDate.of(2025,1,1)).build())
                .emp(EmpDetailsDTO.builder().id("1").build())
                .build();

        when(queryOrchestrator.getAviationAccountWithEMP(accountId)).thenReturn(aviationAccount);

        ArgumentCaptor<AccountOpeningEvent> registryDtoCaptor =
                ArgumentCaptor.forClass(AccountOpeningEvent.class);

        aviationAccountCreatedNotifyRegistryService.notifyRegistry(event);

        verify(queryOrchestrator, times(1)).getAviationAccountWithEMP(accountId);
        verify(registryProducer, times(1)).produce(registryDtoCaptor.capture());
        AccountOpeningEvent produced = registryDtoCaptor.getValue();
        assertNotNull(produced);
    }
}