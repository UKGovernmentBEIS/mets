package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitToRegulatorVerificationPerformedValidationServiceTest {

    @InjectMocks
    private ALRSubmitToRegulatorVerificationPerformedValidationService service;

    @Test
    void getErrorMessage() {
        assertThat(service.getErrorMessage()).isEqualTo(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).isEqualTo(Set.of(RequestTaskActionType.ALR_SUBMIT_TO_REGULATOR));
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(Set.of(), service.getConflictingRequestTaskTypes());
    }

    @Test
    void validate_hasVerificationReport_VerificationPerformedTrue_valid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(ALRApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                        .verificationPerformed(true)
                        .build())
                .request(Request.builder()
                        .type(RequestType.ALR)
                        .payload(ALRRequestPayload
                                .builder()
                                .verificationReport(ALRVerificationReport.builder().build())
                                .build()
                        ).build())
                .build();

        assertEquals(RequestTaskActionValidationResult.validResult(), service.validate(requestTask));
    }

    @Test
    void validate_doesNotHaveVerificationReport_VerificationPerformedTrue_invalid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(ALRApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                        .verificationPerformed(true)
                        .build())
                .request(Request.builder()
                        .type(RequestType.ALR)
                        .payload(ALRRequestPayload
                                .builder()
                                .verificationReport(null)
                                .build()
                        ).build())
                .build();

        assertEquals(RequestTaskActionValidationResult.invalidResult((RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED)), service.validate(requestTask));
    }

    @Test
    void validate_hasVerificationReport_VerificationPerformedFalse_invalid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(ALRApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                        .verificationPerformed(false)
                        .build())
                .request(Request.builder()
                        .type(RequestType.ALR)
                        .payload(ALRRequestPayload
                                .builder()
                                .verificationReport(ALRVerificationReport.builder().build())
                                .build()
                        ).build())
                .build();

        assertEquals(RequestTaskActionValidationResult.invalidResult((RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED)), service.validate(requestTask));
    }
}
