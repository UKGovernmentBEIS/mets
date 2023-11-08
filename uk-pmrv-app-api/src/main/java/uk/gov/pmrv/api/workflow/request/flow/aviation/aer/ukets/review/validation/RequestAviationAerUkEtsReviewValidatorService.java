package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Validated
public class RequestAviationAerUkEtsReviewValidatorService {

    public void validateAllReviewGroupsExistAndAccepted(AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload, boolean isVerificationPerformed) {
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = reviewRequestTaskPayload.getReviewGroupDecisions();
        Boolean reportingRequired = reviewRequestTaskPayload.getReportingRequired();
        AviationAerUkEts aer = reviewRequestTaskPayload.getAer();
        AviationAerUkEtsVerificationReport verificationReport = reviewRequestTaskPayload.getVerificationReport();

        //validate all groups have decisions which are accepted
        //(no need to check accepted decision for verification report review groups accepted is the only  option for these)
        if (!existDecisionForAllReviewGroups(reviewGroupDecisions, reportingRequired, aer, isVerificationPerformed, verificationReport) ||
            !areAllAerReviewGroupsAccepted(reviewGroupDecisions)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateAtLeastOneReviewGroupAmendsNeeded(AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        boolean amendExists = reviewRequestTaskPayload.getReviewGroupDecisions().values().stream()
            .filter(reviewDecision -> reviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
            .map(AerDataReviewDecision.class::cast)
            .anyMatch(reviewDecision -> reviewDecision.getType() == AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED);

        if (!amendExists) {
            throw new BusinessException(ErrorCode.INVALID_AVIATION_AER_REVIEW);
        }
    }

    private boolean existDecisionForAllReviewGroups(@NotEmpty Map<AviationAerUkEtsReviewGroup, @Valid AerReviewDecision> reviewGroupDecisions,
                                                    boolean isReportingRequired, AviationAerUkEts aer,
                                                    boolean isVerificationPerformed, AviationAerUkEtsVerificationReport verificationReport) {
        Set<AviationAerUkEtsReviewGroup> aerReviewGroups = new HashSet<>(AviationAerUkEtsReviewGroup.getAerDataReviewGroups(aer, isReportingRequired));

        if(isVerificationPerformed) {
            aerReviewGroups.addAll(AviationAerUkEtsReviewGroup.getVerificationReportDataReviewGroups(verificationReport));
        }

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), aerReviewGroups);
    }

    private boolean areAllAerReviewGroupsAccepted(@NotEmpty Map<AviationAerUkEtsReviewGroup, @Valid AerReviewDecision> reviewGroupDecisions) {
        return reviewGroupDecisions.values().stream()
            .filter(aerReviewDecision -> aerReviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
            .map(AerDataReviewDecision.class::cast)
            .allMatch(aerDataReviewDecision -> aerDataReviewDecision.getType().equals(AerDataReviewDecisionType.ACCEPTED));
    }
}
