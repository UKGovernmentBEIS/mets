package uk.gov.pmrv.api.workflow.request.flow.installation.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;

class PermitReviewUtilsTest {

	@Test
	void getPermitReviewGroups() {
		Permit permit = Permit.builder()
				.monitoringApproaches(MonitoringApproaches.builder()
						.monitoringApproaches(Map.of(
								MonitoringApproachType.FALLBACK, FallbackMonitoringApproach.builder().build(),
								MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build()
								))
						.build())
				.build();
		
		Set<PermitReviewGroup> result = PermitReviewUtils.getPermitReviewGroups(permit);
		
		Set<PermitReviewGroup> expectedResult = Stream
				.concat(Set.of(PermitReviewGroup.FALLBACK, 
						PermitReviewGroup.CALCULATION_CO2).stream(),
						PermitReviewGroup.getStandardReviewGroups().stream())
				.collect(Collectors.toSet());

		assertThat(result).containsExactlyInAnyOrderElementsOf(expectedResult);
	}
}
