package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalProceedToAuthorityValidatorTest {

    @InjectMocks
    private DoalProceedToAuthorityValidator validator;

    @Mock
    private DoalSubmitValidator doalSubmitValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validateNotify() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
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
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
                .thenReturn(true);

        // Invoke
        validator.validateNotify(requestTask, decisionNotification, appUser);

        // Verify
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validateNotify_not_valid_determination_type() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .doal(Doal.builder()
                        .determination(DoalClosedDetermination.builder()
                                .type(DoalDeterminationType.CLOSED)
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validateNotify(requestTask, decisionNotification, appUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, never()).areUsersValid(any(), any(), any());
    }

    @Test
    void validateNotify_not_valid_send_notice() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .doal(Doal.builder()
                        .determination(DoalProceedToAuthorityDetermination.builder()
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .needsOfficialNotice(false)
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validateNotify(requestTask, decisionNotification, appUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, never()).areUsersValid(any(), any(), any());
    }

    @Test
    void validateNotify_not_valid_users() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
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
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
                .thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validateNotify(requestTask, decisionNotification, appUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validateComplete() {
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .doal(Doal.builder()
                        .determination(DoalProceedToAuthorityDetermination.builder()
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .needsOfficialNotice(false)
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        // Invoke
        validator.validateComplete(requestTask);

        // Verify
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
    }

    @Test
    void validateComplete_not_valid_send_notice() {
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
                () -> validator.validateComplete(requestTask));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
    }
}
