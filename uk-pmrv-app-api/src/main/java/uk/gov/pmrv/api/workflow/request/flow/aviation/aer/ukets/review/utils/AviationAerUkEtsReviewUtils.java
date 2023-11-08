package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class AviationAerUkEtsReviewUtils {

    public Set<AviationAerUkEtsReviewGroup> getDeprecatedAerDataReviewGroups(AviationAerUkEtsRequestPayload requestPayload,
                                                                             AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {

        Set<AviationAerUkEtsReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerUkEtsReviewGroup.getAerDataReviewGroups(requestPayload.getAer(), requestPayload.getReportingRequired()));

        Set<AviationAerUkEtsReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerUkEtsReviewGroup.getAerDataReviewGroups(amendsSubmitRequestTaskPayload.getAer(), amendsSubmitRequestTaskPayload.getReportingRequired()));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }

    public Set<AviationAerUkEtsReviewGroup> getDeprecatedVerificationDataReviewGroups(AviationAerUkEtsRequestPayload requestPayload,
                                                                                      AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload) {

        Set<AviationAerUkEtsReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerUkEtsReviewGroup.getVerificationReportDataReviewGroups(requestPayload.getVerificationReport()));

        Set<AviationAerUkEtsReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerUkEtsReviewGroup.getVerificationReportDataReviewGroups(verificationSubmitRequestTaskPayload.getVerificationReport()));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }
}
