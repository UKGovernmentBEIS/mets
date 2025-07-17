package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.AuthorityConstants.EMITTER_CONTACT;
import static uk.gov.netz.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class OperatorRoleCodeAcceptInvitationServiceDelegatorTest {

    private OperatorRoleCodeAcceptInvitationServiceDelegator serviceDelegator;
    private EmitterContactAcceptInvitationService emitterContactAcceptInvitationService;
    private OperatorRoleCodeAcceptInvitationServiceDefaultImpl operatorRoleCodeAcceptInvitationServiceDefaultImpl;

    @BeforeAll
    void setUp() {
        emitterContactAcceptInvitationService = mock(EmitterContactAcceptInvitationService.class);
        operatorRoleCodeAcceptInvitationServiceDefaultImpl = mock(OperatorRoleCodeAcceptInvitationServiceDefaultImpl.class);

        serviceDelegator = new OperatorRoleCodeAcceptInvitationServiceDelegator(
            List.of(emitterContactAcceptInvitationService, operatorRoleCodeAcceptInvitationServiceDefaultImpl));

        when(emitterContactAcceptInvitationService.getRoleCodes()).thenReturn(Set.of(EMITTER_CONTACT));
        when(operatorRoleCodeAcceptInvitationServiceDefaultImpl.getRoleCodes())
            .thenReturn(Set.of(OPERATOR_ADMIN_ROLE_CODE));

    }

    @Test
    void acceptInvitation_when_not_emitter_contact() {
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder().build();

        when(operatorRoleCodeAcceptInvitationServiceDefaultImpl.acceptInvitation(operatorUserAcceptInvitation))
            .thenReturn(UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM);

        serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, OPERATOR_ADMIN_ROLE_CODE);

        verify(operatorRoleCodeAcceptInvitationServiceDefaultImpl, times(1))
            .acceptInvitation(operatorUserAcceptInvitation);
    }

    @Test
    void acceptInvitation_when_emitter_contact() {
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder().build();

        when(emitterContactAcceptInvitationService.acceptInvitation(operatorUserAcceptInvitation))
            .thenReturn(UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD);

        serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, EMITTER_CONTACT);

        verify(emitterContactAcceptInvitationService, times(1))
            .acceptInvitation(operatorUserAcceptInvitation);
    }

    @Test
    void acceptInvitation_when_no_matched_role_code() {
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder().build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () ->serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, VERIFIER_ADMIN_ROLE_CODE));

        assertEquals(ErrorCode.USER_REGISTRATION_FAILED_500, businessException.getErrorCode());
    }
}