package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

@ExtendWith(MockitoExtension.class)
class PermitReviewNotifyOperatorValidatorTest {

    @InjectMocks
    private PermitReviewNotifyOperatorValidator notifyOperatorValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    

    @Test
    void validate_whenDeterminationNotValid_thenThrowException() {

        final Permit permit = Permit.builder().monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder()
                    .build()))
                .build())
            .build();

        final PermitIssuanceRejectDetermination determination = PermitIssuanceRejectDetermination.builder()
            .type(DeterminationType.REJECTED)
            .reason("reason")
            .officialNotice("official notice")
            .build();

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(permit)
                .determination(determination)
                .build();

        final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                    .operators(Set.of("operator1"))
                    .externalContacts(Set.of(10L))
                    .signatory("signatory")
                    .build())
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).type(RequestType.PERMIT_ISSUANCE).build())
            .payload(taskPayload)
            .build();

        final AppUser appUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .build();

        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE)).thenReturn(false);

        final BusinessException businessException = assertThrows(BusinessException.class,
            () -> notifyOperatorValidator.validate(requestTask, actionPayload, appUser));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        
        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE);
        verifyNoInteractions(decisionNotificationUsersValidator);
    }

    @Test
    void validate_whenUsersNotValid_thenThrowException() {
        final Permit permit = Permit.builder().monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder()
                    .build()))
                .build())
            .build();

        final PermitIssuanceRejectDetermination determination = PermitIssuanceRejectDetermination.builder()
            .type(DeterminationType.REJECTED)
            .reason("reason")
            .officialNotice("official notice")
            .build();

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(permit)
                .determination(determination)
                .build();

        final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(DecisionNotification.builder()
                    .operators(Set.of("operator1"))
                    .externalContacts(Set.of(10L))
                    .signatory("signatory")
                    .build())
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).type(RequestType.PERMIT_ISSUANCE).build())
            .payload(taskPayload)
            .build();

        final AppUser appUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .build();

        
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE)).thenReturn(true);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, actionPayload.getDecisionNotification(), appUser)).thenReturn(false);

        final BusinessException businessException = assertThrows(BusinessException.class,
            () -> notifyOperatorValidator.validate(requestTask, actionPayload, appUser));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        
        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, actionPayload.getDecisionNotification(), appUser);
    }
}
