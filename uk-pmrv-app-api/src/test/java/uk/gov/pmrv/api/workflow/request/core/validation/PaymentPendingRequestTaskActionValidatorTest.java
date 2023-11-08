package uk.gov.pmrv.api.workflow.request.core.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PaymentPendingRequestTaskActionValidatorTest {

    @InjectMocks
    private PaymentPendingRequestTaskActionValidator validator;

    @Test
    void validate_invalid() {

        final RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .request(Request.builder().id("1").payload(
                    PermitIssuanceRequestPayload.builder().paymentCompleted(false).build()
                ).build()
            ).build();

        RequestTaskActionValidationResult result = validator.validate(reviewTask);

        assertFalse(result.isValid());
    }

    @Test
    void validate_valid() {

        final RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .request(Request.builder().id("1").payload(
                    PermitIssuanceRequestPayload.builder().paymentCompleted(true).build()
                ).build()
            ).build();

        RequestTaskActionValidationResult result = validator.validate(reviewTask);

        assertTrue(result.isValid());
    }

    @Test
    void getTypes() {
        Set<RequestTaskActionType> requestTaskActionTypes = new HashSet<>();
        requestTaskActionTypes.addAll(RequestTaskActionType.getNotifyOperatorForDecisionTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRequestPeerReviewTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRfiRdeSubmissionTypes());

        assertEquals(requestTaskActionTypes, validator.getTypes());
    }

}