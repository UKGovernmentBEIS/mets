package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper.EmpUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewRequestPeerReviewValidatorService {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final EmpIssuanceUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
    private static final EmpUkEtsReviewMapper EMP_UKETS_REVIEW_MAPPER = Mappers.getMapper(EmpUkEtsReviewMapper.class);

    public void validate(RequestTask requestTask, String selectedPeerReviewer, AppUser appUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
            selectedPeerReviewer,
            appUser);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        validateDetermination(reviewRequestTaskPayload);
        validateEmissionsMonitoringPlan(reviewRequestTaskPayload);
    }

    private void validateDetermination(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpIssuanceDetermination reviewDetermination = reviewRequestTaskPayload.getDetermination();

        reviewDeterminationValidatorService.validateDeterminationObject(reviewDetermination);

        if (!reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, reviewDetermination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateEmissionsMonitoringPlan(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer = EMP_UKETS_REVIEW_MAPPER.toEmissionsMonitoringPlanUkEtsContainer(reviewRequestTaskPayload);
        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);
    }
}
