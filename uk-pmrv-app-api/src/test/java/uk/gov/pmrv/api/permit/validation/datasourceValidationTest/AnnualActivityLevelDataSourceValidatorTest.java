package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevelType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.*;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.AnnualActivityLevelDataSourceValidator;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


@ExtendWith(MockitoExtension.class)
public class AnnualActivityLevelDataSourceValidatorTest {

    @InjectMocks
    private AnnualActivityLevelDataSourceValidator validator;

    @Test
    void validateDataSources_validHeatDataSource_shouldReturnTrue() {
        MeasurableHeatFlow first = new MeasurableHeatFlow();
        first.setNet(AnnexVIISection72.DOCUMENTATION);
        first.setQuantification(AnnexVIISection45.OTHER_METHODS);

        AnnualActivityHeatLevel data = new AnnualActivityHeatLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_HEAT);
        data.setMeasurableHeatFlowList(List.of(first));

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidHeatFirstDataSource_shouldReturnFalse() {
        MeasurableHeatFlow first = new MeasurableHeatFlow();
        // net and quantification are null

        AnnualActivityHeatLevel data = new AnnualActivityHeatLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_HEAT);
        data.setMeasurableHeatFlowList(List.of(first));

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidAdditionalHeatDataSource_shouldReturnFalse() {
        MeasurableHeatFlow first = new MeasurableHeatFlow();
        first.setNet(AnnexVIISection72.MEASUREMENTS);
        first.setQuantification(AnnexVIISection45.OPERATOR_CONTROL_DIRECT_READING_NOT_A);

        MeasurableHeatFlow second = new MeasurableHeatFlow(); // both null

        AnnualActivityHeatLevel data = new AnnualActivityHeatLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_HEAT);
        data.setMeasurableHeatFlowList(List.of(first, second));

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_validFuelDataSource_shouldReturnTrue() {
        ActivityFuelDataSource first = new ActivityFuelDataSource();
        first.setFuelInput(AnnexVIISection44.METHOD_MONITORING_PLAN);

        AnnualActivityFuelLevel data = new AnnualActivityFuelLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_FUEL);
        data.setFuelDataSources(List.of(first));

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidFirstFuelDataSource_shouldReturnFalse() {
        ActivityFuelDataSource first = new ActivityFuelDataSource();
        // fuelInput is null

        AnnualActivityFuelLevel data = new AnnualActivityFuelLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_FUEL);
        data.setFuelDataSources(List.of(first));

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidAdditionalFuelDataSource_shouldReturnFalse() {
        ActivityFuelDataSource first = new ActivityFuelDataSource();
        first.setFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL);

        ActivityFuelDataSource second = new ActivityFuelDataSource(); // all fields null

        AnnualActivityFuelLevel data = new AnnualActivityFuelLevel();
        data.setAnnualLevelType(AnnualLevelType.ACTIVITY_FUEL);
        data.setFuelDataSources(List.of(first, second));

        assertFalse(validator.validateDataSources(data));
    }

}
