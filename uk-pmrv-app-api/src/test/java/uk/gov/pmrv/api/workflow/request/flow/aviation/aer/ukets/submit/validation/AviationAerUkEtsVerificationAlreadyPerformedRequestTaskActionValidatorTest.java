package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerSupportFacilityMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationAlreadyPerformedRequestTaskActionValidatorTest {

    private final AviationAerUkEtsVerificationAlreadyPerformedRequestTaskActionValidator validator =
        new AviationAerUkEtsVerificationAlreadyPerformedRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertEquals(RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND, validator.getErrorMessage());
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).containsExactlyInAnyOrder(
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION
        );
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertThat(validator.getConflictingRequestTaskTypes()).isEmpty();
    }

    @Test
    void validate_valid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(false).build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .aer(aer)
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_invalid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(true).build())
            .monitoringApproach(AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .verificationPerformed(true)
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        RequestTaskActionValidationResult validationResult = validator.validate(requestTask);
        assertFalse(validationResult.isValid());
    }
}