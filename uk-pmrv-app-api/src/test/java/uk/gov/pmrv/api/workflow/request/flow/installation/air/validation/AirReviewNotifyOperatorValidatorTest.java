package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirReviewNotifyOperatorValidatorTest {

    @InjectMocks
    private AirReviewNotifyOperatorValidator validator;

    @Mock
    private AirReviewValidator reviewValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validate() {
        
        final Map<Integer, RegulatorAirImprovementResponse> regulatorAirImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
        );
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
                1, OperatorAirImprovementYesResponse.builder().build()
        );
        final AirApplicationReviewRequestTaskPayload taskPayload = AirApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
            .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses).build())
            .operatorImprovementResponses(operatorImprovementResponses)
            .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        final PmrvUser user = PmrvUser.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("signatoryUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(true);

        // Invoke
        validator.validate(requestTask, actionPayload, user);

        // Verify
        verify(reviewValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);
    }

    @Test
    void validate_not_valid() {
        
        final Map<Integer, RegulatorAirImprovementResponse> regulatorAirImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
        );
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            1, OperatorAirImprovementYesResponse.builder().build()
        );
        final AirApplicationReviewRequestTaskPayload taskPayload = AirApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
            .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses).build())
            .operatorImprovementResponses(operatorImprovementResponses)
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        final PmrvUser user = PmrvUser.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUser"))
            .signatory("signatoryUser")
            .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(false);

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> validator.validate(requestTask, actionPayload, user));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
        verify(reviewValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);
    }
}
