package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewRequestPeerReviewValidatorService {

	private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final EmpVariationCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
    private static final EmpVariationCorsiaMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaMapper.class);

    public void validate(RequestTask requestTask, String selectedPeerReviewer, AppUser appUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW,
            selectedPeerReviewer,
            appUser);

        EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        validateDetermination(reviewRequestTaskPayload);
        validateEmissionsMonitoringPlan(reviewRequestTaskPayload);
    }

    private void validateDetermination(EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpVariationDetermination reviewDetermination = reviewRequestTaskPayload.getDetermination();

        reviewDeterminationValidatorService.validateDeterminationObject(reviewDetermination);

        if (!reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, reviewDetermination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateEmissionsMonitoringPlan(EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer = MAPPER.toEmissionsMonitoringPlanCorsiaContainer(reviewRequestTaskPayload);
        empCorsiaValidatorService.validateEmissionsMonitoringPlan(empCorsiaContainer);
    }
}
