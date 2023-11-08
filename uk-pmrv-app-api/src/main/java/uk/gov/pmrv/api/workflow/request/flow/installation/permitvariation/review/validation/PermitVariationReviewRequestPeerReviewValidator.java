package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewRequestPeerReviewValidator {

    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final PermitGrantedValidatorService permitGrantedValidatorService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    
    private static final PermitVariationReviewMapper REVIEW_MAPPER = Mappers.getMapper(PermitVariationReviewMapper.class);

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final PmrvUser pmrvUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW,
            payload.getPeerReviewer(), 
            pmrvUser
        );

        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        this.validateDeterminationAndDecisions(taskPayload, requestTask.getRequest().getType());
        this.validatePermit(taskPayload);
    }

    private void validateDeterminationAndDecisions(final PermitVariationApplicationReviewRequestTaskPayload taskPayload,
                                                   final RequestType requestType) {

        final PermitVariationDeterminateable reviewDetermination = taskPayload.getDetermination();
        permitReviewDeterminationValidatorService.validateDeterminationObject(reviewDetermination);
        if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(reviewDetermination, taskPayload, requestType)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validatePermit(final PermitVariationApplicationReviewRequestTaskPayload taskPayload) {

        final DeterminationType reviewDeterminationType = taskPayload.getDetermination().getType();

        if (DeterminationType.GRANTED.equals(reviewDeterminationType)) {
            final PermitContainer permitContainer = REVIEW_MAPPER.toPermitContainer(taskPayload);
            permitGrantedValidatorService.validatePermit(permitContainer);
        }
    }
}
