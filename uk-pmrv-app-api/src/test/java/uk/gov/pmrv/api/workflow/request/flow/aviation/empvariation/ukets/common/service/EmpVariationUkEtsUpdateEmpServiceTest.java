package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsUpdateEmpServiceTest {

	@InjectMocks
    private EmpVariationUkEtsUpdateEmpService cut;

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
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
            		.operatorDetails(EmpOperatorDetails.builder()
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
        String crcoCode = "crcoCode";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .crcoCode(crcoCode)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        cut.updateEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .updateEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanUkEtsContainer.class));
        verify(aviationAccountUpdateService, times(1))
        	.updateAccountUponEmpVariationApproved(accountId, operatorName, organisationLocationDTO);
    }
}
