package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class AerReviewValidatorService {

    public void validateCompleted(Aer aer,
                                  @NotEmpty Map<AerReviewGroup, @Valid AerReviewDecision> aerReviewGroupAerReviewDecisionMap,
                                  boolean isVerificationPerformed, AerVerificationReport aerVerificationReport) {
        boolean allAccepted = aerReviewGroupAerReviewDecisionMap.values().stream()
            .filter(aerReviewDecision -> aerReviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
            .map(AerDataReviewDecision.class::cast)
            .allMatch(aerDataReviewDecision -> aerDataReviewDecision.getType().equals(AerDataReviewDecisionType.ACCEPTED));

        if (!decisionExistsForAllReviewGroups(aer, aerVerificationReport, aerReviewGroupAerReviewDecisionMap,
            isVerificationPerformed) || !allAccepted) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private boolean decisionExistsForAllReviewGroups(Aer aer,
                                                     AerVerificationReport aerVerificationReport,
                                                     Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap,
                                                     boolean isVerificationPerformed) {
        return decisionExistsForAllAerReviewGroups(aer, aerVerificationReport, aerReviewGroupAerReviewDecisionMap)
            && decisionExistsForAllVerificationReviewGroups(aerReviewGroupAerReviewDecisionMap,
            aerVerificationReport,
            isVerificationPerformed);
    }

    private boolean decisionExistsForAllAerReviewGroups(Aer aer,
                                                        AerVerificationReport aerVerificationReport,
                                                        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap) {
        final Set<AerReviewGroup> verificationDataReviewGroups =
            AerReviewGroup.getVerificationDataReviewGroups(aerVerificationReport);
        final Set<AerReviewGroup> aerDataReviewGroups = aerReviewGroupAerReviewDecisionMap.keySet().stream()
            .filter(aerReviewGroup -> !verificationDataReviewGroups.contains(aerReviewGroup))
            .collect(Collectors.toSet());

        return CollectionUtils.isEqualCollection(aerDataReviewGroups, AerReviewGroup.getAerDataReviewGroups(aer));
    }

    private boolean decisionExistsForAllVerificationReviewGroups(Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap,
                                                                 AerVerificationReport aerVerificationReport,
                                                                 boolean isVerificationPerformed) {
        final Set<AerReviewGroup> verificationDataReviewGroups =
            AerReviewGroup.getVerificationDataReviewGroups(aerVerificationReport);
        return isVerificationPerformed
            ? aerReviewGroupAerReviewDecisionMap.keySet().containsAll(verificationDataReviewGroups)
            : aerReviewGroupAerReviewDecisionMap.keySet().stream().noneMatch(verificationDataReviewGroups::contains);
    }
}
