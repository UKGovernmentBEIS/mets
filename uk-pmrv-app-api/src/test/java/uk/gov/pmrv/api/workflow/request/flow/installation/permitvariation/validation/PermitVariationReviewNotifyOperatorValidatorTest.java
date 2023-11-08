package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation.PermitVariationReviewNotifyOperatorValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewNotifyOperatorValidatorTest {

	@InjectMocks
    private PermitVariationReviewNotifyOperatorValidator cut;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    @Test
    void validate() {
		Request request = Request.builder().type(RequestType.PERMIT_VARIATION).build();

		PermitVariationGrantDetermination determination = PermitVariationGrantDetermination.builder()
				.type(DeterminationType.GRANTED).reason("reason").build();

		PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload
				.builder().determination(determination).build();

		DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator1")).signatory("signatory").build();
		
		PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload actionPayload = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload
				.builder()
				.decisionNotification(decisionNotification)
				.build();

		RequestTask requestTask = RequestTask.builder().request(request).payload(taskPayload).build();

		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_VARIATION))
        	.thenReturn(true);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser))
        	.thenReturn(true);

        cut.validate(requestTask, actionPayload, pmrvUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_VARIATION);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);
    }
}
