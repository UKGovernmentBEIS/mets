package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation.VirSubmitValidatorService;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestVirApplyServiceTest {

    @InjectMocks
    private RequestVirApplyService requestVirApplyService;

    @Mock
    private RequestService requestService;

    @Mock
    private VirSubmitValidatorService virSubmitValidatorService;

    @Test
    void applySaveAction() {
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of("A1",
                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
        );
        final VirSaveApplicationRequestTaskActionPayload virApplySavePayload =
                VirSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_SUBMIT_PAYLOAD)
                        .operatorImprovementResponses(Map.of("A2",
                                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
                        ))
                        .build())
                .build();

        // Invoke
        requestVirApplyService.applySaveAction(virApplySavePayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(VirApplicationSubmitRequestTaskPayload.class);

        VirApplicationSubmitRequestTaskPayload payloadSaved = (VirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
    }

    @Test
    void submitVir() {
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final  Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1", OperatorImprovementResponse.builder().build());
        final VirVerificationData verificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(Map.of("A1", UncorrectedItem.builder().build()))
                .build();
        final Map<String, Boolean> virSectionsCompleted = Map.of("A1", true);
        final Year reportingYear = Year.now();

        Request request = Request.builder()
                .metadata(VirRequestMetadata.builder()
                        .year(reportingYear)
                        .build())
                .payload(VirRequestPayload.builder()
                        .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_SUBMIT_PAYLOAD)
                        .verificationData(verificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .virSectionsCompleted(virSectionsCompleted)
                        .build())
                .request(request)
                .build();

        final VirApplicationSubmittedRequestActionPayload actionPayload = VirApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.VIR_APPLICATION_SUBMITTED_PAYLOAD)
                .reportingYear(reportingYear)
                .verificationData(verificationData)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build();

        // Invoke
        requestVirApplyService.applySubmitAction(requestTask, appUser);

        // Verify
        verify(virSubmitValidatorService, times(1)).validate(operatorImprovementResponses, verificationData);

        assertThat(request.getPayload()).isInstanceOf(VirRequestPayload.class);

        VirRequestPayload payloadSaved = (VirRequestPayload) request.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
        assertThat(payloadSaved.getVirSectionsCompleted()).isEqualTo(virSectionsCompleted);

        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.VIR_APPLICATION_SUBMITTED, userId);
    }
}
