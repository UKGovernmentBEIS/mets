package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

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
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirSubmitValidator;

@ExtendWith(MockitoExtension.class)
class AirApplyServiceTest {

    @InjectMocks
    private AirApplyService applyService;

    @Mock
    private RequestService requestService;

    @Mock
    private AirSubmitValidator airSubmitValidator;

    @Test
    void applySaveAction() {

        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            Map.of(1, OperatorAirImprovementYesResponse.builder().proposal("proposal").build());
        final AirSaveApplicationRequestTaskActionPayload taskActionPayload =
            AirSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AIR_SAVE_APPLICATION_PAYLOAD)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(AirApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AIR_APPLICATION_SUBMIT_PAYLOAD)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build())
            .build();

        // Invoke
        applyService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(AirApplicationSubmitRequestTaskPayload.class);

        AirApplicationSubmitRequestTaskPayload payloadSaved =
            (AirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
    }

    @Test
    void submitAir() {

        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            Map.of(1, OperatorAirImprovementYesResponse.builder().proposal("proposal").build());
        final Map<Integer, AirImprovement> airImprovements = Map.of(1, AirImprovementCalculationCO2.builder().build());
        final Map<Integer, Boolean> airSectionsCompleted = Map.of(1, true);
        final Year reportingYear = Year.now();

        final Request request = Request.builder()
            .metadata(AirRequestMetadata.builder()
                .year(reportingYear)
                .build())
            .payload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .build())
            .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(AirApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AIR_APPLICATION_SUBMIT_PAYLOAD)
                .airImprovements(airImprovements)
                .operatorImprovementResponses(operatorImprovementResponses)
                .airSectionsCompleted(airSectionsCompleted)
                .build())
            .request(request)
            .build();

        final AirApplicationSubmittedRequestActionPayload actionPayload =
            AirApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AIR_APPLICATION_SUBMITTED_PAYLOAD)
                .reportingYear(reportingYear)
                .airImprovements(airImprovements)
                .operatorImprovementResponses(operatorImprovementResponses)
                .airSectionsCompleted(airSectionsCompleted)
                .build();

        // Invoke
        applyService.applySubmitAction(requestTask, pmrvUser);

        // Verify
        verify(airSubmitValidator, times(1)).validate(operatorImprovementResponses, airImprovements);

        assertThat(request.getPayload()).isInstanceOf(AirRequestPayload.class);

        final AirRequestPayload payloadSaved = (AirRequestPayload) request.getPayload();
        assertThat(payloadSaved.getOperatorImprovementResponses()).isEqualTo(operatorImprovementResponses);
        assertThat(payloadSaved.getAirSectionsCompleted()).isEqualTo(airSectionsCompleted);

        verify(requestService, times(1))
            .addActionToRequest(request, actionPayload, RequestActionType.AIR_APPLICATION_SUBMITTED, userId);
    }
}
