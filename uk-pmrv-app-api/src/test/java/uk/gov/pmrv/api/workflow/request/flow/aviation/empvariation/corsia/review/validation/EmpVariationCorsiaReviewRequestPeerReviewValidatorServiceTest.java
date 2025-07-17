package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewRequestPeerReviewValidatorServiceTest {

	@InjectMocks
    private EmpVariationCorsiaReviewRequestPeerReviewValidatorService requestPeerReviewValidatorService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private EmpVariationCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;

    @Test
    void validate_is_valid() {
        String selectedPeerReviewer = "peerReviewer";
        AppUser appUser = AppUser.builder().userId("userId").build();
        EmpVariationDeterminationType determinationType = EmpVariationDeterminationType.APPROVED;
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(determinationType).build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .build();

        when(reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determinationType)).thenReturn(true);


        requestPeerReviewValidatorService.validate(requestTask, selectedPeerReviewer, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(reviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(reviewRequestTaskPayload, determinationType);
        verify(empCorsiaValidatorService, times(1)).validateEmissionsMonitoringPlan(
        		isA(EmissionsMonitoringPlanCorsiaContainer.class));
    }

    @Test
    void validate_throws_exception_when_invalid() {
        String selectedPeerReviewer = "peerReviewer";
        AppUser appUser = AppUser.builder().userId("userId").build();
        EmpVariationDeterminationType determinationType = EmpVariationDeterminationType.APPROVED;
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(determinationType).build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .build();

        when(reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determinationType)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> requestPeerReviewValidatorService.validate(requestTask, selectedPeerReviewer, appUser));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(reviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(reviewRequestTaskPayload, determinationType);
        verifyNoInteractions(empCorsiaValidatorService);
    }
}
