package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DoalCloseValidatorTest {

    @InjectMocks
    private DoalCloseValidator validator;

    @Mock
    private DoalSubmitValidator doalSubmitValidator;

    @Test
    void validate() {
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .doal(Doal.builder()
                        .determination(DoalClosedDetermination.builder()
                                .type(DoalDeterminationType.CLOSED)
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
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
    }

    @Test
    void validate_not_valid_determination() {
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .doal(Doal.builder()
                        .determination(DoalProceedToAuthorityDetermination.builder()
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .needsOfficialNotice(true)
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
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
    }
}
