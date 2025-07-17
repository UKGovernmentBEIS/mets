package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Validated
public class RequestAviationAerCorsiaReviewValidatorService {

    public void validateAllReviewGroupsExistAndAccepted(
        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload,
        boolean isVerificationPerformed) {
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions =
            reviewRequestTaskPayload.getReviewGroupDecisions();
        Boolean reportingRequired = reviewRequestTaskPayload.getReportingRequired();

        //validate all groups have decisions which are accepted
        //(no need to check accepted decision for verification report review groups accepted is the only  option for these)
        if (!existDecisionForAllReviewGroups(reviewGroupDecisions, reportingRequired, reviewRequestTaskPayload.getAer(), isVerificationPerformed) ||
            !areAllAerReviewGroupsAccepted(reviewGroupDecisions)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private boolean existDecisionForAllReviewGroups(
        @NotEmpty Map<AviationAerCorsiaReviewGroup,
            @Valid AerReviewDecision> reviewGroupDecisions,
        boolean isReportingRequired,
        AviationAerCorsia aer,
        boolean isVerificationPerformed) {
        Set<AviationAerCorsiaReviewGroup> aerReviewGroups =
            new HashSet<>(AviationAerCorsiaReviewGroup.getAerDataReviewGroups(isReportingRequired));

        if (isVerificationPerformed) {
            aerReviewGroups.addAll(AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(aer));
        }

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), aerReviewGroups);
    }

    private boolean areAllAerReviewGroupsAccepted(@NotEmpty Map<AviationAerCorsiaReviewGroup, @Valid AerReviewDecision> reviewGroupDecisions) {
        return reviewGroupDecisions.values().stream()
            .filter(aerReviewDecision -> aerReviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
            .map(AerDataReviewDecision.class::cast)
            .allMatch(aerDataReviewDecision -> aerDataReviewDecision.getType().equals(AerDataReviewDecisionType.ACCEPTED));
    }

    public void validateAtLeastOneReviewGroupAmendsNeeded(AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        boolean amendExists = reviewRequestTaskPayload.getReviewGroupDecisions().values().stream()
                .filter(reviewDecision -> reviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
                .map(AerDataReviewDecision.class::cast)
                .anyMatch(reviewDecision -> reviewDecision.getType().equals(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED));

        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_AVIATION_AER_REVIEW);
        }
    }
}
