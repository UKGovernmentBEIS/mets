package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service.EmpCorsiaCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpUkEtsCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionMonitoringPlanCreationServiceTest {

    @InjectMocks
    private EmissionMonitoringPlanCreationService emissionMonitoringPlanCreationService;

    @Spy
    private ArrayList<EmpCreationRequestParamsBuilderService> empCreationRequestParamsBuilderServices;

    @Mock
    private EmpUkEtsCreationRequestParamsBuilderService empUkEtsCreationRequestParamsBuilderService;

    @Mock
    private EmpCorsiaCreationRequestParamsBuilderService empCorsiaCreationRequestParamsBuilderService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @BeforeEach
    void setUp() {
        empCreationRequestParamsBuilderServices.add(empUkEtsCreationRequestParamsBuilderService);
        empCreationRequestParamsBuilderServices.add(empCorsiaCreationRequestParamsBuilderService);
    }

    @Test
    void createRequestEmissionMonitoringPlan_when_uk_ets() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;

        RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.EMP_ISSUANCE_UKETS)
            .build();

        when(empUkEtsCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);
        when(empUkEtsCreationRequestParamsBuilderService.buildRequestParams(accountId)).thenReturn(requestParams);

        emissionMonitoringPlanCreationService.createRequestEmissionMonitoringPlan(accountId, emissionTradingScheme);

        verify(empUkEtsCreationRequestParamsBuilderService, times(1)).getEmissionTradingScheme();
        verify(empUkEtsCreationRequestParamsBuilderService, times(1))
            .buildRequestParams(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void createRequestEmissionMonitoringPlan_when_corsia() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingSchemeUkEts = EmissionTradingScheme.UK_ETS_AVIATION;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;

        RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.EMP_ISSUANCE_CORSIA)
            .build();

        when(empUkEtsCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingSchemeUkEts);
        when(empCorsiaCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);
        when(empCorsiaCreationRequestParamsBuilderService.buildRequestParams(accountId)).thenReturn(requestParams);

        emissionMonitoringPlanCreationService.createRequestEmissionMonitoringPlan(accountId, emissionTradingScheme);

        verify(empCorsiaCreationRequestParamsBuilderService, times(1)).getEmissionTradingScheme();
        verify(empCorsiaCreationRequestParamsBuilderService, times(1))
            .buildRequestParams(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}