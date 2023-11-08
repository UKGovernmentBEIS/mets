package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApprovedServiceTest {

    @InjectMocks
    private EmpIssuanceUkEtsApprovedService empIssuanceUkEtsApprovedService;

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
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
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

        empIssuanceUkEtsApprovedService.approveEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .submitEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanUkEtsContainer.class));
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
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
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
        String crcoCode = "crcoCode";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .crcoCode(crcoCode)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        empIssuanceUkEtsApprovedService.approveEmp(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(emissionsMonitoringPlanService, times(1))
            .submitEmissionsMonitoringPlan(eq(accountId), any(EmissionsMonitoringPlanUkEtsContainer.class));
        verify(aviationAccountUpdateService, times(1))
            .updateAccountUponEmpApproved(accountId, operatorName, differentContactLocation);
    }
}