package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2SubmitToRegulatorVerificationPerformedValidationService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BDRS2SubmitToRegulatorVerificationPerformedValidationServiceTest {

    @InjectMocks
    private BDRS2SubmitToRegulatorVerificationPerformedValidationService service;


    @Test
    void getTypes() {
        assertThat(service.getTypes())
                .isEqualTo(Set.of(RequestTaskActionType.BDRS2_SUBMIT_TO_REGULATOR));
    }

    @Test
    void validate_cbamFlag_false_verification_not_performed_valid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(BDRS2ApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_SUBMIT_PAYLOAD)
                        .bdrs2(BDRS2.builder()
                                .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                        .requiresAdditionalSubInstallationSplitsForCbam(false)
                                        .build())
                                .build())
                        .verificationPerformed(false)
                        .build())
                .build();

        assertEquals(RequestTaskActionValidationResult.validResult(), service.validate(requestTask));
    }

    @Test
    void validate_cbamFlag_true_and_verification_not_performed_invalid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(BDRS2ApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_SUBMIT_PAYLOAD)
                        .bdrs2(BDRS2.builder()
                                .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                        .requiresAdditionalSubInstallationSplitsForCbam(true)
                                        .build())
                                .build())
                        .verificationPerformed(false)
                        .build())
                .build();

        assertEquals(
                RequestTaskActionValidationResult.invalidResult(
                        RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED),
                service.validate(requestTask)
        );
    }

    @Test
    void validate_cbamFlag_true_and_verification_performed_valid_result() {
        final RequestTask requestTask = RequestTask.builder()
                .payload(BDRS2ApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_SUBMIT_PAYLOAD)
                        .bdrs2(BDRS2.builder()
                                .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                        .requiresAdditionalSubInstallationSplitsForCbam(true)
                                        .build())
                                .build())
                        .verificationPerformed(true)
                        .build())
                .build();

        assertEquals(RequestTaskActionValidationResult.validResult(), service.validate(requestTask));
    }
}
