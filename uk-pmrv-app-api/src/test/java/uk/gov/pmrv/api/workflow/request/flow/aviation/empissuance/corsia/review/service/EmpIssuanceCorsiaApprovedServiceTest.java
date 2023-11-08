package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaApprovedServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaApprovedService empIssuanceCorsiaApprovedService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanService emissionsMonitoringPlanService;

    @Mock
    private AviationAccountUpdateService aviationAccountUpdateService;

    @Test
    void approveEmp() {
        Long accountId = 1L;
        String requestId = "REQ3";
        String operatorName = "name";
        LocationOnShoreStateDTO organisationLocationDTO = LocationOnShoreStateDTO.builder()
            .type(LocationType.ONSHORE_STATE)
            .line1("line1")
            .city("city")
            .state("state")
            .country("UK")
            .build();
        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
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
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        empIssuanceCorsiaApprovedService.approveEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .submitEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanCorsiaContainer.class));
        verify(aviationAccountUpdateService, times(1))
            .updateAccountUponEmpApproved(accountId, operatorName, organisationLocationDTO);
    }

    @Test
    void approveEmp_when_org_structure_is_limited_company_and_different_location_Exists() {
        Long accountId = 1L;
        String requestId = "REQ3";
        String operatorName = "name";
        LocationOnShoreStateDTO organisationLocationDTO = LocationOnShoreStateDTO.builder()
            .type(LocationType.ONSHORE_STATE)
            .line1("line1")
            .city("city")
            .state("state")
            .country("UK")
            .build();
        LocationOnShoreStateDTO differentContactLocation = LocationOnShoreStateDTO.builder()
            .type(LocationType.ONSHORE_STATE)
            .line1("line11")
            .city("city")
            .country("EN")
            .build();
        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .operatorName(operatorName)
                    .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .organisationLocation(organisationLocationDTO)
                        .differentContactLocationExist(true)
                        .differentContactLocation(differentContactLocation)
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .accountId(accountId)
            .build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        empIssuanceCorsiaApprovedService.approveEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .submitEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanCorsiaContainer.class));
        verify(aviationAccountUpdateService, times(1))
            .updateAccountUponEmpApproved(accountId, operatorName, differentContactLocation);
    }
}