package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRCloseValidatorTest {

    @InjectMocks
    private ALRCloseValidator validator;

    @Mock
    private ALRValidationService alrValidationService;

    @Test
    void validate() {
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().determination(ALRClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED_ALR)
                        .reason("Close reason")
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        // Invoke
        validator.validate(requestTask);

        // Verify
        verify(alrValidationService, times(1)).validateRegulatorSubmitTaskPayload(taskPayload);
    }

    @Test
    void validate_not_valid_determination() {
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().determination(ALRClosedDetermination.builder()
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .reason("Close reason")
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validate(requestTask));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());

        // Verify
        verify(alrValidationService, times(1)).validateRegulatorSubmitTaskPayload(taskPayload);
    }
}
