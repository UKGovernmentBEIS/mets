package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BDRSubmitToRegulatorVerificationPerformedValidationServiceTest {

    @InjectMocks
    private BDRSubmitToRegulatorVerificationPerformedValidationService service;

      @Test
    void getErrorMessage() {
        assertThat(service.getErrorMessage()).isEqualTo(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).isEqualTo(Set.of(RequestTaskActionType.BDR_SUBMIT_TO_REGULATOR ));
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(Set.of(), service.getConflictingRequestTaskTypes());
    }

    @Test
    void validate_isApplicationForFreeAllocation_false_valid_result() {
        final RequestTask requestTask = RequestTask.builder()
            .payload(BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(BDR.builder().isApplicationForFreeAllocation(false).build())
                .verificationPerformed(false)
                .build())
            .build();

        assertEquals(RequestTaskActionValidationResult.validResult(), service.validate(requestTask));
    }

    @Test
    void validate_isApplicationForFreeAllocation_true_and_verification_not_performed_invalid_result() {
        final RequestTask requestTask = RequestTask.builder()
            .payload(BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .verificationPerformed(false)
                .build())
            .build();

        assertEquals(RequestTaskActionValidationResult.invalidResult(RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED),
            service.validate(requestTask));
    }

    @Test
    void validate_isApplicationForFreeAllocation_false_and_verification_performed_valid_result() {
        final RequestTask requestTask = RequestTask.builder()
            .payload(BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(BDR.builder().isApplicationForFreeAllocation(false).build())
                .verificationPerformed(false)
                .build())
            .build();

        assertEquals(RequestTaskActionValidationResult.validResult(), service.validate(requestTask));
    }


}
