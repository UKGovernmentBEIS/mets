package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerificationNotEligibleRequestTaskActionValidatorTest {

    private final AviationAerCorsiaVerificationNotEligibleRequestTaskActionValidator validator =
            new AviationAerCorsiaVerificationNotEligibleRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertEquals(RequestTaskActionValidationResult.ErrorMessage.VERIFICATION_NOT_ELIGIBLE, validator.getErrorMessage());
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).containsExactlyInAnyOrder(
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION
        );
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertThat(validator.getConflictingRequestTaskTypes()).isEmpty();
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder().reportingRequired(true).build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_invalid() {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder().reportingRequired(false).build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertFalse(validationResult.isValid());
    }
}
