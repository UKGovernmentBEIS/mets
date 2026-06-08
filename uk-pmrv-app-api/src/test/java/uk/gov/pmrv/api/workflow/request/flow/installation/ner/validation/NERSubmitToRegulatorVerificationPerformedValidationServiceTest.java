package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class NERSubmitToRegulatorVerificationPerformedValidationServiceTest {

    private NERSubmitToRegulatorVerificationPerformedValidationService validator =
            new NERSubmitToRegulatorVerificationPerformedValidationService();

    @Test
    void validate_whenVerificationNotPerformed_shouldReturnInvalid() {
        // given
        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .verificationPerformed(false)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .verificationReport(NERVerificationReport.builder().build()) // even if exists
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        // when
        RequestTaskActionValidationResult result = validator.validate(requestTask);

        // then
        assertFalse(result.isValid());
        assertEquals(
                RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED,
                result.getErrorMessage()
        );
    }

    @Test
    void validate_whenVerificationPerformedButNoReport_shouldReturnInvalid() {
        // given
        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .verificationPerformed(true)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .verificationReport(null)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        // when
        RequestTaskActionValidationResult result = validator.validate(requestTask);

        // then
        assertFalse(result.isValid());
    }

    @Test
    void validate_whenVerificationPerformedAndReportExists_shouldReturnValid() {
        // given
        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .verificationPerformed(true)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .verificationReport(NERVerificationReport.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().payload(requestPayload).build())
                .build();

        // when
        RequestTaskActionValidationResult result = validator.validate(requestTask);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void getTypes_shouldReturnCorrectType() {
        // when
        Set<RequestTaskActionType> result = validator.getTypes();

        // then
        assertEquals(Set.of(RequestTaskActionType.NER_SUBMIT_APPLICATION), result);
    }
}
