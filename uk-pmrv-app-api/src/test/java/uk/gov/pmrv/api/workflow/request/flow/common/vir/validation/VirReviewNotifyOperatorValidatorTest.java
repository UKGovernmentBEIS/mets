package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

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
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class VirReviewNotifyOperatorValidatorTest {

    @InjectMocks
    private VirReviewNotifyOperatorValidator validator;

    @Mock
    private VirReviewValidator virReviewValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validate() {
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .regulatorImprovementResponses(Map.of("A1", RegulatorImprovementResponse.builder().build()))
                .reportSummary("Report summary")
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1", OperatorImprovementResponse.builder().build()
        );
        final RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .build())
                .build();

        final PmrvUser user = PmrvUser.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("signatoryUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(true);

        // Invoke
        validator.validate(requestTask, actionPayload, user);

        // Verify
        verify(virReviewValidator, times(1))
                .validate(regulatorReviewResponse, operatorImprovementResponses);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);
    }

    @Test
    void validate_not_valid() {
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .regulatorImprovementResponses(Map.of("A1", RegulatorImprovementResponse.builder().build()))
                .reportSummary("Report summary")
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1", OperatorImprovementResponse.builder().build()
        );
        final RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .build())
                .build();

        final PmrvUser user = PmrvUser.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("signatoryUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(false);

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> validator.validate(requestTask, actionPayload, user));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
        verify(virReviewValidator, times(1))
                .validate(regulatorReviewResponse, operatorImprovementResponses);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);
    }
}
