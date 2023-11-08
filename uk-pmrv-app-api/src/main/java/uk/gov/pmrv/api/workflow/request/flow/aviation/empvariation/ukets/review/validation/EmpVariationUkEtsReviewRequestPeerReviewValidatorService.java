package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewRequestPeerReviewValidatorService {

	private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final EmpVariationUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
    private static final EmpVariationUkEtsMapper MAPPER = Mappers.getMapper(EmpVariationUkEtsMapper.class);

    public void validate(RequestTask requestTask, String selectedPeerReviewer, PmrvUser pmrvUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW,
            selectedPeerReviewer,
            pmrvUser);

        EmpVariationUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        validateDetermination(reviewRequestTaskPayload);
        validateEmissionsMonitoringPlan(reviewRequestTaskPayload);
    }

    private void validateDetermination(EmpVariationUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpVariationDetermination reviewDetermination = reviewRequestTaskPayload.getDetermination();

        reviewDeterminationValidatorService.validateDeterminationObject(reviewDetermination);

        if (!reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, reviewDetermination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateEmissionsMonitoringPlan(EmpVariationUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer = MAPPER.toEmissionsMonitoringPlanUkEtsContainer(reviewRequestTaskPayload);
        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);
    }
}
