package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;

@UtilityClass
public class PermitReviewGroupMonitoringApproachMapper {

    private static final ImmutableMap<MonitoringApproachType, PermitReviewGroup> MAP =
        ImmutableMap.<MonitoringApproachType, PermitReviewGroup>builder()
            .put(MonitoringApproachType.CALCULATION_CO2, PermitReviewGroup.CALCULATION_CO2)
            .put(MonitoringApproachType.MEASUREMENT_CO2, PermitReviewGroup.MEASUREMENT_CO2)
            .put(MonitoringApproachType.FALLBACK, PermitReviewGroup.FALLBACK)
            .put(MonitoringApproachType.MEASUREMENT_N2O, PermitReviewGroup.MEASUREMENT_N2O)
            .put(MonitoringApproachType.CALCULATION_PFC, PermitReviewGroup.CALCULATION_PFC)
            .put(MonitoringApproachType.INHERENT_CO2, PermitReviewGroup.INHERENT_CO2)
            .put(MonitoringApproachType.TRANSFERRED_CO2_N2O, PermitReviewGroup.TRANSFERRED_CO2_N2O)
            .build();

    public static PermitReviewGroup getPermitReviewGroupFromMonitoringApproach(final MonitoringApproachType type) {
        return MAP.get(type);
    }
}