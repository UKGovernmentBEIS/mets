package uk.gov.pmrv.api.permit.validation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;

@Component
@RequiredArgsConstructor
public class MeasurementBiomassFractionValidator implements PermitContextValidator, PermitGrantedContextValidator {
    private static final String WASTE_FLAG_CONFIG_KEY = "ui.features.wastePermitEnabled";
    private final ConfigurationService configurationService;

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        PermitValidationResult valid = PermitValidationResult.builder().valid(true).build();

        boolean isWasteEnabled = configurationService.getConfigurationByKey(WASTE_FLAG_CONFIG_KEY)
            .map(ConfigurationDTO::getValue).filter(Boolean.class::isInstance).map(Boolean.class::cast)
            .orElse(false);
        if (!isWasteEnabled) {
            return valid;
        }

        PermitMonitoringApproachSection permitMonitoringApproachSection =
            permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches().get(
                MonitoringApproachType.MEASUREMENT_CO2);
        if (permitMonitoringApproachSection == null) {
            return valid;
        }

        MeasurementOfCO2MonitoringApproach measurementOfCO2MonitoringApproach = (MeasurementOfCO2MonitoringApproach) permitMonitoringApproachSection;
        List<MeasurementOfCO2EmissionPointCategoryAppliedTier> co2MonitoringApproaches =
            measurementOfCO2MonitoringApproach.getEmissionPointCategoryAppliedTiers().stream()
                .filter(tier -> tier.getBiomassFraction() == null).collect(Collectors.toList());

        if (co2MonitoringApproaches.size() > 0) {
            return PermitValidationResult.invalidPermit(
                List.of(new PermitViolation(PermitViolation.PermitViolationMessage.INVALID_CO2_MEASUREMENT_BIOMASS_FRACTION,
                    co2MonitoringApproaches)));
        }
       return valid;
    }


}

