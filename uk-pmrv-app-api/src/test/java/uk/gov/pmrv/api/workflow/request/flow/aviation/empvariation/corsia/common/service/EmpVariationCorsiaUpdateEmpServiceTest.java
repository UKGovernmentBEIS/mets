package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaUpdateEmpServiceTest {

	@InjectMocks
    private EmpVariationCorsiaUpdateEmpService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanService emissionsMonitoringPlanService;
    
    @Mock
    private AviationAccountUpdateService aviationAccountUpdateService;

    @Test
    void updateEmp() {
        Long accountId = 1L;
        String requestId = "requestId";
        String operatorName = "name";
        LocationOnShoreStateDTO organisationLocationDTO = LocationOnShoreStateDTO.builder()
                .type(LocationType.ONSHORE_STATE)
                .line1("line1")
                .city("city")
                .state("state")
                .country("UK")
                .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
            		.operatorDetails(EmpCorsiaOperatorDetails.builder()
                            .operatorName(operatorName)
                            .organisationStructure(LimitedCompanyOrganisation.builder()
                                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                                .organisationLocation(organisationLocationDTO)
                                .differentContactLocationExist(false)
                                .build())
                            .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .accountId(accountId)
            .build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        service.updateEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .updateEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanCorsiaContainer.class));
        verify(aviationAccountUpdateService, times(1))
        	.updateAccountUponEmpVariationApproved(accountId, operatorName, organisationLocationDTO);
    }
}
