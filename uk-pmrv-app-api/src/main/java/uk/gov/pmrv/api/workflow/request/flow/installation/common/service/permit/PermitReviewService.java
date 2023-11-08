package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.Determinateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadDecidableAndDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadGroupDecidable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitReviewGroupMonitoringApproachMapper;

@Service
@RequiredArgsConstructor
public class PermitReviewService {
	
	private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationAndDecisionsValidatorService;

	public void cleanUpDeprecatedReviewGroupDecisions(
			PermitPayloadGroupDecidable decidablePayload, Set<MonitoringApproachType> newMonitoringApproaches) {
		Set<MonitoringApproachType> removedMonitoringApproaches = new HashSet<>(
				decidablePayload.getPermit().getMonitoringApproaches().getMonitoringApproaches().keySet());
		removedMonitoringApproaches.removeAll(newMonitoringApproaches);
		
		if (!removedMonitoringApproaches.isEmpty()) {
			Set<PermitReviewGroup> deprecatedReviewGroups = removedMonitoringApproaches.stream()
					.map(PermitReviewGroupMonitoringApproachMapper::getPermitReviewGroupFromMonitoringApproach)
					.collect(Collectors.toSet());
			decidablePayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        } 
	}
	
	public <T extends Determinateable> void resetDeterminationIfNotDeemedWithdrawn(
			PermitPayloadDeterminateable<T> determinateablePayload) {
		if (determinateablePayload.getDetermination() != null
				&& DeterminationType.DEEMED_WITHDRAWN != determinateablePayload.getDetermination().getType()) {
			determinateablePayload.setDetermination(null);
		}
	}
	
	public <T extends PermitReviewDecision, S extends Determinateable> void resetDeterminationIfNotValidWithDecisions(
			PermitPayloadDecidableAndDeterminateable<T, S> payload, RequestType requestType) {
		if (payload.getDetermination() != null &&
        		!permitReviewDeterminationAndDecisionsValidatorService.isDeterminationAndDecisionsValid(payload.getDetermination(), payload, requestType)) {
			payload.setDetermination(null);
        }
	}
	
}
