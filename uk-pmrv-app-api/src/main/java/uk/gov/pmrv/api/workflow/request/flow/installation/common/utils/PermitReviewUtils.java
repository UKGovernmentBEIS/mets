package uk.gov.pmrv.api.workflow.request.flow.installation.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitReviewGroupMonitoringApproachMapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class PermitReviewUtils {

	public Set<PermitReviewGroup> getPermitReviewGroups(Permit permit){
		final Set<PermitReviewGroup> permitMonitoringReviewGroups = permit.getMonitoringApproaches().getMonitoringApproaches()
				.keySet().stream()
				.map(PermitReviewGroupMonitoringApproachMapper::getPermitReviewGroupFromMonitoringApproach)
				.collect(Collectors.toSet());

        return Stream.of(
        		PermitReviewGroup.getStandardReviewGroups(),
        		permitMonitoringReviewGroups)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
	}
}
