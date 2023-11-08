package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class AviationAerCorsiaReviewUtils {

    public Set<AviationAerCorsiaReviewGroup> getDeprecatedAerDataReviewGroups(AviationAerCorsiaRequestPayload requestPayload,
                                                                              AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {

        Set<AviationAerCorsiaReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerCorsiaReviewGroup.getAerDataReviewGroups(requestPayload.getReportingRequired()));

        Set<AviationAerCorsiaReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerCorsiaReviewGroup.getAerDataReviewGroups(amendsSubmitRequestTaskPayload.getReportingRequired()));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }

    public Set<AviationAerCorsiaReviewGroup> getDeprecatedVerificationDataReviewGroups(AviationAerCorsiaRequestPayload requestPayload,
                                                                                       AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload) {

        Set<AviationAerCorsiaReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(requestPayload.getAer()));

        Set<AviationAerCorsiaReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(verificationSubmitRequestTaskPayload.getAer()));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }
}