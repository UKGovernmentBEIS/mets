package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper.PermitVariationRegulatorLedMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewRequestPeerReviewRegulatorLedValidator {

	private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final PermitGrantedValidatorService permitGrantedValidatorService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    
    private static final PermitVariationRegulatorLedMapper REVIEW_MAPPER = Mappers.getMapper(PermitVariationRegulatorLedMapper.class);

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final AppUser appUser) {
        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW,
            payload.getPeerReviewer(), 
            appUser
        );

        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        
        final PermitVariationRegulatorLedGrantDetermination determination = taskPayload.getDetermination();
        
        permitReviewDeterminationValidatorService.validateDeterminationObject(determination);

        final PermitContainer permitContainer = REVIEW_MAPPER.toPermitContainer(taskPayload);
        permitGrantedValidatorService.validatePermit(permitContainer);
    }

}
