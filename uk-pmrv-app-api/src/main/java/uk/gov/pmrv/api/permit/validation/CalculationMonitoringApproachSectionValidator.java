package uk.gov.pmrv.api.permit.validation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CalculationMonitoringApproachSectionValidator
    implements PermitSectionContextValidator<CalculationOfCO2MonitoringApproach>, PermitContextValidator, PermitGrantedContextValidator {

    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        MonitoringApproachType type = MonitoringApproachType.CALCULATION_CO2;

        return permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches().containsKey(type)
                ? validate((CalculationOfCO2MonitoringApproach) permitContainer.getPermit().getMonitoringApproaches()
                        .getMonitoringApproaches().get(type), permitContainer)
                : PermitValidationResult.validPermit();
    }

    @Override
    public PermitValidationResult validate(final @Valid CalculationOfCO2MonitoringApproach permitSection,
            PermitContainer permitContainer) {
        if (ObjectUtils.isEmpty(permitSection)) {
            return PermitValidationResult.validPermit();
        }
        final Permit permit = permitContainer.getPermit();

        List<PermitViolation> permitViolations = permitSection.getSourceStreamCategoryAppliedTiers().stream()
            .map(appliedTier -> Stream.of(

                // validate source stream
                    permitReferenceService.validateExistenceInPermit(
                    permit.getSourceStreamsIds(),
                    List.of(appliedTier.getSourceStreamCategory().getSourceStream()),
                    PermitReferenceService.Rule.SOURCE_STREAM_EXISTS),

                // validate emission sources
                    permitReferenceService.validateExistenceInPermit(
                    permit.getEmissionSourcesIds(),
                    appliedTier.getSourceStreamCategory().getEmissionSources(),
                    PermitReferenceService.Rule.EMISSION_SOURCES_EXIST),

                // validate measurement devices or methods in activity data
                permitReferenceService.validateExistenceInPermit(
                    permit.getMeasurementDevicesOrMethodsIds(),
                    appliedTier.getActivityData().getMeasurementDevicesOrMethods(),
                    PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST)

                ).filter(Optional::isPresent).flatMap(Optional::stream).collect(Collectors.toList())
            ).flatMap(List::stream).map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[] {};
                return new PermitViolation(CalculationOfCO2MonitoringApproach.class.getSimpleName(),
                    p.getLeft(),
                    data);
            }).collect(Collectors.toList());

        return PermitValidationResult.builder()
            .valid(permitViolations.isEmpty())
            .permitViolations(permitViolations)
            .build();
    }

}
