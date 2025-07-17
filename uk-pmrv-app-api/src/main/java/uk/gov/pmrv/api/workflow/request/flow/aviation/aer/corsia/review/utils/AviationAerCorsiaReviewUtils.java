package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class AviationAerCorsiaReviewUtils {
	
	public void cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(AviationAerCorsiaRequestPayload existingRequestPayload,
            boolean newIsReportingRequired) {
		Set<AviationAerCorsiaReviewGroup> deprecatedAerDataReviewGroups =
				AviationAerCorsiaReviewUtils.getDeprecatedAerDataReviewGroups(
						existingRequestPayload.getReportingRequired(), newIsReportingRequired);
		
		if (!deprecatedAerDataReviewGroups.isEmpty()) {
			existingRequestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedAerDataReviewGroups);
		}
	}

    private Set<AviationAerCorsiaReviewGroup> getDeprecatedAerDataReviewGroups(boolean existingIsReportingRequired,
    		boolean newIsReportingRequired) {

        Set<AviationAerCorsiaReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerCorsiaReviewGroup.getAerDataReviewGroups(existingIsReportingRequired));

        Set<AviationAerCorsiaReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerCorsiaReviewGroup.getAerDataReviewGroups(newIsReportingRequired));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }
    
    public void cleanUpDeprecatedVerificationDataReviewGroupDecisionsFromRequestPayload(AviationAerCorsiaRequestPayload requestPayload,
    		AviationAerCorsia newAer) {
		Set<AviationAerCorsiaReviewGroup> deprecatedVerificationDataReviewGroups =
				AviationAerCorsiaReviewUtils.getDeprecatedVerificationDataReviewGroups(requestPayload.getAer(), newAer);
		
		if (!deprecatedVerificationDataReviewGroups.isEmpty()) {
			requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedVerificationDataReviewGroups);
		}
	}

    private Set<AviationAerCorsiaReviewGroup> getDeprecatedVerificationDataReviewGroups(AviationAerCorsia existingAer,
    		AviationAerCorsia newAer) {

        Set<AviationAerCorsiaReviewGroup> existingReviewGroups =
            new HashSet<>(AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(existingAer));

        Set<AviationAerCorsiaReviewGroup> newReviewGroupsNeeded =
            new HashSet<>(AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(newAer));

        existingReviewGroups.removeAll(newReviewGroupsNeeded);

        return existingReviewGroups;
    }
    
    public void removeVerificationDataReviewGroupDecisionsFromRequestPayload(AviationAerCorsiaRequestPayload requestPayload) {
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> verificationDataReviewGroupDecisions = requestPayload.getReviewGroupDecisions().entrySet().stream()
                .filter(entry -> entry.getValue().getReviewDataType() == AerReviewDataType.VERIFICATION_REPORT_DATA)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestPayload.getReviewGroupDecisions()
                .keySet()
                .removeAll(verificationDataReviewGroupDecisions.keySet());
    }
}