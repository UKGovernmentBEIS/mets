package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceRegistryIntegrationRequestActionPayload;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceRegistryIntegrationAddRequestActionServiceTest {

    @Mock private RequestService requestService;

    @Mock private AviationAccountQueryService aviationAccountQueryService;
    @Mock private EmissionsMonitoringPlanQueryService empQueryService;

    @InjectMocks
    private EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;

    @Test
    void addRequestAction_whenAppUserPresent_callsAddActionToRequest() {
        String requestId = "REQ-1";
        Request request = Request.builder().id(requestId).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        EmissionsMonitoringPlanUkEts emp = mock(EmissionsMonitoringPlanUkEts.class, RETURNS_DEEP_STUBS);
        LimitedCompanyOrganisation org = mock(LimitedCompanyOrganisation.class);
        when(org.getLegalStatusType()).thenReturn(OrganisationLegalStatusType.LIMITED_COMPANY);
        when(emp.getOperatorDetails().getOrganisationStructure()).thenReturn(org);

        AppUser appUser = mock(AppUser.class);
        when(appUser.getUserId()).thenReturn("user-123");

        AviationAccountCreatedRegistryEvent aviationAccountCreatedRegistryEvent =
                AviationAccountCreatedRegistryEvent.builder()
                        .requestId(requestId)
                        .accountId(1L)
                        .emissionsMonitoringPlan(emp)
                        .appUser(appUser)
                        .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(AviationAccountDTO.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).commencementDate(LocalDate.of(2025,1,1)).build());

        addRequestActionService.addRequestAction(aviationAccountCreatedRegistryEvent);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(eq(request), any(EmpIssuanceRegistryIntegrationRequestActionPayload.class),
                        eq(RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY), eq("user-123"));
        verify(requestService, never())
                .addSystemActionToRequest(any(), any(), any());
    }

    @Test
    void addRequestAction_whenAppUserAbsent_callsAddSystemActionToRequest() {
        String requestId = "REQ-2";
        Request request = Request.builder().id(requestId).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        EmissionsMonitoringPlanUkEts emp = mock(EmissionsMonitoringPlanUkEts.class, RETURNS_DEEP_STUBS);
        LimitedCompanyOrganisation org = mock(LimitedCompanyOrganisation.class);
        when(org.getLegalStatusType()).thenReturn(OrganisationLegalStatusType.LIMITED_COMPANY);
        when(emp.getOperatorDetails().getOrganisationStructure()).thenReturn(org);

        AviationAccountCreatedRegistryEvent aviationAccountCreatedRegistryEvent =
                AviationAccountCreatedRegistryEvent.builder()
                        .requestId(requestId)
                        .accountId(1L)
                        .emissionsMonitoringPlan(emp)
                        .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(AviationAccountDTO.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).commencementDate(LocalDate.of(2025,1,1)).build());


        addRequestActionService.addRequestAction(aviationAccountCreatedRegistryEvent);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addSystemActionToRequest(eq(request), any(EmpIssuanceRegistryIntegrationRequestActionPayload.class),
                        eq(RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY));
        verify(requestService, never())
                .addActionToRequest(any(), any(), any(), anyString());
    }
}