package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Year;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation.AviationVirSubmitValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirApplyServiceTest {

    @InjectMocks
    private AviationVirApplyService requestVirApplyService;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationVirSubmitValidator virSubmitValidator;

    @Test
    void applySaveAction() {
        
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of("A1",
                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
        );
        final AviationVirSaveApplicationRequestTaskActionPayload virApplySavePayload =
            AviationVirSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_VIR_SAVE_APPLICATION_PAYLOAD)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(AviationVirApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD)
                        .operatorImprovementResponses(Map.of("A2",
                                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
                        ))
                        .build())
                .build();

        // Invoke
        requestVirApplyService.applySaveAction(virApplySavePayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(AviationVirApplicationSubmitRequestTaskPayload.class);

        AviationVirApplicationSubmitRequestTaskPayload payloadSaved = (AviationVirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
    }

    @Test
    void submitVir() {
        
        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        final  Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1", OperatorImprovementResponse.builder().build());
        final VirVerificationData verificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(Map.of("A1", UncorrectedItem.builder().build()))
                .build();
        final Map<String, Boolean> virSectionsCompleted = Map.of("A1", true);
        final Year reportingYear = Year.now();

        final Request request = Request.builder()
                .metadata(AviationVirRequestMetadata.builder()
                        .year(reportingYear)
                        .build())
                .payload(AviationVirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                        .build())
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(AviationVirApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD)
                        .verificationData(verificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .virSectionsCompleted(virSectionsCompleted)
                        .build())
                .request(request)
                .build();

        final AviationVirApplicationSubmittedRequestActionPayload actionPayload = AviationVirApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD)
                .reportingYear(reportingYear)
                .verificationData(verificationData)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build();

        // Invoke
        requestVirApplyService.applySubmitAction(requestTask, pmrvUser);

        // Verify
        verify(virSubmitValidator, times(1)).validate(operatorImprovementResponses, verificationData);

        assertThat(request.getPayload()).isInstanceOf(AviationVirRequestPayload.class);

        final AviationVirRequestPayload payloadSaved = (AviationVirRequestPayload) request.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
        assertThat(payloadSaved.getVirSectionsCompleted()).isEqualTo(virSectionsCompleted);

        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.AVIATION_VIR_APPLICATION_SUBMITTED, userId);
    }
}
