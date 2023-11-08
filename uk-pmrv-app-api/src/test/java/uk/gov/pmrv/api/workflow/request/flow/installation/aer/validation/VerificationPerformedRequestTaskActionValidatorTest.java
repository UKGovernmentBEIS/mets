package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VerificationPerformedRequestTaskActionValidatorTest {

    private final VerificationPerformedRequestTaskActionValidator validator =
        new VerificationPerformedRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertThat(validator.getErrorMessage()).isEqualTo(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED);
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).isEqualTo(Set.of(RequestTaskActionType.AER_SUBMIT_APPLICATION,
            RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND));
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(Set.of(), validator.getConflictingRequestTaskTypes());
    }

    @Test
    void validate() {
        final RequestTask requestTask = RequestTask.builder()
            .payload(AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .permitType(PermitType.GHGE)
                .verificationPerformed(true)
                .build())
            .build();
        assertEquals(RequestTaskActionValidationResult.validResult(), validator.validate(requestTask));
    }

    @Test
    void validate_no_valid() {
        final RequestTask requestTask = RequestTask.builder()
            .payload(AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .permitType(PermitType.GHGE)
                .verificationPerformed(false)
                .build())
            .build();

        assertEquals(RequestTaskActionValidationResult.invalidResult(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED),
            validator.validate(requestTask));
    }
}
