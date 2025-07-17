package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.utils.PermitReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewMapper;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationReviewRequestTaskInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final PermitVariationReviewMapper PERMIT_VARIATION_REVIEW_MAPPER = Mappers.getMapper(PermitVariationReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PERMIT_VARIATION_REVIEW_MAPPER.toPermitVariationApplicationReviewRequestTaskPayload(
            requestPayload, RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD);

        requestTaskPayload.setInstallationOperatorDetails(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()));

        /*
        The reason we are checking if the review group decisions are not yet set is because the first time the review
        group decisions are not set so we set them to ACCEPTED, whereas when an application is being resubmitted
        for amends or for peer review, we need to not overwrite the decision groups.
         */
        if (reviewGroupDecisionsNotYetSet(requestPayload)) {
            requestTaskPayload.setReviewGroupDecisions(PermitReviewUtils.getPermitReviewGroups(requestPayload.getPermit())
                .stream()
                .collect(Collectors.toMap(Function.identity(), reviewGroup -> PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).details(
                    PermitAcceptedVariationDecisionDetails.builder().build()).build())));
            
            requestTaskPayload.setPermitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED)
            		.details(PermitAcceptedVariationDecisionDetails.builder().build()).build());

            requestTaskPayload.setDetermination(PermitVariationGrantDetermination.builder()
                            .type(DeterminationType.GRANTED)
                    .annualEmissionsTargets(requestPayload.getOriginalPermitContainer().getAnnualEmissionsTargets()).build());
        }

        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
    }

    private static boolean reviewGroupDecisionsNotYetSet(PermitVariationRequestPayload requestPayload) {
        return requestPayload.getReviewGroupDecisions() == null || 
        		requestPayload.getReviewGroupDecisions().isEmpty() || 
        		requestPayload.getPermitVariationDetailsReviewDecision() == null
        		;
    }
}
