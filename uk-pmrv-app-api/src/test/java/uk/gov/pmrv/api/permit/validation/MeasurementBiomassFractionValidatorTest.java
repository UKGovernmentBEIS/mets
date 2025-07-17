package uk.gov.pmrv.api.permit.validation;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MeasurementBiomassFractionValidatorTest {
    private static final String WASTE_FLAG_CONFIG_KEY = "ui.features.wastePermitEnabled";
    private static final Optional<ConfigurationDTO> configValueTrue = Optional.of(ConfigurationDTO.builder().key(WASTE_FLAG_CONFIG_KEY).value(true).build());
    private static final Optional<ConfigurationDTO> configValueFalse = Optional.of(ConfigurationDTO.builder().key(WASTE_FLAG_CONFIG_KEY).value(false).build());
    private static PermitContainer permitContainerWithBiomassFraction;
    private static PermitContainer permitContainerNoBiomassFraction;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private MeasurementBiomassFractionValidator validator;

    @BeforeAll
    static void setUp() {
        permitContainerWithBiomassFraction = getPermitContainer(true);
        permitContainerNoBiomassFraction = getPermitContainer(false);
    }

    @Test
    void testWasteFlagEnabled() {
        when(configurationService.getConfigurationByKey(WASTE_FLAG_CONFIG_KEY))
            .thenReturn(configValueTrue);
        assertTrue(validator.validate(permitContainerWithBiomassFraction).isValid());
        assertFalse(validator.validate(permitContainerNoBiomassFraction).isValid());
    }

    @Test
    void testWasteFlagDisabled() {
        when(configurationService.getConfigurationByKey(WASTE_FLAG_CONFIG_KEY))
            .thenReturn(configValueFalse);

        assertTrue(validator.validate(permitContainerWithBiomassFraction).isValid());
        assertTrue(validator.validate(permitContainerNoBiomassFraction).isValid());
    }

    private static PermitContainer getPermitContainer(boolean withBiomassFraction) {
        return PermitContainer.builder()
            .permit(Permit.builder()
                .monitoringApproaches(MonitoringApproaches.builder()
                    .monitoringApproaches(Map.of
                        (MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder()
                                .emissionPointCategoryAppliedTiers(
                                    List.of(MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                                        .biomassFraction(withBiomassFraction ? MeasurementBiomassFraction.builder().build() : null)
                                        .build()))
                                .build()
                            , MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder()
                                .build())
                    )
                    .build())
                .build())
            .build();
    }

}