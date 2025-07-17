package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerificationPerformedRequestTaskActionValidatorTest {

    private final AviationAerCorsiaVerificationPerformedRequestTaskActionValidator validator =
            new AviationAerCorsiaVerificationPerformedRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertEquals(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED, validator.getErrorMessage());
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).containsExactlyInAnyOrder(
                RequestTaskActionType.AVIATION_AER_CORSIA_SUBMIT_APPLICATION,
                RequestTaskActionType.AVIATION_AER_CORSIA_SUBMIT_APPLICATION_AMEND
        );
    }

    @Test
    void validate_valid() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .reportingRequired(true)
                        .aer(aer)
                        .verificationPerformed(true)
                        .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_valid_when_no_reporting_required() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .reportingRequired(false)
                        .aer(aer)
                        .verificationPerformed(false)
                        .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_invalid() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .reportingRequired(true)
                        .aer(aer)
                        .verificationPerformed(false)
                        .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertFalse(validationResult.isValid());
    }

}
