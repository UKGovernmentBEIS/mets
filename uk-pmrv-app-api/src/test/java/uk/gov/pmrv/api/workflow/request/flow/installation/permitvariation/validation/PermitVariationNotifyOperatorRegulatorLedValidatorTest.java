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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation.PermitVariationNotifyOperatorRegulatorLedValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationNotifyOperatorRegulatorLedValidatorTest {

	@InjectMocks
    private PermitVariationNotifyOperatorRegulatorLedValidator cut;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    @Test
    void validate() {
		Request request = Request.builder().type(RequestType.PERMIT_VARIATION).build();

		PermitVariationRegulatorLedGrantDetermination determination = PermitVariationRegulatorLedGrantDetermination.builder()
				.reason("reason")
				.build();

		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload
				.builder().determination(determination).build();

		DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator1")).signatory("signatory").build();
		
		PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload actionPayload = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload
				.builder()
				.decisionNotification(decisionNotification)
				.build();

		RequestTask requestTask = RequestTask.builder().request(request).payload(taskPayload).build();

		AppUser appUser = AppUser.builder().userId("user").build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
        	.thenReturn(true);

        cut.validate(requestTask, actionPayload, appUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, appUser);
    }
}
