package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaVerificationAlreadyPerformedRequestTaskActionValidatorTest {

    private final AviationAerCorsiaVerificationAlreadyPerformedRequestTaskActionValidator validator =
        new AviationAerCorsiaVerificationAlreadyPerformedRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertEquals(RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND, validator.getErrorMessage());
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
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .aer(aer)
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_invalid() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .verificationPerformed(true)
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertFalse(validationResult.isValid());
    }

}