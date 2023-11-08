package uk.gov.pmrv.api.permit.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class N2OMonitoringApproachSectionValidator implements
    PermitSectionContextValidator<MeasurementOfN2OMonitoringApproach>, PermitContextValidator, PermitGrantedContextValidator {

    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(PermitContainer permitContainer) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = permitContainer
            .getPermit().getMonitoringApproaches().getMonitoringApproaches();

        if (monitoringApproaches.containsKey(MonitoringApproachType.MEASUREMENT_N2O)) {
            return validate((MeasurementOfN2OMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT_N2O),
                permitContainer);
        }
        return PermitValidationResult.validPermit();
    }

    @Override
    public PermitValidationResult validate(MeasurementOfN2OMonitoringApproach permitSection, PermitContainer permitContainer) {
        if (ObjectUtils.isEmpty(permitSection)) {
            return PermitValidationResult.validPermit();
        }
        final Permit permit = permitContainer.getPermit();

        List<PermitViolation> permitViolations = permitSection.getEmissionPointCategoryAppliedTiers().stream()
            .map(appliedTier -> Stream.of(

                    // validate source streams
                    permitReferenceService.validateExistenceInPermit(
                        permit.getSourceStreamsIds(),
                        appliedTier.getEmissionPointCategory().getSourceStreams(),
                        PermitReferenceService.Rule.SOURCE_STREAM_EXISTS),

                    // validate emission sources
                    permitReferenceService.validateExistenceInPermit(
                        permit.getEmissionSourcesIds(),
                        appliedTier.getEmissionPointCategory().getEmissionSources(),
                        PermitReferenceService.Rule.EMISSION_SOURCES_EXIST),

                    // validate emission point
                    permitReferenceService.validateExistenceInPermit(
                        permit.getEmissionPointsIds(),
                        Collections.singletonList(appliedTier.getEmissionPointCategory().getEmissionPoint()),
                        PermitReferenceService.Rule.EMISSION_POINTS_EXIST),

                    // validate measurement devices or methods
                    permitReferenceService.validateExistenceInPermit(
                        permit.getMeasurementDevicesOrMethodsIds(),
                        appliedTier.getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                        PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST)
                ).filter(Optional::isPresent).flatMap(Optional::stream).collect(Collectors.toList())
            ).flatMap(List::stream).map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[]{};
                return new PermitViolation(MeasurementOfN2OMonitoringApproach.class.getSimpleName(),
                    p.getLeft(),
                    data);
            }).collect(Collectors.toList());

        return PermitValidationResult.builder()
            .valid(permitViolations.isEmpty())
            .permitViolations(permitViolations)
            .build();
    }

}
