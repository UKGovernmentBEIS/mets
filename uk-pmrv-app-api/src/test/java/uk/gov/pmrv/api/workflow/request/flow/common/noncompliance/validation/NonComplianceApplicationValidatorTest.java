package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@ExtendWith(MockitoExtension.class)
class NonComplianceApplicationValidatorTest {

    @InjectMocks
    private NonComplianceApplicationValidator validator;
    
    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validateApplication_whenInvalidId_thenThrowException() {

        final NonComplianceApplicationSubmitRequestTaskPayload payload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder()
                .availableRequests(List.of(RequestInfoDTO.builder().id("id_exists").build()))
                .selectedRequests(Set.of("id_exists", "id_not_exists"))
                .build();

        final BusinessException be = assertThrows(BusinessException.class, () -> validator.validateApplication(payload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());
    }

    @Test
    void validateApplication_whenValidId_thenOk() {

        final NonComplianceApplicationSubmitRequestTaskPayload payload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder()
                .availableRequests(List.of(RequestInfoDTO.builder().id("id_exists").build()))
                .selectedRequests(Set.of("id_exists"))
                .build();

        assertDoesNotThrow(() -> validator.validateApplication(payload));
    }
    
    @Test
    void validateUsers_whenValid_thenOk() {
        
        final RequestTask requestTask = RequestTask.builder().build();
        final Set<Long> externalContacts = Set.of(1L, 2L);
        final Set<String> operators = Set.of("op1", "op2");
        final PmrvUser pmrvUser = PmrvUser.builder().build();    
        final DecisionNotification  decisionNotification = DecisionNotification.builder()
            .operators(operators)
            .externalContacts(externalContacts)
            .build();
        
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(true);
        
        validator.validateUsers(requestTask, operators, externalContacts, pmrvUser);
        
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);
    }

    @Test
    void validateUsers_whenInvalidId_thenThrowException() {

        final RequestTask requestTask = RequestTask.builder().build();
        final Set<Long> externalContacts = Set.of(1L, 2L);
        final Set<String> operators = Set.of("op1", "op2");     
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final DecisionNotification  decisionNotification = DecisionNotification.builder()
            .operators(operators)
            .externalContacts(externalContacts)
            .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(false);

        final BusinessException be = assertThrows(BusinessException.class, () -> validator.validateUsers(requestTask, operators, externalContacts, pmrvUser));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);
    }
    
    @Test
    void validateContactAddress_whenValid_thenOk() {
        
    	final Request request = Request.builder().accountId(1L).type(RequestType.NON_COMPLIANCE).build();
        
        when(decisionNotificationUsersValidator.shouldHaveContactAddress(request)).thenReturn(true);
        
        validator.validateContactAddress(request);
        
        verify(decisionNotificationUsersValidator, times(1)).shouldHaveContactAddress(request);
    }

    @Test
    void validateContactAddress_whenInvalid_thenThrowException() {

    	final Request request = Request.builder().accountId(1L).type(RequestType.AVIATION_NON_COMPLIANCE).build();
        
        when(decisionNotificationUsersValidator.shouldHaveContactAddress(request)).thenReturn(false);

        final BusinessException be = assertThrows(BusinessException.class, () -> validator.validateContactAddress(request));

        assertEquals(ErrorCode.AVIATION_ACCOUNT_LOCATION_NOT_EXIST, be.getErrorCode());

        verify(decisionNotificationUsersValidator, times(1)).shouldHaveContactAddress(request);
    }
}