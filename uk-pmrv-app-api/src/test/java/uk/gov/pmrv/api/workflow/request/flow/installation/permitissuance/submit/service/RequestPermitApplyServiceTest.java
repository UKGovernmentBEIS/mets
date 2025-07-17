package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestPermitApplyServiceTest {

    @InjectMocks
    private RequestPermitApplyService service;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitValidatorService permitValidatorService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void applySaveAction() {
        //prepare
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload = PermitIssuanceApplicationSubmitRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
            .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
            .permitSectionsCompleted(new HashMap<>())
            .build();

        RequestTask requestTask = RequestTask.builder().payload(permitIssuanceApplicationSubmitRequestTaskPayload).build();

        PermitIssuanceSaveApplicationRequestTaskActionPayload permitIssuanceSaveApplicationRequestTaskActionPayload =
            PermitIssuanceSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD)
                .permitType(PermitType.GHGE)
                .permit(Permit.builder()
                    .installationDescription(InstallationDescription.builder().mainActivitiesDesc("mainAct")
                            .siteDescription("siteDescription").build())
                    .build())
                .permitSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), List.of(true)))
                .build();

        //invoke
        service.applySaveAction(permitIssuanceSaveApplicationRequestTaskActionPayload, requestTask);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationSubmitRequestTaskPayload.class);
        PermitIssuanceApplicationSubmitRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertEquals(PermitType.GHGE, payloadSaved.getPermitType());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getMainActivitiesDesc()).isEqualTo("mainAct");
        assertThat(payloadSaved.getPermit().getInstallationDescription().getSiteDescription()).isEqualTo("siteDescription");
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName()).isEqualTo("instName");
        assertThat(payloadSaved.getPermitSectionsCompleted()).containsExactly(Map.entry(InstallationOperatorDetails.class.getName(), List.of(true)));
    }

    @Test
    void applySubmitAction() {
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("instName").build();
        Long accountId = 1L;

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload = PermitIssuanceApplicationSubmitRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
            .installationOperatorDetails(installationOperatorDetails)
            .permitSectionsCompleted(new HashMap<>())
            .build();
        PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .build();

        Request request = Request.builder().accountId(accountId).type(RequestType.PERMIT_ISSUANCE).payload(permitIssuanceRequestPayload).build();
        RequestTask requestTask = RequestTask.builder().request(request).payload(
            permitIssuanceApplicationSubmitRequestTaskPayload).build();

        AppUser authUser = AppUser.builder().userId("user").build();

        PermitContainer permitContainer = PermitContainer.builder()
            .installationOperatorDetails(permitIssuanceApplicationSubmitRequestTaskPayload.getInstallationOperatorDetails())
            .build();

        PermitIssuanceApplicationSubmittedRequestActionPayload permitApplySubmittedPayload = PermitIssuanceApplicationSubmittedRequestActionPayload
            .builder()
            .installationOperatorDetails(permitIssuanceApplicationSubmitRequestTaskPayload.getInstallationOperatorDetails())
            .payloadType(RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD)
            .build();

        //invoke
        service.applySubmitAction(requestTask, authUser);

        //verify
        verify(permitValidatorService, times(1)).validatePermit(permitContainer);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(requestService, times(1)).addActionToRequest(request,
            permitApplySubmittedPayload, RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED, authUser.getUserId());

    }
}
